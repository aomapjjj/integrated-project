<script setup>
import { ref, onMounted, computed, watch } from "vue"
import {
  getItemById,
  getItems,
  addItem,
  editItem,
  deleteItemById,
  deleteItemAndTransfer,
  getBoardById,
  getResponseItems
} from "@/libs/fetchUtils.js"
import { useStatuses } from "@/stores/storeStatus"
import { useRoute, useRouter } from "vue-router"
import { useLimitStore } from "@/stores/storeLimit"
import { useTasks } from "@/stores/store"
import { useUsers } from "@/stores/storeUser"
import SideBar from "@/component/bar/SideBar.vue"
import Navbar from "@/component/bar/Navbar.vue"
import LodingPage from "@/component/ui/LodingPage.vue"
import Alert from "@/component/alert/Alert.vue"
import ConfirmModal from "@/component/modal/ConfirmModal.vue"
// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()
const myStatuses = useStatuses()
const limitStore = useLimitStore()
const taskStore = useTasks()

// ----------------------- Params -----------------------

const selectedStatusId = ref(0)
const boardName = ref("")
const boardId = ref()
const isLoading = ref(true)

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)
const token = localStorage.getItem("access_token")

const userName = userStore.getUser().username

// ----------------------- List Items -----------------------

const statusList = ref([])
const status = ref({
  id: "",
  name: "",
  description: "",
  createdOn: "",
  updatedOn: ""
})

const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: "No Status"
})

// ----------------------- Alerts -----------------------

const notFound = ref(false)
const errorAdd = ref("")
const errorEdit = ref("")

const notAdd = ref(false)
const notEdit = ref(false)

const showAlertEdit = ref(false)
const showAlertAfterEdit = ref(false)

const showAlertAfterAdd = ref(false)

const showAlertDelete = ref(false)
const showAlertAfterDelete = ref(false)

const limitAlert = ref(false)
const errorLimit = ref("")
const errorTrans = ref(false)

// ----------------------- Enable & Disable -----------------------

const disabledButtonWhileOpenPublic = ref(false)
const openModalDelete = ref(false)

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlStatus = `${baseUrlboards}/${boardId.value}/statuses`

onMounted(async () => {
  try {
    isLoading.value = true

    userStore.setToken(token)

    if (myStatuses.getStatuses().length === 0) {
      const items = await getItems(baseUrlStatus)
      myStatuses.addStatuses(items)
    }

    statusList.value = myStatuses.getStatuses()
    const statusId = route.params.id

    const board = await getBoardById(boardId.value)

    if (board && board.item && board.item.name) {
      boardName.value = board.item.name
    }
    if (!userName) {
      disabledButtonWhileOpenPublic.value = true
    } else if (
      board.item.owner.name !== userName &&
      !board.item.collaborators.some(
        (collab) => collab.name === userName && collab.accessRight === "WRITE"
      )
    ) {
      disabledButtonWhileOpenPublic.value = true
    } else {
      disabledButtonWhileOpenPublic.value = false
    }
  } catch (error) {
    console.error("Error loading data:", error)
  } finally {
    isLoading.value = false
  }
})

const toBoardList = () => {
  router.go(-1)
}

// ----------------------- Add -----------------------

const submitForm = async () => {
  const statusName = status.value.name.trim()
  const statusDescription = status.value.description.trim()
  const itemAdd = await addItem(baseUrlStatus, {
    name: statusName,
    description: statusDescription
  })
  if (statusExists(statusName)) {
    setTimeout(() => {
      notAdd.value = false
    }, 1800)
    notAdd.value = true
    return (errorAdd.value = "Status name already exists")
  }
  myStatuses.addStatus(
    itemAdd.id,
    itemAdd.name,
    itemAdd.description,
    itemAdd.createdOn,
    itemAdd.updateOn
  )
  showAlertAfterAdd.value = true
  setTimeout(() => {
    showAlertAfterAdd.value = false
  }, 2300)
  closeModalAdd()
  clearForm()
}

const clearForm = () => {
  status.value.name = ""
  status.value.description = ""
}

const closeModalAdd = () => {
  clearForm()
  const modal = document.getElementById("my_modal_4")
  modal.close()
  router.go(-1)
}

const selectStatus = (statusId) => {
  selectedStatusId.value = statusId
}

// ----------------------- Edit -----------------------
const originalStatus = ref({})

