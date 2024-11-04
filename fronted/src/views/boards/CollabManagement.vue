<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import {
  getBoardById,
  getItems,
  addCollaborator,
  deleteCollaborator,
  editAccessRight
} from './../../libs/fetchUtils.js'
import { useRoute, useRouter } from 'vue-router'
import { useUsers } from '@/stores/storeUser'
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import Alert from '@/component/alert/Alert.vue'
import ModalAcess from '@/component/modal/Modal.vue'
import CollabCard from '@/component/card/CollabCard.vue'
import ConfirmModal from '@/component/modal/ConfirmModal.vue'

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()

// ----------------------- Params -----------------------

const boardName = ref('')
const boardId = ref()
watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

const token = localStorage.getItem('access_token')
const collaboratorEmail = ref('')
const collaboratorAccess = ref('READ')
const oidCollaboratorToRemove = ref(null)
const userName = userStore.getUser().username
const userEmail = userStore.getEmail()

console.log(userStore.getUserInfo)

// ----------------------- List Items -----------------------

const collaboratorInfo = ref([])

// ----------------------- Enable & Disable -----------------------

const disabledButtonWhileOpenPublic = ref(false)
const openModalAddCollab = ref(false)
const showConfirmModal = ref(false)
const openModalAcess = ref(false)
const confirmAcessChange = ref(false)

// ----------------------- Alerts -----------------------

const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref('')

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

const cancelChange = () => {
  openModalAcess.value = false
  confirmAcessChange.value = false
  pendingItem.value = null
}

onMounted(async () => {
  userStore.setToken(token)
  const collaborator = await getItems(baseUrlCollaborator)
  collaboratorInfo.value = collaborator.collaborators
  console.log('Get Items', collaboratorInfo.value)

  const Board = await getBoardById(boardId.value)
  console.log('Board data', Board.item.owner.name)

  if (Board && Board.item && Board.item.name) {
    boardName.value = Board.item.name
  }
  if (Board.item.owner.name !== userName) {
    disabledButtonWhileOpenPublic.value = true
    console.log('ไม่ตรงกันนะจ๊า')
  } else {
    console.log('ตรงกันนะจ๊า')
  }
})

const openAdd = () => {
  openModalAddCollab.value = true
}

const cancelAction = () => {
  openModalAddCollab.value = false
  clearForm()
}

const submitForm = async () => {
  if (collaboratorAccess.value && collaboratorEmail.value) {
    try {
      const result = await addCollaborator(boardId.value, {
        email: collaboratorEmail.value,
        accessRight: collaboratorAccess.value,
        status: "ACCEPTED"
      })

      console.log(result)
      switch (result.statusCode) {
        case 201:
          collaboratorInfo.value.push(result.data)
          isAlertSuccess.value = true
          alertMessage.value = 'Collaborator added successfully!'
          setTimeout(hideAlert, 3000)
          cancelAction()
          break
        case 401:
          isAlertFailure.value = true
          alertMessage.value = 'Unauthorized access. Please log in again.'
          setTimeout(hideAlert, 3000)
          break
        case 403:
          isAlertFailure.value = true
          alertMessage.value =
            'You do not have permission to add a collaborator.'
          setTimeout(hideAlert, 3000)
          break
        case 404:
          isAlertFailure.value = true
          alertMessage.value = 'The user does not exists.'
          setTimeout(hideAlert, 3000)
          break
        case 409:
          isAlertFailure.value = true
          if (
            result.data.message ===
            'The collaborator already exists for this board'
          ) {
            alertMessage.value =
              'The user is already a collaborator of this board.'
          } else if (
            result.data.message ===
            'The collaborator email belongs to the board owner'
          ) {
            alertMessage.value =
              'Board owner cannot be collaborator of his/her own board'
          } else {
            alertMessage.value = 'An unknown error occurred.'
          }
          setTimeout(hideAlert, 3000)
          break
        default:
          isAlertFailure.value = true
          alertMessage.value = 'There is a problem. Please try again later.'
          setTimeout(hideAlert, 3000)
      }
    } catch (error) {
      isAlertFailure.value = true
      alertMessage.value = 'An error occurred: ' + error.message
      setTimeout(hideAlert, 3000)
    }
  } else {
    isAlertFailure.value = true
    alertMessage.value = 'Collaborator email and access right are required.'
    setTimeout(hideAlert, 3000)
  }
}

const showRemoveModal = (oid) => {
  oidCollaboratorToRemove.value = oid
  showConfirmModal.value = true
}

