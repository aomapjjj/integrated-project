<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { getItems, addBoard, deleteItemById , getBoardItems} from '../libs/fetchUtils.js'
import SideBar from '@/component/SideBar.vue'

const BoardsList = ref([])
const openModalName = ref(false)
const openModalToDelete = ref(false)
const selectedItemIdToDelete = ref()

const userStore = useUsers()
const userName = userStore.getUser().username
const userBoard = ref({ name: userName + ' personal board' })
// const userID = userStore.getUser()

const router = useRouter()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

function getToken() {
  return sessionStorage.getItem('access_token')
}
onMounted(async () => {
  const itemsBoards = await getBoardItems(baseUrlBoard)
  BoardsList.value = itemsBoards

  const token = getToken()
  const response = await fetch(`${import.meta.env.VITE_BASE_URL_MAIN}/boards`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })

  if (response.status === 404) {
    router.push({ name: 'ErrorPage' })
  } else if (response.status === 401) {
    router.push({ name: 'Login' })
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
    sessionStorage.removeItem('access_token')
    router.push({ name: 'Login' })
  } else {
    toBoardsList(result.data.id)
    clearForm()
  }
}

const deletBoard = async (boardId) => {
  await deleteItemById(baseUrlBoard, boardId)
  router.go()
}

const openModalToDeleteBoard = (itemId) => {
  selectedItemIdToDelete.value = itemId
  openModalToDelete.value = true
}

const confirmDelete = () => {
  deletBoard(selectedItemIdToDelete.value)
}

const clearForm = () => {
  userBoard.value.name = ''
}

const cancelAction = () => {
  clearForm()
  openModalName.value = false
}

const openModalCreate  = () => {
  openModalName.value = !openModalName.value
  router.push({name : 'BoardAdd'}) 
}

</script>

<template>
  <div class="min-h-full max-h-fit">
    <div class="min-h-screen flex">
      <!-- Sidebar -->
      <SideBar>
        {{ userBoard.name }}
      </SideBar>
      <!-- End Sidebar -->

      <!-- Main Content -->
      <div class="flex-1 flex flex-col">
        <!-- Navbar -->
        <nav class="bg-white shadow px-4 py-6 flex justify-center items-center">
          <div
            class="text-2xl font-bold tracking-tight"
            style="color: #9391e4; text-shadow: 0 0 5px #ffffff"
          >
            My Boards
          </div>
        </nav>

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

        <div
          class="p-4 overflow-y-auto h-screen max-h-screen md:h-[80vh] lg:h-[75vh] xl:h-[70vh]"
        >
          <!-- <h2 class="text-xl font-bold mb-4">Visited a while ago...</h2> -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div v-for="(item, index) in BoardsList" :key="index">
              <div
                class="overflow-hidden relative bg-transparent transition ease-in-out delay-100 hover:bg-gradient-to-l from-cyan-100 to-teal-50 w-full max-w-lg px-5 py-8 mx-auto bg-white rounded-lg hover:rotate-3 shadow-xl hover:bg-teal-50 hover:rounded-3xl"
              >
                <div class="max-w-md mx-auto space-y-6">
                  <div
                    class="animate-bounce absolute w-52 h-52 bg-teal-50 rounded-full -bottom-10 -right-10 -z-10"
                  ></div>
                  <div
                    class="animate-bounce absolute w-32 h-32 bg-cyan-100 rounded-full -top-10 -left-10 -z-10"
                  ></div>
                  <p class="text-md font-semibold text-center">
                    {{ item.name }}
                  </p>
                  <div class="flex justify-between items-center">
                    <div class="flex gap-2">
                      <!-- Edit -->
                      <button class="btn p-2 rounded-md customBgYellow">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="16"
                          height="16"
                          viewBox="0 0 24 24"
                        >
                          <g fill="none">
                            <path
                              d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                            />
                            <path
                              fill="currentColor"
                              d="M16.035 3.015a3 3 0 0 1 4.099-.135l.144.135l.707.707a3 3 0 0 1 .135 4.098l-.135.144L9.773 19.177a1.5 1.5 0 0 1-.562.354l-.162.047l-4.454 1.028a1.001 1.001 0 0 1-1.22-1.088l.02-.113l1.027-4.455a1.5 1.5 0 0 1 .29-.598l.111-.125zm-.707 3.535l-8.99 8.99l-.636 2.758l2.758-.637l8.99-8.99l-2.122-2.12Zm3.536-2.121a1 1 0 0 0-1.32-.083l-.094.083l-.708.707l2.122 2.121l.707-.707a1 1 0 0 0 .083-1.32l-.083-.094z"
                            />
                          </g>
                        </svg>
                      </button>
                      <!-- Delete -->
                      <button
                        class="btn p-2 bg-red-400 rounded-md"
                        @click="openModalToDeleteBoard(item.id)"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="16"
                          height="16"
                          viewBox="0 0 24 24"
                        >
                          <g fill="none" fill-rule="evenodd">
                            <path
                              d="M24 0v24H0V0zM12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036c-.01-.003-.019 0-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z"
                            />
                            <path
                              fill="white"
                              d="M14.28 2a2 2 0 0 1 1.897 1.368L16.72 5H20a1 1 0 1 1 0 2h-1v12a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V7H4a1 1 0 0 1 0-2h3.28l.543-1.632A2 2 0 0 1 9.721 2zM17 7H7v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1zm-2.72-3H9.72l-.333 1h5.226z"
                            />
                          </g>
                        </svg>
                      </button>
                      <button
                        @click="toBoardsList(item.id)"
                        class="bg-teal-400 px-4 py-2 text-sm rounded-md text-white hover:bg-teal-500"
                      >
                        Show More
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- delete modal for the selected item only -->
              <div
                v-if="openModalToDelete && selectedItemIdToDelete === item.id"
                class="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50"
              >
                <div
                  class="w-full max-w-lg p-3 relative mx-auto my-auto rounded-xl shadow-lg bg-white"
                >
                  <div>
                    <div
                      class="text-center p-3 flex-auto justify-center leading-6"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="w-16 h-16 flex items-center customRed mx-auto"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                          clip-rule="evenodd"
                        />
                      </svg>
                      <h2 class="text-2xl font-bold py-4">Are you sure?</h2>
                      <p class="text-md text-gray-500 px-8">
                        Do you really want to Delete your Board?
                      </p>
                    </div>
                    <div class="p-3 mt-2 text-center space-x-4 md:block">
                      <button
                        class="itbkk-button-ok customRed mb-2 md:mb-0 bg-white px-5 py-2 text-sm shadow-sm font-medium tracking-wider border text-gray-600 rounded-md hover:shadow-lg hover:bg-gray-100"
                        @click="confirmDelete()"
                      >
                        Delete
                      </button>
                      <button
                        @click="openModalToDelete = false"
                        class="itbkk-button-cancel mb-2 md:mb-0 bg-gray-500 border border-gray-500 px-5 py-2 text-sm shadow-sm font-medium tracking-wider text-white rounded-md hover:shadow-lg hover:bg-gray-600"
                      >
                        Close
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

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
