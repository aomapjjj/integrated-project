<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useUsers } from '../../stores/storeUser'
import { useBoard } from '../../stores/storeBoard'
import {
  deleteItemById,
  getBoardItems,
  deleteCollaborator
} from '../../libs/fetchUtils.js'
import Alert from '../alert/Alert.vue'
import ConfirmModal from '../modal/ConfirmModal.vue'
import EmptyBoard from './EmptyBoard.vue'
import PersonalAndCollabBoard from '../board/PersonalAndCollabBoard.vue'

// ----------------------- Router -----------------------

const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()
const boardStore = useBoard()
const {
  addNewBoard,
  addNewBoards,
  removeBoard,
  resetBoard,
  addNewCollab,
  addNewCollabs,
  editCollab,
  removeCollabs,
  resetCollabs
} = useBoard()

// ----------------------- List Items -----------------------

const boardsList = ref([])
const currentColor = ref({})

// ----------------------- Params -----------------------

const selectedItemIdToDelete = ref()
const colorCard = ref(null)
const oidCollaboratorToRemove = ref()
const boardIdCollabs = ref()
const userName = userStore.getUser().username
const boardsOwner = ref()

const boardsCollab = ref()
const isPending = ref(false)

boardsOwner.value = boardStore.getBoards()
boardsCollab.value = boardStore.getCollabs()

console.log(boardStore.getBoards())

// ----------------------- Enable & Disable -----------------------

const openModalToDelete = ref(false)
const showConfirmModal = ref(false)
const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref('')

// ----------------------- BaseUrl -----------------------

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

const showRemoveModal = (boardId) => {
  boardIdCollabs.value = boardId
  showConfirmModal.value = true
  oidCollaboratorToRemove.value = userStore.getUserInfo().oid
}

const hideAlert = () => {
  isAlertFailure.value = false
  isAlertSuccess.value = false
}

const confirmRemove = async () => {
  if (oidCollaboratorToRemove.value) {
    try {
      const status = await deleteCollaborator(
        boardIdCollabs.value,
        oidCollaboratorToRemove.value
      )
      if (status === 200) {
        collaboratorInfo.value = collaboratorInfo.value.filter(
          (collab) => collab.id !== oidCollaboratorToRemove.value
        )
        boardStore.removeBoard(boardIdCollabs.value)
        
        isAlertSuccess.value = true
        alertMessage.value = 'Collaborator removed successfully'
        setTimeout(hideAlert, 3000)
      } else {
        isAlertFailure.value = true
        alertMessage.value = 'Failed to remove collaborator'
        setTimeout(hideAlert, 3000)
      }
    } catch (error) {
      alertMessage.value = 'Error removing collaborator:'
      setTimeout(hideAlert, 3000)
    } finally {
      showConfirmModal.value = false
      oidCollaboratorToRemove.value = null
    }
  }
}

function getToken() {
  return localStorage.getItem('access_token')
}

onMounted(async () => {
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
  const itemsBoards = await getBoardItems(baseUrlBoard)
  boardsCollab.value = itemsBoards.collabs

  const savedColors = JSON.parse(localStorage.getItem('boardColors'))
  if (savedColors) {
    currentColor.value = savedColors
  }

  const savedColor = boardStore.getChangeColor()
  if (savedColor) {
    currentColor.value = savedColor
  }
})

// const filterCollaboratorsByAccessRightAndName = (boardId, name) => {
//   const board = boardLo.find((b) => b.id === boardId);
//   if (!board) {
//     console.error("Board not found");
//     return [];
//   }

//   if (!board.collaborators) {
//     console.error("Collaborators not found in the specified board");
//     return [];
//   }

//   return board.collaborators
//     .filter((collab) => collab.name === name)
//     .map((collab) => ({ name: collab.name, accessRight: collab.accessRight }));
// }

const toBoardsList = (board) => {
  if (board.id !== null) {
    router.push({ name: 'TaskList', params: { id: board.id } })
    userStore.setBoard(board.id)
  }
}

const toBoardsInvitations = (board) => {
  if (board.id !== null) {
    router.push({ name: 'Invitations', params: { id: board.id } })
    userStore.setBoard(board.id)
  }
}