const confirmRemove = async () => {
  if (oidCollaboratorToRemove.value) {
    try {
      const status = await deleteCollaborator(
        boardId.value,
        oidCollaboratorToRemove.value
      )
      if (status === 200) {
        collaboratorInfo.value = collaboratorInfo.value.filter(
          (collab) => collab.id !== oidCollaboratorToRemove.value
        )
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

const pendingItem = ref(null)

const updateAccessRight = (item) => {
  try {
    openModalAcess.value = true
    pendingItem.value = item
  } catch (error) {
    console.error('Failed to open modal:', error)
  }
}

const confirmChange = async () => {
  openModalAcess.value = true
  try {
    confirmAcessChange.value = true
    openModalAcess.value = false

    if (pendingItem.value && confirmAcessChange.value === true) {
      const result = await editAccessRight(
        boardId.value,
        pendingItem.value.accessRight,
        pendingItem.value.id,
        pendingItem.value.status
      )
      console.log(pendingItem.value)
      alertMessage.value = 'Access right updated: ' + result.accessRight
      isAlertSuccess.value = true
      setTimeout(hideAlert, 3000)
      confirmAcessChange.value = false
      pendingItem.value = null
    }
  } catch (error) {
    console.error('Failed to update access right:', error)
    isAlertFailure.value = true
    alertMessage.value = 'Failed to update access right'
    setTimeout(hideAlert, 3000)
  }
}

const hideAlert = () => {
  isAlertFailure.value = false
  isAlertSuccess.value = false
}

const clearForm = () => {
  collaboratorEmail.value = ''
}

const checkDisabled = () => {
  if (
    !collaboratorEmail.value ||
    collaboratorEmail.value === userEmail.email ||
    !collaboratorEmail.value.includes('@')
  ) {
    return true
  }
  return false
}

const filteredCollaboratorInfo = computed(() =>
  collaboratorInfo.value.filter(item => item.status === 'ACCEPTED')
);
</script>

<template>
  <div class="flex flex-col h-screen overflow-hidden">
    <div class="flex flex-1 overflow-hidden">
      <Alert :isAlertFailure="isAlertFailure" :isAlertSuccess="isAlertSuccess">
        {{ alertMessage }}
      </Alert>
      <SideBar />

      <div class="flex flex-col flex-1">
        <Navbar> Collaborator Management </Navbar>

        <div class="mt-9 px-10 flex justify-between items-center">
          <div class="text-sm breadcrumbs">
            <ul>
              <li>
                <router-link
                  :to="{ name: 'TaskList', params: { id: boardId } }"
                >
                  <svg
                    xmlns="http://www.w3.org/3000/svg"
                    viewBox="0 0 256 256"
                    class="w-4 h-4 stroke-current mr-2"
                  >
                    <path
                      fill="currentColor"
                      d="m219.31 108.68l-80-80a16 16 0 0 0-22.62 0l-80 80A15.87 15.87 0 0 0 32 120v96a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-56h32v56a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-96a15.87 15.87 0 0 0-4.69-11.32M208 208h-48v-56a8 8 0 0 0-8-8h-48a8 8 0 0 0-8 8v56H48v-88l80-80l80 80Z"
                    />
                  </svg>
                  <span class="itbkk-board-name">{{ boardName }}</span>
                </router-link>
              </li>
              <li>
                <a>
                  <svg
                    xmlns="http://www.w3.org/3000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    class="w-4 h-4 stroke-current mr-2"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
                    ></path>
                  </svg>
                  <span class="font-extrabold">Collaborator</span>
                </a>
              </li>
            </ul>
          </div>

          <button
            :disabled="disabledButtonWhileOpenPublic"
            :style="{
              backgroundColor: disabledButtonWhileOpenPublic
                ? '#d3d3d3'
                : '#F785B1',
              color: disabledButtonWhileOpenPublic ? '#a9a9a9' : 'white',
              borderRadius: '30px',
              cursor: disabledButtonWhileOpenPublic ? 'not-allowed' : 'pointer',
              opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
            }"
            @click="openAdd"
            class="itbkk-collaborator-add btn ml-4"
          >
            <svg
              xmlns="http://www.w3.org/3000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z"
              />
            </svg>
            Add Collaborator
          </button>
        </div>

        <div class="flex flex-col items-center mt-2">
          <!-- Table content -->
          <!-- <div class="overflow-x-auto">
            <div class="min-w-full">
              <table
                class="table-auto1 mt-5 rounded-xl overflow-hidden mb-10"
                style="table-layout: fixed"
              >

                <tbody>
                    <td
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      <button
                        :disabled="disabledButtonWhileOpenPublic"
                        class="btn bg-red-400 rounded-full"
                        @click="showRemoveModal(item.id)"
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
                    </td>
                  </tr>
                  
                </tbody>
              </table>
            </div>
          </div> -->

          <div>
            <div class="bg-base-100" v-if="!filteredCollaboratorInfo.length">
              <span class="text-center py-4 text-gray-400">
                No Collaborator
              </span>
            </div>
            <div
              v-else
              class="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3"
            >
              <CollabCard
                v-for="(item, index) in filteredCollaboratorInfo"
                :key="item.id"
              >
                <template #name>
                  <h2 class="itbkk-name font-semibold">{{ item.name }}</h2>
                </template>

                <template #email>
                  <p class="itbkk-email mt-2 text-sm text-gray-500">
                    {{ item.email }}
                  </p>
                </template>

                <template #access-right>
                  <div class="itbkk-access-right mt-3 relative inline-flex">
                    <svg
                      class="w-2 h-2 absolute top-2 right-1 m-2 pointer-events-none"
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 412 232"
                    >
                      <path
                        d="M206 171.144L42.678 7.822c-9.763-9.763-25.592-9.763-35.355 0-9.763 9.764-9.763 25.592 0 35.355l181 181c4.88 4.882 11.279 7.323 17.677 7.323s12.796-2.441 17.678-7.322l181-181c9.763-9.764 9.763-25.592 0-35.355-9.763-9.763-25.592-9.763-35.355 0L206 171.144z"
                        fill="#648299"
                        fill-rule="nonzero"
                      />
                    </svg>
                    <select
                      v-model="item.accessRight"
                      @change="updateAccessRight(item)"
                      class="border border-gray-300 rounded-full text-gray-600 h-10 pl-5 pr-10 bg-white hover:border-gray-400 focus:outline-none appearance-none"
                    >
                      <option value="READ">Read</option>
                      <option value="WRITE">Write</option>
                    </select>
                  </div>
                </template>
                <template #btn>
                  <button
        :disabled="disabledButtonWhileOpenPublic"
        @click="showRemoveModal(item.id)"
        class="itbkk-collab -ml-4 flex items-center justify-center rounded-full px-2 hover:text-red-500 text-red-400 transition duration-200 ease-in-out"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-4 h-4"
          viewBox="0 0 24 24"
        >
          <path
            fill="currentColor"
            fill-rule="evenodd"
            d="m12 10.586l5.657-5.657l1.414 1.414L13.414 12l5.657 5.657l-1.414 1.414L12 13.414l-5.657 5.657l-1.414-1.414L10.586 12L4.929 6.343L6.343 4.93z"
          />
        </svg>
      </button>
                </template>
              </CollabCard>
            </div>

            <ConfirmModal :openModal="showConfirmModal" @confirm="confirmRemove()" @cancel="showConfirmModal = false">
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
        <template #headerMessage> Delete Collaborator </template>
        <template #message>
          <p class="text-sm text-gray-500">
            Are you sure you want to delete your Collaborator? 
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

            <ModalAcess
              :isOpen="openModalAcess"
              @confirm="confirmChange"
              @cancel="cancelChange"
            >
              <template #headerName>Access Changed!</template>
              <template #messageName
                >Are you sure you want to change the access?</template
              >
            </ModalAcess>

            <!-- Modal AddCollab -->
            <div
              v-if="openModalAddCollab"
              class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50"
            >
              <div
                class="max-h-full w-full max-w-md overflow-y-auto sm:rounded-2xl bg-white"
              >
                <div class="w-full p-8">
                  <h2 class="text-2xl font-bold text-center mb-4">
                    Add Collaborator
                  </h2>

                  <div class="flex items-center space-x-4 mb-4">
                    <div class="flex-1">
                      <label class="block text-sm font-bold mb-2">Email</label>
                      <input
                        type="email"
                        v-model="collaboratorEmail"
                        class="w-full p-2 border rounded-lg"
                        placeholder="you@ad.sit.kmutt.ac.th"
                      />
                    </div>

                    <div>
                      <label class="block text-sm font-bold mb-2"
                        >Access Right</label
                      >
                      <select
                        v-model="collaboratorAccess"
                        class="w-full p-2 border border-gray-300 rounded-lg bg-white text-sm"
                      >
                        <option value="READ">Read</option>
                        <option value="WRITE">Write</option>
                      </select>
                    </div>
                  </div>

                  <div class="flex justify-end mt-4">
                    <button class="btn bg-gray-300 mr-4" @click="cancelAction">
                      Cancel
                    </button>
                    <button
                      :disabled="checkDisabled()"
                      class="btn bg-customPink hover:bg-customPinkDark disabled:opacity-50"
                      @click="submitForm"
                    >
                      Add
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- <div
              v-if="showConfirmModal"
              class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50"
            >
              <div class="bg-white p-6 rounded-md max-w-md w-full">
                <h3 class="text-lg font-semibold text-center mb-4">
                  Are you sure you want to remove this collaborator?
                </h3>
                <div class="flex justify-end">
                  <button
                    @click="showConfirmModal = false"
                    class="btn bg-gray-500 text-white mr-4"
                  >
                    Cancel
                  </button>
                  <button
                    @click="confirmRemove"
                    class="btn bg-red-500 text-white"
                  >
                    Confirm
                  </button>
                </div>
              </div>
            </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
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
</style>
