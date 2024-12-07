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
import ConfirmModal from '@/component/modal/ConfirmModal.vue'
import WaitModal from '@/component/modal/WaitModal.vue'
import SuccessModal from '@/component/alert/SuccessModal.vue'
import CollaboratorCard from '@/component/card/CollaboratorCard.vue'

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()

// ----------------------- Params -----------------------

const boardName = ref('')
const isSendEmailSuccess = ref(false)
const boardId = ref()
// const isLoading = ref(false)
const openDropdownId = ref(null)

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

const token = localStorage.getItem('access_token')
const collaboratorEmail = ref('')
const emailTouched = ref(false)
const isOwnerEmail = ref(false)

// Dropdown state
const isOpenDropdown = ref(false)
const collaboratorAccess = ref('READ')

const collaboratorToRemove = ref(null)
const userName = userStore.getUser().username
const userEmail = userStore.getEmail()

// ----------------------- List Items -----------------------

const collaboratorInfo = ref([])
const boardOwnerName = ref('')

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
const waitModal = ref(false)

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlBoardId = `${baseUrlboards}/${boardId.value}`
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

  const Board = await getBoardById(boardId.value)

  boardOwnerName.value = Board.item.owner.name

  if (Board && Board.item && Board.item.name) {
    boardName.value = Board.item.name
  }
  if (Board.item.owner.name !== userName) {
    disabledButtonWhileOpenPublic.value = true
  } else {
    console.log('Error')
  }
})

const openAdd = () => {
  openModalAddCollab.value = true
}

const cancelAction = () => {
  openModalAddCollab.value = false
  clearForm()
}

const showRemoveModal = (item) => {
  collaboratorToRemove.value = item
  showConfirmModal.value = true
}

