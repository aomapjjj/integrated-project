<script setup>
import { ref, onMounted, watch } from "vue"
import {
  getBoardById,
  getItems,
  addCollaborator,
  deleteCollaborator,
  editAccessRight
} from "../libs/fetchUtils.js"
import { useStatuses } from "../stores/storeStatus"
import { useRoute, useRouter } from "vue-router"
import { useUsers } from "@/stores/storeUser"
import SideBar from "@/component/SideBar.vue"
import Navbar from "@/component/Navbar.vue"
import Alert from "@/component/Alert.vue"
import ModalAcess from "@/component/Modal.vue"
const userStore = useUsers()
const route = useRoute()
const router = useRouter()

const token = localStorage.getItem("access_token")
const disabledButtonWhileOpenPublic = ref(false)
const boardId = ref()
const boardName = ref("")
const openModalAddCollab = ref(false)
const collaboratorEmail = ref("")
const collaboratorAccess = ref("READ")
const statusList = ref([])
const collaboratorInfo = ref([])
const showConfirmModal = ref(false)
const oidCollaboratorToRemove = ref(null)

const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref("")
const openModalAcess = ref(false)
const confirmAcessChange = ref(false)

const cancelChange = () => {
  openModalAcess.value = false
  confirmAcessChange.value = false
  pendingItem.value = null
}
watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

onMounted(async () => {
  userStore.setToken(token)
  const collaborator = await getItems(baseUrlCollaborator)
  collaboratorInfo.value = collaborator
  console.log("Get Items", collaboratorInfo.value)

  const Board = await getBoardById(boardId.value)
  console.log("Board data", Board.item.owner.name)

  if (Board && Board.item && Board.item.name) {
    boardName.value = Board.item.name
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
        accessRight: collaboratorAccess.value
      })

      console.log(result.statusCode)
      switch (result.statusCode) {
        case 201:
          collaboratorInfo.value.push(result.data)
          isAlertSuccess.value = true
          alertMessage.value = "Collaborator added successfully!"
          setTimeout(hideAlert, 3000)
          cancelAction()
          break
        case 401:
          isAlertFailure.value = true
          alertMessage.value = "Unauthorized access. Please log in again."
          setTimeout(hideAlert, 3000)
          break
        case 403:
          isAlertFailure.value = true
          alertMessage.value =
            "You do not have permission to add a collaborator."
          setTimeout(hideAlert, 3000)
          break
        case 404:
          isAlertFailure.value = true
          alertMessage.value = "The user does not exist."
          setTimeout(hideAlert, 3000)
          break
        case 409:
          isAlertFailure.value = true
          alertMessage.value = "The user is already a collaborator."
          setTimeout(hideAlert, 3000)
          break
        default:
          isAlertFailure.value = true
          alertMessage.value = "There is a problem. Please try again later."
          setTimeout(hideAlert, 3000)
      }
    } catch (error) {
      isAlertFailure.value = true
      alertMessage.value = "An error occurred: " + error.message
      setTimeout(hideAlert, 3000)
    }
  } else {
    isAlertFailure.value = true
    alertMessage.value = "Collaborator email and access right are required."
    setTimeout(hideAlert, 3000)
  }
}
console.log(collaboratorInfo.value)
const showRemoveModal = (oid) => {
  console.log(oid)
  oidCollaboratorToRemove.value = oid
  console.log(oidCollaboratorToRemove.value)
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
        alertMessage.value = "Collaborator removed successfully"
        setTimeout(hideAlert, 3000)
      } else {
        isAlertFailure.value = true
        alertMessage.value = "Failed to remove collaborator"
        setTimeout(hideAlert, 3000)
      }
    } catch (error) {
      alertMessage.value = "Error removing collaborator:"
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
    console.error("Failed to open modal:", error)
  }
}