const UpdateStatus = async () => {
  const statusName = status.value.name
  const statusDescription = status.value.description
  const statusId = status.value.id

  const edit = await editItem(baseUrlStatus, statusId, {
    name: statusName,
    description: statusDescription
  })

  if (statusExists(statusName, statusId)) {
    setTimeout(() => {
      notEdit.value = false
    }, 1800)
    notEdit.value = true
    return (errorEdit.value = "Status name already exists")
  }

  myStatuses.updateStatus(
    edit.id,
    edit.name,
    edit.description,
    edit.createdOn,
    edit.updateOn
  )

  const statusIndex = statusList.value.findIndex(
    (status) => status.id === statusId
  )
  if (statusIndex !== -1) {
    statusList.value[statusIndex] = { ...edit }
  }
  showAlertEdit.value = true
  showAlertAfterEdit.value = true
  setTimeout(() => {
    showAlertAfterEdit.value = false
  }, 2300)
  router.go(-1)
}

const openModalToEdit = (statusId) => {
  const statusToEdit = statusList.value.find((item) => item.id === statusId)
  status.value = { ...statusToEdit }
  originalStatus.value = { ...statusToEdit }
  const modal = document.getElementById("my_modal_edit")
  modal.showModal()
  router.push({ name: "EditStatus", params: { statusid: statusId } })
}

const closeModalEdit = () => {
  const modal = document.getElementById("my_modal_edit")
  modal.close()
  router.go(-1)
  clearForm()
}

const isEdited = computed(() => {
  return (
    (status.value.name !== originalStatus.value.name ||
      status.value.description !== originalStatus.value.description) &&
    (status.value.name.trim().length > 0 ||
      status.value.description.trim().length > 0)
  )
})

const statusExists = (name, id) => {
  return statusList.value.some(
    (status) =>
      status.name?.trim().toLowerCase() === name?.trim().toLowerCase() &&
      status.id !== id
  )
}

// ----------------------- Delete -----------------------

const getNameById = (id) => {
  const item = statusList.value.find((item) => item.id === id)
  if (item) {
    return item.name
  } else {
    return null
  }
}

const openAdd = () => {
  router.push({ name: "AddStatus" })
}

const selectedItemIdToDelete = ref(0)

const deleteStatus = async (statusId) => {
  try {
    const status = await deleteItemById(baseUrlStatus, statusId)
    if (status === 200) {
      myStatuses.removeStatus(selectedItemIdToDelete.value)
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
      showAlertDelete.value = true
      showAlertAfterDelete.value = true
      setTimeout(() => {
        showAlertAfterDelete.value = false
      }, 2300)
    } else {
      console.error(`Failed to delete item with ID ${statusId}`)
    }

    if (status === 400) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
      openModalToDeleteTrans(statusId)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${statusId}:`, error)
  }
}

const openModalToDelete = (statusId) => {
  selectedItemIdToDelete.value = statusId
  openModalDelete.value = true
}

const confirmDelete = () => {
  deleteStatus(selectedItemIdToDelete.value)
  openModalDelete.value = false
}

const openModalToDeleteTrans = (statusId) => {
  selectedItemIdToDelete.value = statusId
  const modal3 = document.getElementById("my_modal_deleteTrans")
  modal3?.showModal()
}

const closeModalTrans = () => {
  const modal3 = document.getElementById("my_modal_deleteTrans")
  modal3?.close()
}
const confirmDeleteTrans = (statusId) => {
  if (isLimitReached.value) {
    console.error(error.value)
    return
  }
  deleteandtrans(selectedItemIdToDelete.value, statusId)
  closeModalTrans()
}

const deleteandtrans = async (statusId, newID) => {
  try {
    const status = await deleteItemAndTransfer(baseUrlStatus, statusId, newID)
    if (status === 200) {
      myStatuses.removeStatus(selectedItemIdToDelete.value)
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
    } else if (status === 400) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
      if (isLimitReached.value) {
        console.error(errorLimit.value)
      }
      errorTrans.value = true
      setTimeout(() => {
        errorTrans.value = false
      }, 2300)
    } else {
      console.error(`Failed to delete item with ID ${statusId}`)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${statusId}:`, error)
  }
}

// ----------------------- Limit -----------------------

const isLimitReached = computed(() => {
  const status = todo.value.status
  if (status === "No Status" || status === "Done") {
    return false
  }
  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = taskStore
      .getTasks()
      .filter((task) => task.status === status)
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      return true
    }
  }
  return false
})

// ----------------------- Validate -----------------------