const confirmRemove = async () => {
  if (collaboratorToRemove.value.id) {
    try {
      const status = await deleteCollaborator(
        boardId.value,
        collaboratorToRemove.value.id
      )
      if (status === 200) {
        collaboratorInfo.value = collaboratorInfo.value.filter(
          (collab) => collab.id !== collaboratorToRemove.value.id
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
      collaboratorToRemove.value.id = null
    }
  }
}

const openDropdownAccess = (itemId) => {
  openDropdownId.value = openDropdownId.value === itemId ? null : itemId
}
const pendingItem = ref(null)

const updateAccessRight = (item, access) => {
  try {
    openModalAcess.value = true
    pendingItem.value = { ...item }
    pendingItem.value.accessRight = access
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
      if (typeof result === 'object') {
        await fetchCollaborators()
        openDropdownId.value = false
        alertMessage.value = 'Access right updated: ' + result.accessRight
        isAlertSuccess.value = true
        setTimeout(hideAlert, 3000)
        confirmAcessChange.value = false
        pendingItem.value = null
      }
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
  // isSendEmailSuccess.value = false
}

const emailErrorMessage = computed(() => {
  if (!emailTouched.value) {
    return ''
  }
  const email = collaboratorEmail.value.trim()
  if (!email.includes('@')) {
    return `Email must contain an '@' symbol.`
  }
  const domainRegex = /^[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  const [localPart, domain] = email.split('@')
  if (!domain || !domainRegex.test(domain)) {
    return `Invalid email domain. Please use a format like 'ad.sit.kmutt.ac.th'.`
  }
  const localPartRegex = /^[a-zA-Z0-9._%+-]+$/
  if (!localPart || !localPartRegex.test(localPart)) {
    return `The part before '@' contains invalid characters.`
  }
  if (email === userEmail.email) {
    return 'Board owner cannot be added as a collaborator.'
  }
  return ''
})

const validateEmail = (isBlur = false) => {
  if (isBlur) {
    emailTouched.value = true
  }
}

const clearForm = () => {
  collaboratorEmail.value = ''
  emailTouched.value = false
}

const checkEmail = computed(() => {
  if (collaboratorEmail.value === userEmail.email) {
    isOwnerEmail.value = true
  } else {
    isOwnerEmail.value = false
  }
  return (
    !collaboratorEmail.value ||
    collaboratorEmail.value === userEmail.email ||
    isOwnerEmail.value ||
    !collaboratorEmail.value.includes('@')
  )
})

const submitFormSendEmail = async () => {
  const email = collaboratorEmail?.value
  const accessRight = collaboratorAccess.value
  const boardIdValue = boardId.value
  const inviterName = boardOwnerName.value
  const boardNames = boardName.value
  const boardUrl = baseUrlBoardId
  waitModal.value = true
  if (!email || !accessRight || !inviterName || !boardNames || !boardUrl) {
    console.error('One or more required fields are missing.')
    return
  }

  try {
    const collaboratorWithEmailDTO = {
      collaborator: {
        email,
        accessRight,
        status: 'PENDING'
      },
      email: {
        inviterName,
        boardName: boardNames,
        accessRight, 
        boardUrl
      },
      access_token : localStorage.getItem("accesstokenToMS")
    }

    const result = await addCollaborator(boardIdValue, collaboratorWithEmailDTO)

    switch (result.statusCode) {
      case 201:
        // collaboratorInfo.value.push(result.data)
        await fetchCollaborators()
        isSendEmailSuccess.value = true
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
        alertMessage.value = 'You do not have permission to add a collaborator.'
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
            'The user is already the collaborator or pending collaborator of this board.'
        } else if (
          result.data.message ===
          'The collaborator email belongs to the board owner'
        ) {
          alertMessage.value =
            'Board owner cannot be collaborator of his/her own board'
        } else {
          alertMessage.value =
            'The user is already the collaborator or pending collaborator of this board.'
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
  } finally {
    waitModal.value = false
    // isLoading.value = false
  }
}

const fetchCollaborators = async () => {
  try {
    const collaborator = await getItems(baseUrlCollaborator)
    collaboratorInfo.value = collaborator.collaborators
  } catch (error) {
    console.error('Failed to fetch collaborators:', error)
  }
}

watch(
  () => boardId.value,
  async () => {
    await fetchCollaborators()
  },
  { immediate: true }
)

const deleteConfirmationMessage = computed(() => {
  if (
    collaboratorToRemove.value &&
    collaboratorToRemove.value.status === 'PENDING'
  ) {
    return `Do you want to cancel the invitation to ${collaboratorToRemove.value.name}?`
  }
  return `Do you want to remove ${collaboratorToRemove.value.name} from the board?`
})
</script>

<template>
  <div>
    <WaitModal :is-loading="waitModal" />
  </div>

  <div class="fixed top-0 right-0 mt-4 mr-4 z-20">
    <Alert :isAlertFailure="isAlertFailure" :isAlertSuccess="isAlertSuccess">
      {{ alertMessage }}
    </Alert>
    <SuccessModal
      :isSuccess="isSendEmailSuccess"
      @close="isSendEmailSuccess = false"
    />
  </div>

  <div class="flex flex-col h-screen overflow-hidden">
    <div class="flex flex-1 overflow-hidden">
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
          <div>
            <div
              class="bg-base-100 mt-4 md:mt-0 flex justify-center items-center"
              v-if="!collaboratorInfo.length || collaboratorInfo?.length === 0"
            >
              <span class="text-center py-4 text-gray-400">
                No Collaborator
              </span>
            </div>
            <div
              v-else
              :class="[
                'grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3 z-0 px-4',
                collaboratorInfo.length > 3
                  ? 'max-h-[calc(100vh-200px)] overflow-y-auto'
                  : ''
              ]"
            >
              <CollaboratorCard
                v-for="(item, index) in collaboratorInfo"
                :key="item.id"
              >
                <template #status>
                  <!-- pendind -->
                  <div class="flex items-center">
                    <svg
                      v-if="item.status === 'PENDING'"
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-3 h-3 text-yellow-400"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2S2 6.477 2 12s4.477 10 10 10"
                        opacity=".5"
                      />
                      <path
                        fill="currentColor"
                        fill-rule="evenodd"
                        d="M12 7.25a.75.75 0 0 1 .75.75v3.69l2.28 2.28a.75.75 0 1 1-1.06 1.06l-2.5-2.5a.75.75 0 0 1-.22-.53V8a.75.75 0 0 1 .75-.75"
                        clip-rule="evenodd"
                      />
                    </svg>
                    <!-- accept -->
                    <svg
                      v-if="item.status === 'ACCEPTED'"
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-3 h-3 text-green-400"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        fill-rule="evenodd"
                        d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10s-4.477 10-10 10m-1.177-7.86l-2.765-2.767L7 12.431l3.119 3.121a1 1 0 0 0 1.414 0l5.952-5.95l-1.062-1.062z"
                      />
                    </svg>
                    <span
                      :class="{
                        ' text-yellow-400': item.status === 'PENDING',
                        ' text-green-400': item.status === 'ACCEPTED'
                      }"
                      class="text-xs font-medium px-2 py-1 rounded-full"
                    >
                      {{ item.status === 'PENDING' ? 'PENDING' : 'ACCEPTED' }}
                    </span>
                  </div>
                </template>
                <template #name>
                  <p class="itbkk-name">{{ item.name }}</p>
                </template>
                <template #email>
                  <p class="itbkk-email">
                    {{ item.email }}
                  </p>
                </template>

                <template #access-right>
                  <div class="itbkk-access-right">
                    <div class="relative inline-block text-left">
                      <button
                        @click="openDropdownAccess(item.id)"
                        type="button"
                        class="inline-flex w-full justify-center gap-x-1.5 rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                        id="menu-button"
                        aria-expanded="true"
                        aria-haspopup="true"
                      >
                        {{ item.accessRight }}
                        <svg
                          class="-mr-1 h-5 w-5 text-gray-400"
                          viewBox="0 0 20 20"
                          fill="currentColor"
                          aria-hidden="true"
                        >
                          <path
                            fill-rule="evenodd"
                            d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z"
                            clip-rule="evenodd"
                          />
                        </svg>
                      </button>

                      <div
                        v-if="openDropdownId === item.id"
                        class="absolute mt-2 left-5 w-32 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
                        role="menu"
                        aria-orientation="vertical"
                        aria-labelledby="menu-button"
                        tabindex="-1"
                      >
                        <div class="py-1" role="none">
                          <p
                            @click="
                              () =>
                                updateAccessRight(
                                  collaboratorInfo[index],
                                  'READ'
                                )
                            "
                            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-all"
                          >
                            READ
                          </p>
                          <p
                            @click="
                              () =>
                                updateAccessRight(
                                  collaboratorInfo[index],
                                  'WRITE'
                                )
                            "
                            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-all"
                          >
                            WRITE
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
                <!-- <select
                      v-model="item.accessRight"
                      @change="updateAccessRight(item)"
                      class="border border-gray-300 rounded-full text-gray-900 h-10 pl-5 pr-10 bg-white hover:border-gray-400 focus:outline-none appearance-none"
                    >
                      <option value="READ">Read</option>
                      <option value="WRITE">Write</option>
                    </select> -->
                <template #btn>
                  <button
                    :disabled="disabledButtonWhileOpenPublic"
                    @click="showRemoveModal(item)"
                    class="itbkk-collab flex items-center justify-center rounded-full transition duration-200 ease-in-out"
                    :class="
                      item.status === 'PENDING'
                        ? 'hover:text-gray-500 text-gray-400'
                        : 'hover:text-red-500 text-red-400'
                    "
                  >
                    <!-- cancel -->
                    <svg
                      v-if="item.status === 'PENDING'"
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-6 h-6"
                      viewBox="0 0 1024 1024"
                    >
                      <path
                        fill="currentColor"
                        d="M512 64a448 448 0 1 1 0 896a448 448 0 0 1 0-896M288 512a38.4 38.4 0 0 0 38.4 38.4h371.2a38.4 38.4 0 0 0 0-76.8H326.4A38.4 38.4 0 0 0 288 512"
                      />
                    </svg>
                    <!-- delete -->
                    <svg
                      v-else-if="item.status === 'ACCEPTED'"
                      xmlns="http://www.w3.org/2000/svg"
                      class="w-6 h-6"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        d="M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10s10-4.47 10-10S17.53 2 12 2m4.3 14.3a.996.996 0 0 1-1.41 0L12 13.41L9.11 16.3a.996.996 0 1 1-1.41-1.41L10.59 12L7.7 9.11A.996.996 0 1 1 9.11 7.7L12 10.59l2.89-2.89a.996.996 0 1 1 1.41 1.41L13.41 12l2.89 2.89c.38.38.38 1.02 0 1.41"
                      />
                    </svg>
                  </button>
                </template>
              </CollaboratorCard>
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

              <template #headerMessage>
                {{
                  collaboratorToRemove?.status === 'PENDING'
                    ? 'Cancel Collaborator'
                    : 'Delete Collaborator'
                }}
              </template>

              <template #message>
                <p class="text-sm text-gray-500">
                  {{ deleteConfirmationMessage }}
                </p>
              </template>

              <template #confirmBtn>
                <span
                  :class="
                    collaboratorToRemove?.status === 'PENDING'
                      ? 'inline-flex w-full justify-center rounded-md bg-green-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-green-400 sm:ml-3 sm:w-auto'
                      : 'inline-flex w-full justify-center rounded-md bg-red-500 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-400 sm:ml-3 sm:w-auto'
                  "
                >
                  {{
                    collaboratorToRemove?.status === 'PENDING'
                      ? 'Confirm'
                      : 'Delete'
                  }}
                </span>
              </template>

              <template #cancelBtn>
                <span
                  class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
                >
                  Cancel
                </span>
              </template>
            </ConfirmModal>

            <ModalAcess
              :isOpen="openModalAcess"
              @confirm="confirmChange"
              @cancel="cancelChange"
            >
              <template #headerName>Access Collaborator</template>
              <template #messageName
                >Do you want to change access right of {{ pendingItem.name }} to
                {{ pendingItem.accessRight }}?</template
              >
            </ModalAcess>

            <!-- Modal AddCollab -->
            <div
              v-if="openModalAddCollab"
              class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 transition-opacity duration-300"
            >
              <div
                class="bg-white rounded-lg shadow-lg w-full max-w-lg mx-4 px-6 py-6 flex flex-col gap-4 transform scale-95 transition-all duration-300 ease-in-out"
              >
                <div>
                  <div class="text-center mb-4">
                    <h2 class="text-2xl font-bold customPurple">
                      Add Collaborator
                    </h2>
                    <p class="text-gray-500 text-sm mt-1">
                      Invite someone to collaborate on
                      <span class="font-bold">{{ boardName }}</span
                      >.
                    </p>
                  </div>
                  <div class="flex flex-col md:flex-row gap-4 mb-6">
                    <!-- Email Address -->
                    <div class="flex-1">
                      <label
                        for="collaboratorEmail"
                        class="block text-base font-bold mb-2 px-2"
                      >
                        Email Address <span class="text-red-500">*</span>
                      </label>
                      <input
                        id="collaboratorEmail"
                        type="email"
                        v-model="collaboratorEmail"
                        placeholder="you@ad.sit.kmutt.ac.th"
                        @input="validateEmail"
                        @blur="validateEmail(true)"
                        :class="[
                          'w-full px-4 py-3 border rounded-full text-gray-700 focus:ring-2 focus:ring-customPurple focus:outline-none transition duration-200',
                          emailErrorMessage
                            ? 'border-2 border-red-400'
                            : 'border-gray-300'
                        ]"
                      />
                      <p
                        v-if="emailErrorMessage"
                        class="text-sm text-red-500 mt-1 px-2"
                      >
                        {{ emailErrorMessage }}
                      </p>
                    </div>

                    <div class="w-1/4">
                      <label class="block text-base font-bold mb-2 px-2"
                        >Access Right</label
                      >
                      <select
                        v-model="collaboratorAccess"
                        class="w-full px-4 py-3 border rounded-full bg-white text-gray-700 focus:ring-2 focus:ring-customPurple focus:outline-none transition duration-200"
                      >
                        <option value="READ">Read</option>
                        <option value="WRITE">Write</option>
                      </select>
                    </div>
                  </div>

                  <div
                    class="flex justify-center gap-4 px-4 py-4 border-t border-gray-200"
                  >
                    <button
                      class="py-3 px-6 rounded-full border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all duration-200"
                      @click="cancelAction"
                    >
                      Cancel
                    </button>
                    <button
                      :disabled="checkEmail"
                      class="py-3 px-7 rounded-full bg-customPink text-white hover:bg-[#fa619c] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                      @click="submitFormSendEmail"
                    >
                      Add
                    </button>
                  </div>
                </div>
              </div>
            </div>
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