const deletBoard = async (boardId) => {
 const deletBoard =  await deleteItemById(baseUrlBoard, boardId)
 console.log(boardId)
 if(deletBoard === 200){
  removeBoard(boardId)
  openModalToDelete.value = false
  console.log(boardStore.getBoards())
  boardsOwner.value = boardStore.getBoards()
 }

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

const getAccessRight = (boardId, username) => {
  const board = boardsCollab.value.find((board) => board.id === boardId)

  if (board) {
    const collaborator = board.collaborators.find(
      (collab) => collab.name === username
    )

    return collaborator ? collaborator.accessRight : null
  }

  return null
}

const getCollaboratorStatus = (collaborators, name, status) => {
  for (let collab of collaborators) {
    if (
      collab.name === name + ' (Pending Invite)' &&
      collab.status === status
    ) {
      return true
    }
  }
  return false
}
</script>

<template>
  <Alert :isAlertFailure="isAlertFailure" :isAlertSuccess="isAlertSuccess">
    {{ alertMessage }}
  </Alert>
  <div
    class="p-4 overflow-y-auto h-screen max-h-screen md:h-[80vh] lg:h-[75vh] xl:h-[70vh]"
  >
    <!-- Personal Boards -->
    <slot name="labelPersonalBoard"></slot>
    <div v-if="boardsOwner?.length === 0">
      <EmptyBoard />
    </div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="(item, index) in boardsOwner" :key="index">
        <div
          ref="colorCard"
          :class="`${currentColor[item.id]}`"
          class="itbkk-personal-item max-w-xs py-4 border container rounded-xl shadow-lg transform transition duration-500 hover:scale-105 hover:shadow-2xl"
        >
          <PersonalAndCollabBoard>
            <template #toggle>
              <span
                class="itbkk-board-visibility text-white text-xs font-bold rounded-full inline-block ml-4 py-1.5 px-4 cursor-pointer"
                :class="{
                  'bg-green-400': item.visibility === 'PUBLIC',
                  'bg-gray-400': item.visibility === 'PRIVATE'
                }"
                >{{
                  item?.visibility?.charAt(0).toUpperCase() +
                  item?.visibility?.slice(1).toLowerCase()
                }}</span
              >
            </template>
            <template #name>
              <h1
                class="itbkk-board-name text-l px-6 py-2 font-bold text-gray-800 cursor-pointer hover:text-gray-900 transition duration-100"
              >
                {{ item.name }}
              </h1>
            </template>
            <template #colorBoard>
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
            </template>
            <template #Btn>
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
            </template>
            <template #deleteBtn>
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
            </template>
            <template #viewBtn>
              <button
                @click="toBoardsList(item)"
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
            </template>
          </PersonalAndCollabBoard>
        </div>
        <ConfirmModal
          :openModal="openModalToDelete && selectedItemIdToDelete === item.id"
          @confirm="confirmDelete()"
          @cancel="openModalToDelete = false"
        >
          <template #svg>
            <div
              class="mx-auto flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
            >
              <svg
                class="h-6 w-6 text-red-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                aria-hidden="true"
                data-slot="icon"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"
                />
              </svg>
            </div>
          </template>
          <template #headerMessage> Delete Board </template>
          <template #message>
            <p class="text-sm text-gray-500">
              Do you really want to Delete your Board?
            </p>
          </template>
          <template #confirmBtn>
            <span
              class="inline-flex w-full justify-center rounded-md bg-red-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-300 sm:ml-3 sm:w-auto"
              >Delete</span
            >
          </template>
          <template #cancelBtn>
            <span
              class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
              >Cancel</span
            >
          </template>
        </ConfirmModal>
      </div>
    </div>

    <!-- Collab Boards -->
    <slot name="labelCollabBoard"></slot>
    <div v-if="boardsCollab?.length === 0">
      <EmptyBoard />
    </div>
    <div
      v-if="boardsCollab"
      class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
    >
      <div v-for="(board, index) in boardsCollab" :key="board.id">
        <div
          ref="colorCard"
          :class="`${currentColor[board.id]}`"
          class="itbkk-collab-item max-w-xs py-4 border container rounded-xl shadow-lg transform transition duration-500 hover:scale-105 hover:shadow-2xl"
        >
          <PersonalAndCollabBoard
            :is-show="
              getCollaboratorStatus(
                boardsCollab[index].collaborators,
                userName,
                'PENDING'
              )
            "
          >
            <template #toggle>
              <span
                v-if="
                  getCollaboratorStatus(
                    board.collaborators,
                    userName,
                    'PENDING'
                  )
                "
                class="itbkk-access-right text-white text-xs font-bold rounded-full inline-block ml-4 py-1.5 px-4 bg-yellow-400 cursor-pointer"
              >
                PENDING
              </span>
              <span
                v-else
                class="itbkk-access-right text-white text-xs font-bold rounded-full inline-block ml-4 py-1.5 px-4 cursor-pointer"
                :class="{
                  'bg-green-400': board.visibility === 'PUBLIC',
                  'bg-gray-400': board.visibility === 'PRIVATE'
                }"
              >
                {{ getAccessRight(board.id, userName) }}
              </span>
            </template>

            <template #name>
              <div
                class="flex items-center space-x-2 cursor-pointer hover:text-gray-900 transition duration-100 mx-4 my-4"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="w-5 h-5 text-customPink"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M20 4H4c-1.103 0-2 .897-2 2v10c0 1.103.897 2 2 2h4l-1.8 2.4l1.6 1.2l2.7-3.6h3l2.7 3.6l1.6-1.2L16 18h4c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2zM5 13h4v2H5v-2z"
                    fill="currentColor"
                  />
                </svg>

                <h1 class="itbkk-board-name text-sm font-bold text-gray-800">
                  {{ board.name }}
                </h1>
              </div>
            </template>

            <template #ownerName>
              <div
                class="flex items-center space-x-2 cursor-pointer hover:text-gray-900 transition duration-100 mx-4"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  class="w-5 h-5 text-customPink"
                >
                  <path
                    fill="currentColor"
                    d="M12 19.2c-2.5 0-4.71-1.28-6-3.2c.03-2 4-3.1 6-3.1s5.97 1.1 6 3.1a7.23 7.23 0 0 1-6 3.2M12 5a3 3 0 0 1 3 3a3 3 0 0 1-3 3a3 3 0 0 1-3-3a3 3 0 0 1 3-3m0-3A10 10 0 0 0 2 12a10 10 0 0 0 10 10a10 10 0 0 0 10-10c0-5.53-4.5-10-10-10"
                  />
                </svg>
                <h1 class="itbkk-owner-name text-sm font-bold text-gray-800">
                  {{ board.owner.name }}
                </h1>
              </div>
            </template>

            <!-- Display buttons based on collaborator status -->
            <template #pendingBtn>
              <button
                class="btn rounded-full customBgYellow"
                @click="toBoardsInvitations(board)"
              >
                Accept/Decline
              </button>
            </template>
            <template #Btn>
              <button
                class="btn rounded-full customBgYellow"
                @click="toBoardsList(board)"
              >
                View
              </button>
              <button
                class="itbkk-leave-board btn bg-red-400 rounded-full"
                @click="showRemoveModal(board.id)"
              >
                Leave
              </button>
            </template>
          </PersonalAndCollabBoard>
        </div>
      </div>
    </div>

    <div v-else-if="!boardsCollab || boardsCollab.length === 0">
      <EmptyBoard />
    </div>
  </div>

  <ConfirmModal
    :openModal="showConfirmModal"
    @confirm="confirmRemove()"
    @cancel="showConfirmModal = false"
  >
    <template #svg>
      <div
        class="mx-auto flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
      >
        <svg
          class="h-6 w-6 text-red-600"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          aria-hidden="true"
          data-slot="icon"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"
          />
        </svg>
      </div>
    </template>
    <template #headerMessage> Leave Board </template>
    <template #message>
      <p class="text-sm text-gray-500">Are you sure you want to Leave ?</p>
    </template>
    <template #confirmBtn>
      <span
        class="inline-flex w-full justify-center rounded-md bg-red-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-300 sm:ml-3 sm:w-auto"
        >Leave</span
      >
    </template>
    <template #cancelBtn>
      <span
        class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
        >Cancel</span
      >
    </template>
  </ConfirmModal>
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
