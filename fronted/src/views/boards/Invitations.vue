<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import {
  getBoardById,
  getItems,
  addCollaborator,
  editStatusCollab,
  deleteCollaborator
} from './../../libs/fetchUtils.js'
import { useRoute, useRouter } from 'vue-router'
import { useUsers } from '@/stores/storeUser'
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import Alert from '@/component/alert/Alert.vue'

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()

// ----------------------- Params -----------------------

const boardOwnerName = ref('')
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

// ----------------------- List Items -----------------------

const collaboratorInfo = ref([])

// ----------------------- Enable & Disable -----------------------

const disabledButtonWhileOpenPublic = ref(false)
const openModalAddCollab = ref(false)
const showConfirmModal = ref(false)

// ----------------------- Alerts -----------------------

const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref('')

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlBoardId = `${baseUrlboards}/${boardId.value}`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

console.log(userEmail.email)

onMounted(async () => {
  userStore.setToken(token)
  const collaborator = await getItems(baseUrlCollaborator)
  collaboratorInfo.value = collaborator.collaborators
  console.log('Get Items', collaboratorInfo.value)
  console.log(baseUrlboards)

  const Board = await getBoardById(boardId.value)
  boardName.value = Board.item.name
  console.log(Board.item.name)
  console.log('Board data', Board.item.owner.name)
  boardOwnerName.value = Board.item.owner.name
  console.log(boardOwnerName.value)

  if (Board.item.owner.name !== userName) {
    disabledButtonWhileOpenPublic.value = true
  }
})

const formatStatus = () => {
  if (collaboratorInfo.value.length > 0) {
    return (
      collaboratorInfo.value[0].status.charAt(0) +
      collaboratorInfo.value[0].status.slice(1).toLowerCase()
    )
  }
  return ''
}

const filteredCollaboratorInfo = computed(() =>
  collaboratorInfo.value.filter(
    (item) => item.status === 'PENDING' && item.email === userEmail.email
  )
)

const acceptCollaborator = async (collaborator) => {
  console.log(collaborator)

  try {
    if (collaborator) {
      collaborator.status = 'ACCEPTED'
      await editStatusCollab(
        boardId.value,
        collaborator.status,
        collaborator.id,
        collaborator.accessRight
      )
      router.push({ name: 'TaskList', params: { id: boardId.value } })
    }
  } catch (error) {
    console.error('Error updating collaborator status:', error)
  }
}

const declineCollaborator = async (collaborator) => {
  try {
    const status = await deleteCollaborator(boardId.value, collaborator.id)
    if (status === 200) {
      console.log('Collaborator deleted successfully')
      collaboratorInfo.value = collaboratorInfo.value.filter(
        (c) => c.id !== collaborator.id
      )
    } else {
      console.log('Failed to delete collaborator')
    }
  } catch (error) {
    console.error('Error deleting collaborator:', error)
  }
}
</script>

<template>
  <div class="flex flex-col h-screen overflow-hidden">
    <div class="flex flex-1 overflow-hidden">
      <Alert :isAlertFailure="isAlertFailure" :isAlertSuccess="isAlertSuccess">
        {{ alertMessage }}
      </Alert>
      <SideBar />

      <div class="flex flex-col flex-1">
        <Navbar> IT Bangmod Kradan Kanban </Navbar>

        <div class="flex flex-col justify-center items-center h-[100vh]">
          <div
            class="relative flex flex-col items-center rounded-[10px] border-[1px] border-gray-200 w-[400px] mx-auto p-4 bg-white bg-clip-border shadow-md shadow-[#F3F3F3] dark:border-[#ffffff33] dark:!bg-navy-800 dark:text-white dark:shadow-none"
          >
            <div
              class="relative flex h-32 w-full justify-center rounded-xl bg-cover"
            >
              <img
                src="/image/invite.png"
                class="absolute flex h-32 w-full justify-center rounded-xl bg-cover"
              />
              <div
                class="absolute -bottom-12 flex h-[87px] w-[87px] items-center justify-center rounded-full border-[4px] border-white bg-pink-400 dark:!border-navy-700"
              >
                <img
                  class="h-full w-full rounded-full"
                  src="/image/profile.png"
                  alt="profile"
                />
              </div>
            </div>

            <div class="mt-16 flex flex-col items-center">
              <!-- <h4 class="text-xl font-bold text-navy-700 dark:text-white">
                Adela Parkson
              </h4> -->
              <div
                v-if="filteredCollaboratorInfo.length > 0"
                class="itbkk-invite-message"
              >
                <p>
                  (inviter name) has invited you to collaborate with
                  (access-right given) access right on (board name) board
                </p>
              </div>
              <!-- <p class="text-base font-normal text-gray-600">Product Manager</p> -->
            </div>
          </div>
          <p class="font-normal text-navy-700 mt-20 mx-auto w-max">
            Profile Card component from
          </p>
        </div>

        <!-- <div class="flex flex-col items-center mt-1">
                <tbody>
                  <tr
                    v-for="(item, index) in filteredCollaboratorInfo"
                    :key="item.id"
                    class="itbkk-item"
                  >
                    <td
                      class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{ index + 1 }}
                    </td>
                    <td class="">
                      <label class="itbkk-status-name" for="my_modal_6">
                        {{ item.name }}
                      </label>
                    </td>

                    <td
                      class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{ item.email }}
                    </td>

                    <td
                      class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{ formatStatus() }}
                    </td>

                    <td
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700 space-x-2 flex justify-center"
                    >
                      <button
                        class="inline-flex bg-green-500 hover:bg-green-600 text-white rounded-full w-8 h-8 items-center justify-center"
                        @click="acceptCollaborator(item)"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke-width="2"
                          stroke="currentColor"
                          class="w-4 h-4"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            d="M5 13l4 4L19 7"
                          />
                        </svg>
                      </button>
                      <button
                        class="inline-flex bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 items-center justify-center"
                        @click="declineCollaborator(item)"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke-width="2"
                          stroke="currentColor"
                          class="w-4 h-4"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            d="M6 18L18 6M6 6l12 12"
                          />
                        </svg>
                      </button>
                    </td>
                  </tr>
                  <tr
                    class="bg-base-100 mt-4 md:mt-0"
                    v-if="filteredCollaboratorInfo?.length === 0"
                  >
                    <td colspan="5" class="text-center py-4 text-gray-400">
                      Sorry, we couldn't find the invitation to this board
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div> -->
      </div>
    </div>
  </div>
</template>

<style></style>