const isValidName = (name) => {
  return name && name.trim().length > 0 && name.trim().length <= 50
}

const isValidDescription = (description) => {
  return !description || description.trim().length <= 200
}

const isFormValid = computed(() => {
  return (
    isValidName(status.value.name) &&
    isValidDescription(status.value.description)
  )
})


</script>

<template>
  <div v-if="isLoading">
    <LodingPage />
  </div>

  <div v-else class="flex flex-col h-screen overflow-hidden">
    <!-- Sidebar -->
    <div class="flex flex-1 overflow-hidden">
      <SideBar />

      <!-- Main content -->
      <div class="flex flex-col flex-1">
        <!-- Navbar -->

        <Navbar> IT Bangmod Kradan Kanbun </Navbar>

        <div class="mt-9 px-10 flex justify-between items-center">
          <div class="text-sm breadcrumbs">
            <ul>
              <li>
                <a @click="toBoardList()">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 256 256"
                    class="w-4 h-4 stroke-current mr-2"
                  >
                    <path
                      fill="currentColor"
                      d="m219.31 108.68l-80-80a16 16 0 0 0-22.62 0l-80 80A15.87 15.87 0 0 0 32 120v96a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-56h32v56a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-96a15.87 15.87 0 0 0-4.69-11.32M208 208h-48v-56a8 8 0 0 0-8-8h-48a8 8 0 0 0-8 8v56H48v-88l80-80l80 80Z"
                    />
                  </svg>
                  <span class="itbkk-board-name">{{ boardName }}</span>
                </a>
              </li>
              <li>
                <a>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
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
                  <span class="font-extrabold">Task Status</span>
                </a>
              </li>
            </ul>
          </div>

          <button
            :disabled="disabledButtonWhileOpenPublic"
            :class="[
              'itbkk-button-add',
              'btn',
              { 'btn-disabled': disabledButtonWhileOpenPublic }
            ]"
            :style="{
              backgroundColor: disabledButtonWhileOpenPublic
                ? '#d3d3d3'
                : '#F785B1',
              color: disabledButtonWhileOpenPublic ? '#a9a9a9' : 'white',
              borderRadius: '30px',
              position: 'static',
              cursor: disabledButtonWhileOpenPublic ? 'not-allowed' : 'pointer',
              opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
            }"
            onclick="my_modal_4.showModal() "
            @click="openAdd()"
            class="itbkk-button-add btn ml-4"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z"
              />
            </svg>
            Add new status
          </button>
        </div>

        <div class="flex items-center">
          <div class="hidden md:block">
            <div class="flex space-x-1.5"></div>
          </div>
        </div>

        <div class="flex flex-col items-center mt-1">
          <div class="overflow-x-auto">
            <div class="min-w-full">
              <!-- HOME -->

              <!-- Add Alert -->
              <Alert :isAlertSuccess="showAlertAfterAdd">
                The Status has been successfully added
              </Alert>

              <Alert :isAlertFailure="notAdd">
                {{ errorAdd }}
              </Alert>

              <!-- Edit Alert Success -->

              <Alert :isAlertSuccess="showAlertAfterEdit">
                The Status has been successfully edited
              </Alert>

              <Alert :isAlertFailure="notEdit">
                {{ errorEdit }}
              </Alert>

              <!-- Delete Alert -->
              <Alert :isAlertSuccess="showAlertAfterDelete">
                The Status has been deleted
              </Alert>
              <Alert :isAlertFailure="errorTrans">
                "Cannot transfer to status since it will exceed the limit.
                Please choose another status to transfer to."
              </Alert>
              <Alert :isAlertFailure="notFound">
                Error! Status not found
              </Alert>

              <!-- LimitAlert -->
              <Alert :isAlertFailure="limitAlert">
                {{ errorLimit }}
              </Alert>

              <!-- TABLE -->
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
                      Description
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
                    class="itbkk-item"
                    v-for="(item, index) in myStatuses.getStatuses()"
                    :key="index"
                  >
                    <!-- ID -->
                    <td
                      class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{ index + 1 }}
                    </td>
                    <!-- NAME -->

                    <td class="">
                      <label
                        class="itbkk-status-name"
                        for="my_modal_6"
                        @click="selectStatus(item.id)"
                      >
                        {{ item.name }}
                      </label>
                    </td>

                    <td
                      class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700"
                      :class="{
                        italic:
                          !item.description || item.description?.length === 0
                      }"
                    >
                      <label
                        for="my_modal_6"
                        :class="{
                          italic:
                            !item.description || item.description?.length === 0
                        }"
                      >
                        {{
                          !item.description || item.description.length === 0
                            ? "No description is provided"
                            : item.description
                        }}
                      </label>
                    </td>

                    <!-- Edit modal-->

                    <td
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status"
                    >
                      <button
                        :disabled="disabledButtonWhileOpenPublic"
                        :class="[
                          'itbkk-button-edit',
                          'btn',
                          'rounded-full',
                          { 'btn-disabled': disabledButtonWhileOpenPublic }
                        ]"
                        :style="{
                          backgroundColor: disabledButtonWhileOpenPublic
                            ? '#d3d3d3'
                            : '#fae59d',
                          color: disabledButtonWhileOpenPublic
                            ? '#a9a9a9'
                            : 'white',
                          borderRadius: '30px',
                          position: 'relative',
                          cursor: disabledButtonWhileOpenPublic
                            ? 'not-allowed'
                            : 'pointer',
                          opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
                        }"
                        @click="openModalToEdit(item.id)"
                        v-if="item.name !== 'No Status' && item.name !== 'Done'"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="18"
                          height="18"
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

                      <dialog id="my_modal_edit" class="modal">
                        <div
                          class="modal-box w-full md:w-11/12 max-w-5xl mx-auto"
                          :key="index"
                        >
                          <span
                            class="block text-2xl font-bold leading-6 text-gray-900 mb-1"
                            style="margin: 15px"
                          ></span>

                          <!-- Modal content -->
                          <div
                            class="modal-action flex flex-col justify-between"
                          >
                            <div
                              class="modal-content py-4 text-left px-6 flex-grow flex flex-col"
                            >
                              <div class="label">
                                <span
                                  class="block text-lg font-bold leading-6 text-gray-900 mb-1 ml-4"
                                  >Name
                                </span>
                              </div>

                              <label
                                class="input input-bordered flex items-center gap-2 font-bold ml-4"
                              >
                                <input
                                  type="text"
                                  class="itbkk-status-name grow"
                                  placeholder="Enter Your Status Name"
                                  maxlength="100"
                                  v-model="status.name"
                                />
                              </label>
                              <p
                                class="text-sm text-gray-400 mb-2 mt-2"
                                style="text-align: right"
                              >
                                {{ status.name?.length }}/50
                              </p>

                              <!-- Description -->

                              <label
                                for="description"
                                class="itbkk-status-description form-control flex-grow ml-4"
                              >
                                <div class="label">
                                  <span
                                    class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                                    >Description</span
                                  >
                                </div>

                                <textarea
                                  id="description"
                                  class="textarea textarea-bordered flex-grow w-full"
                                  maxlength="500"
                                  rows="4"
                                  placeholder="No Description Provided"
                                  v-model="status.description"
                                >

                            No description is provided
                          </textarea
                                >
                              </label>
                              <p
                                class="text-sm text-gray-400 mb-2 mt-2"
                                style="text-align: right"
                              >
                                {{ status.description?.length }}/200
                              </p>
                            </div>

                            <!-- Buttons -->
                            <div class="flex justify-end">
                              <form method="dialog">
                                <button
                                  @click="UpdateStatus"
                                  type="submit"
                                  class="itbkk-button-confirm btn mr-2"
                                  :class="{ disabled: !isEdited }"
                                  :disabled="!isFormValid || !isEdited"
                                >
                                  Save
                                </button>
                              </form>
                              <button
                                class="itbkk-button-cancel btn"
                                @click="closeModalEdit()"
                              >
                                Cancel
                              </button>
                            </div>
                          </div>
                        </div>
                      </dialog>

                      <!-- Delete Modal -->

                      <button
                        :disabled="disabledButtonWhileOpenPublic"
                        :class="[
                          'itbkk-button-delete ml-2',
                          'btn',
                          'rounded-full',
                          { 'btn-disabled': disabledButtonWhileOpenPublic }
                        ]"
                        :style="{
                          backgroundColor: disabledButtonWhileOpenPublic
                            ? '#d3d3d3'
                            : '#f87171',
                          color: disabledButtonWhileOpenPublic
                            ? '#a9a9a9'
                            : 'white',
                          borderRadius: '30px',
                          position: 'relative',
                          cursor: disabledButtonWhileOpenPublic
                            ? 'not-allowed'
                            : 'pointer',
                          opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
                        }"
                        v-if="item.name !== 'No Status' && item.name !== 'Done'"
                        class="itbkk-button-delete btn bg-red-400 rounded-full"
                        style="margin-left: 10px"
                        @click="openModalToDelete(item.id)"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="18"
                          height="18"
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
                      <ConfirmModal
                        :openModal="openModalDelete"
                        @confirm="confirmDelete()"
                        @cancel="openModalDelete = false"
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
                            Do you want to delete
                            {{ getNameById(selectedItemIdToDelete) }} ?
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
                     

                      <!-- Delete And Trans -->
                      <dialog id="my_modal_deleteTrans" class="modal">
                        <div class="modal-box" style="max-width: 500px">
                          <h3
                            class="itbkk-message font-bold text-lg"
                            style="color: #9391e4"
                          >
                            Transfer a Status
                          </h3>
                          <p
                            class="py-4 font-medium"
                            style="word-wrap: break-word"
                          >
                            There is some task associated with the ...
                          </p>

                          <div
                            class="itbkk-status mb-4 mt-2 w-full flex flex-col items-center"
                          >
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-2 text-center"
                              >Transfer a task to</span
                            >
                            <select
                              v-model="status.id"
                              class="select select-bordered w-full max-w-xs mt-1"
                              style="text-align: center"
                            >
                              <option
                                v-for="status in statusList"
                                :value="status.id"
                              >
                                {{ status.name }}
                              </option>
                            </select>

                            <div
                              class="flex justify-center w-full max-w-xs mt-4 space-x-5"
                            >
                              <button
                                class="itbkk-button-cancel btn"
                                @click="closeModalTrans"
                                style="color: #eb4343"
                              >
                                Cancel
                              </button>
                              <button
                                class="itbkk-button-confirm btn bg-green-400"
                                style="color: #fff"
                                @click="confirmDeleteTrans(status.id)"
                              >
                                Confirm
                              </button>
                            </div>
                          </div>
                        </div>
                      </dialog>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="min-h-full max-h-fit">
          <div class="flex items-center">
            <div class="hidden md:block">
              <div class="flex space-x-1.5"></div>
            </div>
          </div>
        </div>

        <div>
          <!-- Add new status -->
          <dialog id="my_modal_4" class="itbkk-modal-status modal">
            <div class="modal-box w-full md:w-11/12 max-w-5xl mx-auto">
              <span
                class="block text-2xl font-bold leading-6 mb-1"
                style="margin: 15px; color: #9391e4; text-align: center"
                >Add Status</span
              >

              <!-- Modal content -->
              <div class="modal-action flex flex-col justify-between">
                <!-- Name -->
                <div class="modal-content py-4 text-left px-6 flex-grow">
                  <div class="label">
                    <span
                      class="block text-lg font-bold leading-6 text-gray-900 mb-1 ml-4"
                      >Name
                    </span>
                  </div>

                  <label
                    class="input input-bordered flex items-center gap-2 font-bold ml-4"
                  >
                    <input
                      type="text"
                      class="itbkk-status-name grow"
                      placeholder="Enter Your Status Name"
                      v-model="status.name"
                    />
                  </label>
                  <p
                    class="text-sm text-gray-400 mb-2 mt-2"
                    style="text-align: right"
                  >
                    {{ status.name?.length }}/50
                  </p>
                  <!-- Description -->
                  <label for="description" class="form-control flex-grow ml-4">
                    <div class="label">
                      <span
                        class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                        >Description
                      </span>
                    </div>

                    <textarea
                      id="description"
                      class="itbkk-status-description textarea textarea-bordered flex-grow w-full"
                      rows="4"
                      placeholder="No Description Provided"
                      v-model="status.description"
                    ></textarea>
                  </label>
                  <p
                    class="text-sm text-gray-400 mb-2 mt-2"
                    style="text-align: right"
                  >
                    {{ status.description?.length }}/200
                  </p>
                </div>

                <!-- Buttons -->
                <div class="flex justify-end">
                  <button
                    class="itbkk-button-cancel btn"
                    style="margin: 10px"
                    @click="closeModalAdd()"
                  >
                    Cancel
                  </button>
                  <form form @submit.prevent="submitForm" method="dialog">
                    <button
                      type="submit"
                      class="itbkk-button-confirm btn mr-2"
                      style="flex: 3; margin: 10px; background-color: #f785b1"
                      :disabled="!isFormValid"
                    >
                      Save
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </dialog>
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
