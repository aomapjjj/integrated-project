<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useUsers } from '../stores/storeUser'
import { useBoard } from '../stores/storeBoard'
import { deleteItemById, getBoardItems } from '../libs/fetchUtils.js'

const BoardsList = ref([])
const openModalToDelete = ref(false)
const selectedItemIdToDelete = ref()
const userStore = useUsers()
const userName = userStore.getUser().username
const boardStore = useBoard()

const currentColor = ref({})
const colorCard = ref(null)

const router = useRouter()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

function getToken() {
  return localStorage.getItem('access_token')
}

onMounted(async () => {
  const itemsBoards = await getBoardItems(baseUrlBoard)
  BoardsList.value = itemsBoards.boards
  console.log(BoardsList.value)
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

  const savedColors = JSON.parse(localStorage.getItem('boardColors'))
  if (savedColors) {
    currentColor.value = savedColors
  }

  const savedColor = boardStore.getChangeColor()
  if (savedColor) {
    currentColor.value = savedColor
  }
})

const getBoardsByOwner = (boardsList, userName) => {
  return boardsList?.filter((board) => board.owner.name === userName)
}
const getBoardsCollabsByOwner = (boardsList, userName) => {
  return boardsList?.filter((board) => board.owner.name !== userName)
}

const toBoardsList = (boardId) => {
  if (boardId !== null) {
    router.push({ name: 'TaskList', params: { id: boardId } })
    userStore.setBoard(boardId)
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

// Color
const setColor = (color, id) => {
  currentColor.value = {
    ...currentColor.value,
    [id]: color
  }
  localStorage.setItem('boardColors', JSON.stringify(currentColor.value))
  boardStore.setChangeColor(currentColor.value)
}
</script>

<template>
  <div
    class="p-4 overflow-y-auto h-screen max-h-screen md:h-[80vh] lg:h-[75vh] xl:h-[70vh]"
  >
    <!-- Personal Boards -->
    <slot name="labelPersonalBoard"></slot>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="(item, index) in getBoardsByOwner(BoardsList, userName)"
        :key="index"
      >
        <div class="p-3">
          <div
            ref="colorCard"
            :class="`${currentColor[item.id]}`"
            class="itbkk-personal-item max-w-xs py-4 container rounded-xl shadow-lg transform transition duration-500 hover:scale-105 hover:shadow-2xl"
          >
            <div>
              <span
                class="itbkk-board-visibility text-white text-xs font-bold rounded-full inline-block ml-4 py-1.5 px-4 cursor-pointer"
                :class="{
                  'bg-green-400': item.visibility === 'PUBLIC',
                  'bg-gray-400': item.visibility === 'PRIVATE'
                }"
                >{{
                  item.visibility.charAt(0).toUpperCase() +
                  item.visibility.slice(1).toLowerCase()
                }}</span
              >
              <h1
                class="itbkk-board-name text-l px-6 py-2 font-bold text-gray-800 cursor-pointer hover:text-gray-900 transition duration-100"
              >
                {{ item.name }}
              </h1>
              <div class="px-6 py-2">
                <span
                  @click="setColor('bg-gray-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-gray-400 p-1 text-xs font-bold mr-3"
                ></span>
                <span
                  @click="setColor('bg-purple-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-purple-400 p-1 text-xs font-bold mr-3"
                ></span>
                <span
                  @click="setColor('bg-indigo-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-indigo-400 p-1 text-xs font-bold mr-3"
                ></span>
                <span
                  @click="setColor('bg-blue-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-blue-400 p-1 text-xs font-bold mr-3"
                ></span>
                <span
                  @click="setColor('bg-yellow-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-yellow-400 p-1 text-xs font-bold mr-3"
                ></span>
                <span
                  @click="setColor('bg-red-300', item.id)"
                  class="inline-block rounded-full bg-white border-4 border-red-400 p-1 text-xs font-bold mr-3"
                ></span>
              </div>
            </div>
            <div class="flex px-4 py-2 justify-between">
              <div class="flex items-center space-x-2">
                <div class="flex gap-2">
                  <button class="btn rounded-full customBgYellow">
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

                  <button
                    class="btn bg-red-400 rounded-full"
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
                    class="flex select-none items-center gap-2 rounded-lg px-6 text-center align-middle font-sans text-xs font-bold uppercase text-pink-400 transition-all hover:text-pink-600 disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none"
                    type="button"
                  >
                    Show More
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-8 w-8"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        d="M9.879 17.243a1 1 0 0 1-.707-1.707L12.707 12L9.172 8.464a1 1 0 0 1 1.414-1.414l4.242 4.243a1 1 0 0 1 0 1.414l-4.242 4.243a1 1 0 0 1-.707.293"
                      />
                    </svg>
                  </button>
                </div>
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
              <div class="text-center p-3 flex-auto justify-center leading-6">
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

    <!-- Collab Boards -->
    <slot name="labelCollabBoard"></slot>
    <div class="p-3">
      <div
        v-for="(item, index) in getBoardsCollabsByOwner(BoardsList, userName)"
        :key="index"
      >
        <ul
          role="list"
          class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3"
        >
          <li
            class="itbkk-collab-item col-span-1 divide-y divide-gray-200 rounded-lg bg-white shadow"
          >
            <div class="flex w-full items-center justify-between space-x-6 p-6">
              <div class="flex-1 truncate">
                <div class="flex items-center space-x-3">
                  <h3
                    class="itbkk-board-name truncate text-sm font-medium text-gray-900"
                  >
                    {{ item.name }}
                  </h3>
                  <span
                    class="itbkk-access-right inline-flex flex-shrink-0 items-center rounded-full bg-green-50 px-1.5 py-0.5 text-xs font-medium text-blue-600 ring-1 ring-inset ring-green-600/20"
                  >
                    Read</span
                  >
                </div>
                <p class="itbkk-owner-name mt-1 truncate text-sm text-gray-500">
                  {{ item.owner.name }}
                </p>
              </div>
              <img
                class="h-10 w-10 flex-shrink-0 rounded-full bg-gray-300"
                src="https://qph.cf2.quoracdn.net/main-thumb-554097988-200-xietklpojlcioqxaqgcyykzfxblvoqrb.jpeg"
                alt=""
              />
            </div>
            <div>
              <div class="-mt-px flex divide-x divide-gray-200">
                <div class="flex w-0 flex-1">
                  <span
                    @click="toBoardsList(item.id)"
                    class="relative -mr-px inline-flex w-0 flex-1 items-center justify-center gap-x-3 rounded-bl-lg border border-transparent py-4 text-sm font-semibold text-gray-900 cursor-pointer"
                  >
                    View
                  </span>
                </div>
                <div class="-ml-px flex w-0 flex-1">
                  <a
                    href="tel:+1-202-555-0170"
                    class="relative inline-flex w-0 flex-1 items-center justify-center gap-x-3 rounded-br-lg border border-transparent py-4 text-sm font-semibold text-gray-900"
                  >
                    <span class="itbkk-leave-board">Leave</span>
                  </a>
                </div>
              </div>
            </div>
          </li>
        </ul>

        <div
          v-if="openModalToDelete && selectedItemIdToDelete === item.id"
          class="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50"
        >
          <div
            class="w-full max-w-lg p-3 relative mx-auto my-auto rounded-xl shadow-lg bg-white"
          >
            <div>
              <div class="text-center p-3 flex-auto justify-center leading-6">
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
