<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { getItems, addBoard, deleteItemById } from '../libs/fetchUtils.js'

const BoardsList = ref([])
const openModalName = ref(false)
const openModalToDelete = ref(false)
const sidebarTasks = ref(true)
const selectedItemIdToDelete = ref()
const toggleSidebar = () => {
  sidebarTasks.value = !sidebarTasks.value
}
const userStore = useUsers()
const userName = userStore.getUser().username
const userBoard = ref({ name: userName + ' personal Board' })
// const userID = userStore.getUser()

console.log('userStore.getUser()', userStore.getUser())

const router = useRouter()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

onMounted(async () => {
  const itemsBoards = await getItems(baseUrlBoard)
  BoardsList.value = itemsBoards
  console.log(BoardsList)
})

const toBoardsList = (boardId) => {
  if (boardId !== null) {
    router.push({ name: 'TaskList', params: { id: boardId } })
    userStore.setBoard(boardId)
  }
}

// ----------------------- Validate -----------------------

const isValidName = computed(() => {
  return userBoard.value.name.length > 0 && userBoard.value.name.length <= 50
})

const submitForm = async () => {
  const newBoard = await addBoard(baseUrlBoard, userBoard.value)
  toBoardsList(newBoard.id)
  clearForm()
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

const clearToken = () => {
  localStorage.removeItem('token')
  router.push({ name: 'Login' })
}
</script>

<template>
  <div class="min-h-full max-h-fit">
    <div class="min-h-screen flex">
      <!-- Sidebar -->
      <div
        id="sidebar"
        class="hs-overlay fixed top-0 left-0 bottom-0 z-40 w-64 bg-white border-e border-gray-200 pt-7 overflow-y-auto transition-transform duration-300 transform -translate-x-full lg:translate-x-0 lg:relative lg:flex lg:flex-col lg:h-screen"
        role="dialog"
        tabindex="-1"
        aria-label="Sidebar"
      >
        <div class="px-6">
          <img src="/src/image/sj3.png" alt="LOGO" class="w-24 h-24 mx-auto" />
        </div>

        <nav
          class="hs-accordion-group p-6 w-full flex-grow overflow-y-auto"
          data-hs-accordion-always-open
        >
          <ul class="space-y-1.5">
            <li>
              <a
                class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-300"
                href="#"
              >
                <svg
                  class="size-4"
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="#9391E4"
                    d="m21.743 12.331l-9-10c-.379-.422-1.107-.422-1.486 0l-9 10a1 1 0 0 0-.17 1.076c.16.361.518.593.913.593h2v7a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-4h4v4a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-7h2a.998.998 0 0 0 .743-1.669"
                  />
                </svg>
                <router-link to="/board/home"
                  ><span class="itbkk-home">Home</span></router-link
                >
              </a>
            </li>
            <li class="hs-accordion" id="projects-accordion">
              <button
                type="button"
                class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300"
                aria-expanded="true"
                aria-controls="projects-accordion"
              >
                <svg
                  class="size-4"
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="#9391E4"
                    d="M20 4H4c-1.103 0-2 .897-2 2v10c0 1.103.897 2 2 2h4l-1.8 2.4l1.6 1.2l2.7-3.6h3l2.7 3.6l1.6-1.2L16 18h4c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2M5 13h4v2H5z"
                  />
                </svg>
                All boards
                <svg
                  class="hs-accordion-active:hidden ms-auto block size-4 text-gray-600 group-hover:text-gray-500 dark:text-neutral-400"
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="m6 9 6 6 6-6" />
                </svg>
              </button>

              <div
                id="projects-accordion"
                class="hs-accordion-content w-full overflow-hidden transition-[height] duration-300 hidden"
                role="region"
                aria-labelledby="projects-accordion"
              >
                <ul class="pt-2 ps-2">
                  <li>
                    <a
                      class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hover:text-neutral-300 dark:focus:text-neutral-300"
                      href="#"
                    >
                      Link 1
                    </a>
                  </li>
                  <li>
                    <a
                      class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hover:text-neutral-300 dark:focus:text-neutral-300"
                      href="#"
                    >
                      Link 2
                    </a>
                  </li>
                  <li>
                    <a
                      class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hover:text-neutral-300 dark:focus:text-neutral-300"
                      href="#"
                    >
                      Link 3
                    </a>
                  </li>
                </ul>
              </div>
            </li>
          </ul>
        </nav>

        <div
          class="mt-auto p-4 bg-gray-100 dark:bg-neutral-800 border-t border-gray-200 dark:border-neutral-700"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <!-- User Icon -->
              <!-- <img src="/path/to/user-icon.png" alt="User Avatar" class="w-10 h-10 rounded-full"> -->
              <div class="avatar placeholder">
                <div
                  class="bg-neutral text-neutral-content w-10 h-10 rounded-full"
                ></div>
              </div>
              <!-- User Info -->
              <div>
                <p class="text-xs text-gray-500 dark:text-neutral-400">
                  Welcome,
                </p>
                <p
                  class="itbkk-fullname text-sm font-medium text-gray-800 dark:text-white"
                >
                  {{ userName }}
                </p>
              </div>
            </div>
            <!-- Log out -->
            <div class="mt-2">
              <button class="group">
                <svg
                  @click="clearToken()"
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                >
                  <path
        fill="none"
        stroke="#eb4343"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="1.5"
        d="M15 17.625c-.074 1.852-1.617 3.424-3.684 3.374c-.481-.012-1.076-.18-2.265-.515c-2.861-.807-5.345-2.164-5.941-5.203C3 14.724 3 14.095 3 12.837v-1.674c0-1.257 0-1.886.11-2.445c.596-3.038 3.08-4.395 5.941-5.202c1.19-.335 1.784-.503 2.265-.515c2.067-.05 3.61 1.522 3.684 3.374M21 12H10m11 0c0-.7-1.994-2.008-2.5-2.5M21 12c0 .7-1.994 2.008-2.5 2.5"
        class="group-hover:stroke-red-400"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
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
          <button @click="openModalName = !openModalName">
            <router-link to="/board/add">
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
            </router-link>
          </button>
        </div>

        <div class="p-6">
          <!-- <h2 class="text-xl font-bold mb-4">Visited a while ago...</h2> -->
          <div class="grid grid-cols-4 gap-6">
            <div v-for="(item, index) in BoardsList" :key="index">
              <div class="bg-white rounded-lg shadow p-4">
                <div class="relative">
                  <div
                    class="bg-gray-100 h-32 w-full rounded-md mb-4 flex items-center justify-center"
                  ></div>

                  <div
                    class="dropdown absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                  >
                    <div tabindex="0" role="button" class="m-1">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="24"
                        height="24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      >
                        <path
                          d="M5 12H5.01M12 12H12.01M19 12H19.01M6 12C6 12.5523 5.55228 13 5 13C4.44772 13 4 12.5523 4 12C4 11.4477 4.44772 11 5 11C5.55228 11 6 11.4477 6 12ZM13 12C13 12.5523 12.5523 13 12 13C11.4477 13 11 12.5523 11 12C11 11.4477 11.4477 11 12 11C12.5523 11 13 11.4477 13 12ZM20 12C20 12.5523 19.5523 13 19 13C18.4477 13 18 12.5523 18 12C18 11.4477 18.4477 11 19 11C19.5523 11 20 11.4477 20 12Z"
                        />
                      </svg>
                    </div>

                    <ul
                      tabindex="0"
                      class="dropdown-content menu bg-base-100 rounded-box z-[1] w-40 p-2 shadow"
                    >
                      <li><a>Edit</a></li>
                      <li
                        @click="openModalToDeleteBoard(item.id)"
                        class="customRed"
                      >
                        <a>Delete</a>
                      </li>
                    </ul>
                  </div>
                </div>
                <div @click="toBoardsList(item.id)">
                  <p class="text-md font-semibold text-center">
                    {{ item.name }}
                  </p>
                </div>
              </div>

              <!-- delete modal for the selected item only -->

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
          class="fixed left-32 top-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50 py-10"
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
                        >Title
                      </span>
                    </div>

                    <label
                      class="input input-bordered flex items-center gap-2 font-bold ml-4"
                    >
                      <input
                        type="text"
                        class="itbkk-board-name grow"
                        placeholder="Board Name"
                        v-model="userBoard.name"
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
                      class="itbkk-button-ok btn flex-3 mr-2 bg-customPink"
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
