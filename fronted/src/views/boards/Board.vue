<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { addBoard, getBoardItems , editBoard} from '../../libs/fetchUtils.js'
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import BoardCard from '@/component/card/BoardCard.vue'
import LodingPage from '@/component/others/LodingPage.vue'
import { useBoard } from '@/stores/storeBoard.js'

// ----------------------- Router -----------------------

const router = useRouter()

// ----------------------- List Items -----------------------

const boardsList = ref([])

// ----------------------- Enable & Disable -----------------------

const openModalName = ref(false)

// ----------------------- Stores -----------------------

const userStore = useUsers()
const boardStore = useBoard()

// ----------------------- Params -----------------------

const userName = userStore.getUser().username
const userBoard = ref({ name: userName + ' personal board' })
const isLoading = ref(true)

// ----------------------- BaseUrl -----------------------

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

function getToken() {
  return localStorage.getItem('access_token')
}

onMounted(async () => {
  try {
    const itemsBoards = await getBoardItems(baseUrlBoard)
    boardsList.value = itemsBoards.boards

    const token = getToken()
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL_MAIN}/boards`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    )

    if (response.status === 404) {
      router.push({ name: 'ErrorPage' })
    } else if (response.status === 401) {
      router.push({ name: 'Login' })
    }
  } catch (error) {
    console.error('Error loading boards:', error)
  } finally {
    isLoading.value = false
  }
})

const toBoardsList = (boardId) => {
  if (boardId !== null) {
    router.push({ name: 'TaskList', params: { id: boardId } })
    userStore.setBoard(boardId)
  }
}

// ----------------------- Validate -----------------------

const isValidName = computed(() => {
  return (
    userBoard.value.name.trim().length > 0 &&
    userBoard.value.name.trim().length <= 120
  )
})

const submitForm = async () => {
  const result = await addBoard(baseUrlBoard, userBoard.value)
  if (result.data) {
    boardStore.addNewBoard(result.data)
  }

  const items = await getBoardItems(baseUrlBoard)
  items.boards.sort((a, b) => new Date(a.createdOn) - new Date(b.createdOn)) //sort by createdOn
  boardStore.setBoards(items.boards)

  if (result.status === 401) {
    localStorage.removeItem('access_token')
    router.push({ name: 'Login' })
  } else {
    toBoardsList(result.data.id)
    clearForm()
  }
}

const clearForm = () => {
  userBoard.value.name = ''
}

const cancelAction = () => {
  clearForm()
  openModalName.value = false
}

const openModalCreate = () => {
  openModalName.value = !openModalName.value
  router.push({ name: 'BoardAdd' })
}
</script>

<template>
  <div v-if="isLoading">
    <LodingPage />
  </div>

  <div v-else class="min-h-full max-h-fit">
    <div class="min-h-screen flex">
      <!-- Sidebar -->
      <SideBar>
        {{ userBoard.name }}
      </SideBar>
      <!-- End Sidebar -->

      <!-- Main Content -->
      <div class="flex-1 flex flex-col">
        <!-- Navbar -->
        <Navbar> My Boards </Navbar>

        <!------------------------- Create Board ------------------------->
        <div class="bg-gray-200 w-auto h-auto">
          <button @click="openModalCreate">
            <div
              class="w-30 h-20 p-6 bg-white border border-gray-200 rounded-md shadow-md max-w-[13rem] fourth ml-6 mt-6 mb-6"
            >
              <div class="flex flex-col items-center relative">
                <svg
                  class="-mt-2"
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                >
                  <defs>
                    <mask id="letsIconsAddSquareDuotoneLine0">
                      <g fill="none">
                        <path
                          stroke="silver"
                          stroke-opacity=".25"
                          d="M3.5 11c0-1.9.001-3.274.142-4.322c.139-1.034.406-1.675.883-2.153c.478-.477 1.119-.744 2.153-.883C7.726 3.502 9.1 3.5 11 3.5h2c1.9 0 3.274.001 4.323.142c1.033.139 1.674.406 2.152.883c.477.478.744 1.119.883 2.153c.14 1.048.142 2.422.142 4.322v2c0 1.9-.001 3.274-.142 4.323c-.139 1.033-.406 1.674-.883 2.152c-.478.477-1.119.744-2.152.883c-1.049.14-2.423.142-4.323.142h-2c-1.9 0-3.274-.001-4.322-.142c-1.034-.139-1.675-.406-2.153-.883c-.477-.478-.744-1.119-.883-2.152C3.502 16.274 3.5 14.9 3.5 13z"
                        />
                        <path
                          stroke="#fff"
                          stroke-linejoin="round"
                          d="M12 8v8m4-4H8"
                        />
                      </g>
                    </mask>
                  </defs>
                  <path
                    fill="#000000"
                    d="M0 0h24v24H0z"
                    mask="url(#letsIconsAddSquareDuotoneLine0)"
                  />
                </svg>
                <span class="itbkk-button-create mt-1 text-sm"
                  >Create Board</span
                >
              </div>
            </div>
          </button>
        </div>

        <!-- Board Card -->
        <BoardCard>
          <template #labelPersonalBoard>
            <h2
              class="itbkk-personal-board text-xl font-bold mb-2 p-4 items-center justify-center"
            >
              Personal Boards
            </h2>
          </template>

          <template #labelCollabBoard>
            <h2
              class="itbkk-collab-board text-xl font-bold p-4 mt-2 mb-2 items-center justify-center"
            >
              Collab Boards
            </h2>
          </template>
        </BoardCard>

        <!------------------------- Modal ------------------------->
        <div
          v-if="openModalName"
          class="itbkk-modal-new fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 transition-opacity duration-300"
        >
          <div
            class="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4 px-6 py-4 flex flex-col gap-4 transform scale-95 transition-all duration-300 ease-in-out"
          >
            <div>
              <div class="m-6 my-12 max-w-[400px] mx-auto">
                <div class="text-center mb-4">
                  <h2 class="text-2xl font-bold customPurple">Add New Board</h2>
                  <p class="text-gray-500 text-sm mt-1">
                    Make your board special < 3
                  </p>
                </div>
                <div class="mb-6">
                  <label
                    for="boardName"
                    class="block text-base font-bold mb-2 px-2"
                  >
                    Board Name <span class="text-red-500">*</span>
                  </label>
                  <input
                    id="boardName"
                    type="text"
                    placeholder="Board Name"
                    v-model="userBoard.name"
                    maxlength="120"
                    class="w-full px-4 py-3 border border-gray-300 rounded-full text-gray-700 focus:ring-2 focus:ring-customPurple focus:outline-none transition duration-200"
                  />
                  <div class="flex justify-end items-center mx-2 mt-1">
                    <p class="text-sm text-gray-500">
                      {{ userBoard.name.length }}/120
                    </p>
                  </div>
                </div>
                <div
                  class="flex justify-center gap-4 px-4 py-4 border-t border-gray-200"
                >
                  <router-link to="/board">
                    <button
                      @click="cancelAction"
                      class="itbkk-button-cancel py-3 px-6 rounded-full border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all duration-200"
                    >
                      Cancel
                    </button>
                  </router-link>
                  <form method="dialog">
                    <button
                      type="submit"
                      class="itbkk-button-ok btn py-3 px-7 rounded-full bg-customPink text-white hover:bg-[#fa619c] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                      @click="submitForm()"
                      :disabled="!isValidName"
                    >
                      Save
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>        
      </div>
    </div>
  </div>
</template>

<style scoped>
.customPink {
  color: #f785b1;
}

.customPurple {
  color: #9391e4;
}

.customBlue {
  color: #9fc3e9;
}

.customRed {
  color: #eb4343;
}

.customBgYellow {
  background-color: #fae59d;
}

.fourth {
  border-color: #b7b7b7;
  background-image: -webkit-linear-gradient(
    45deg,
    #9fc3e9 50%,
    transparent 50%
  );
  background-image: linear-gradient(45deg, #9fc3e9 50%, transparent 50%);
  background-position: 100%;
  background-size: 400%;
  -webkit-transition: 300ms ease-in-out;
  transition: 300ms ease-in-out;
}

.fourth:hover {
  background-position: 0;
}
</style>