const confirmChange = async () => {
  try {
    confirmAcessChange.value = true
    openModalAcess.value = false

    if (pendingItem.value && confirmAcessChange.value === true) {
      const result = await editAccessRight(
        boardId.value,
        pendingItem.value.accessRight,
        pendingItem.value.id
      )
      console.log(pendingItem.value)
      alertMessage.value = "Access right updated: " + result.accessRight
      isAlertSuccess.value = true
      setTimeout(hideAlert, 3000)
      confirmAcessChange.value = false
      pendingItem.value = null
    }
  } catch (error) {
    console.error("Failed to update access right:", error)
    isAlertFailure.value = true
        alertMessage.value = "Failed to update access right"
        setTimeout(hideAlert, 3000)
  }
}

const hideAlert = () => {
  isAlertFailure.value = false
  isAlertSuccess.value = false
}

const clearForm = () => {
  collaboratorEmail.value = ""
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
        <Navbar> IT Bangmod Kradan Kanbun </Navbar>

        <div class="mt-9 px-10 flex justify-between items-center">
          <div class="text-sm breadcrumbs">
            <ul>
              <li >
               
              <router-link :to="{ name: 'TaskList', params: { id: boardId } }">
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
                  {{ boardName.toLowerCase() }}
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
            class="itbkk-button-add btn ml-4"
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

        <div class="flex flex-col items-center mt-1">
          <!-- Table content -->
          <div class="overflow-x-auto">
            <div class="min-w-full">
              <table
                class="table-auto1 mt-5 rounded-xl overflow-hidden mb-10"
                style="table-layout: fixed"
              >
                <thead>
                  <tr class="bg-base-200">
                    <th
                      class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;
                        color: #fff;
                      "
                    >
                      No.
                    </th>

                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      "
                    >
                      Name
                    </th>

                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      "
                    >
                      E-mail
                    </th>
                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      "
                    >
                      Access Right
                    </th>

                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                        text-align: center;
                        vertical-align: middle;
                      "
                    >
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(item, index) in collaboratorInfo"
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
                      <select
                        class="inline-flex justify-center w-full px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-gray-100 focus:ring-blue-500"
                        v-model="item.accessRight"
                        @change="updateAccessRight(item)"
                      >
                        <option value="READ">Read</option>
                        <option value="WRITE">Write</option>
                      </select>

                      <!-- Modal Component -->
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
                    </td>

                    <td
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      <!-- <button
                        @click="showRemoveModal(item.id)"
                        class="btn bg-red-500 text-white"
                      >
                        Remove
                      </button> -->
                      <button class="btn bg-red-400 rounded-full" @click="showRemoveModal(item.id)">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
                          <g fill="none" fill-rule="evenodd">
                            <path
                              d="M24 0v24H0V0zM12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036c-.01-.003-.019 0-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z" />
                            <path fill="white"
                              d="M14.28 2a2 2 0 0 1 1.897 1.368L16.72 5H20a1 1 0 1 1 0 2h-1v12a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V7H4a1 1 0 0 1 0-2h3.28l.543-1.632A2 2 0 0 1 9.721 2zM17 7H7v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1zm-2.72-3H9.72l-.333 1h5.226z" />
                          </g>
                        </svg>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

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

                <label class="block text-sm font-bold mb-2">Email</label>
                <input
                  type="email"
                  v-model="collaboratorEmail"
                  class="w-full p-2 border rounded mb-4"
                  placeholder="you@ad.sit.kmutt.ac.th	"
                />

                <label class="block text-sm font-bold mb-2">Access Right</label>
                <select
                  v-model="collaboratorAccess"
                  class="w-full p-2 border rounded mb-4"
                >
                  <option value="READ">READ</option>
                  <option value="WRITE">WRITE</option>
                </select>

                <div class="flex justify-end">
                  <button
                    class="btn bg-gray-300 mr-4"
                    @click="cancelAction"
                  >
                    Cancel
                  </button>
                  <button
                    class="btn bg-customPink"
                    @click="submitForm"
                  >
                    Save
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@media (max-width: 576px) {
  .table-auto1 {
    width: 100%;
    overflow-x: auto;
  }
}

td {
  border-bottom: 1px solid #ababab;
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}

thead th {
  height: 3rem;
}

.table-auto1 {
  table-layout: fixed;
}
</style>
