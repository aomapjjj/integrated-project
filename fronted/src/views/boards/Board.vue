<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { addBoard, getBoardItems } from '../../libs/fetchUtils.js'
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import BoardCard from '@/component/card/BoardCard.vue'
import LodingPage from '@/component/LodingPage.vue'

// ----------------------- Router -----------------------

const router = useRouter()

// ----------------------- List Items -----------------------

const boardsList = ref([])

// ----------------------- Enable & Disable -----------------------

const openModalName = ref(false)

// ----------------------- Stores -----------------------

const userStore = useUsers()

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
  console.log(result.status)

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
              Personal Board
            </h2>
          </template>

          <template #labelCollabBoard>
            <h2
              class="itbkk-collab-board text-xl font-bold p-4 mt-2 mb-2 items-center justify-center"
            >
              Collab Board
            </h2>
          </template>
        </BoardCard>

        <!------------------------- Modal ------------------------->
        <div
          v-if="openModalName"
          class="itbkk-modal-new fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50 py-10 lg:left-32"
        >
          <div
            class="max-h-full w-full max-w-md overflow-y-auto sm:rounded-2xl bg-white"
          >
            <div class="w-full">
              <div class="m-8 my-20 max-w-[400px] mx-auto">
                <div class="mb-8">
                  <span
                    class="block text-2xl font-bold leading-6 mb-1 customPurple text-center"
                    >New Board</span
                  >
                  <div class="modal-content py-4 text-left px-6 flex-grow">
                    <div class="label">
                      <span
                        class="block text-lg font-bold leading-6 text-gray-900 mb-1 ml-4"
                        >Title<span style="color: red"> *</span>
                      </span>
                      <!-- <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ status.name?.length }}/120
            </p> -->
                    </div>

                    <label
                      class="input input-bordered flex items-center gap-2 font-bold ml-4"
                    >
                      <input
                        type="text"
                        class="itbkk-board-name grow"
                        placeholder="Board Name"
                        v-model="userBoard.name"
                        maxlength="120"
                      />
                    </label>
                    <!-- <p class="text-sm text-gray-400 mb-2 mt-2" style="text-align: right">
              {{ status.name?.length }}/50
            </p> -->
                  </div>
                </div>
                <!-- <div class="space-y-4">
                  <button
                    class="itbkk-button-ok p-3 bg-black rounded-full text-white w-full font-semibold"
                  >
                    Save
                  </button>

                  <button
                    @click="openModalName = false"
                    class="itbkk-button-cancel p-3 bg-white border rounded-full w-full font-semibold"
                  >
                    Cancel
                  </button>
                </div> -->
                <!-- Buttons -->
                <div class="flex justify-center">
                  <form method="dialog">
                    <button
                      type="submit"
                      class="itbkk-button-ok btn flex-3 mr-5 bg-customPink"
                      @click="submitForm()"
                      :disabled="!isValidName"
                    >
                      Save
                    </button>
                  </form>
                  <router-link to="/board">
                    <button
                      @click="cancelAction"
                      class="itbkk-button-cancel btn"
                    >
                      Cancel
                    </button>
                  </router-link>
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
