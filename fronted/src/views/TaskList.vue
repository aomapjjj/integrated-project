<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import {
  getItemById,
  getItems,
  deleteItemById,
  editLimit,
  getBoardById,
  getAttachments
} from '../libs/fetchUtils.js'
import TaskDetail from './tasks/TaskDetail.vue'
import AddTask from './tasks/AddTask.vue'
import EditTask from './tasks/EditTask.vue'
import { boardVisibility } from '../libs/fetchUtils.js'
import { useLimitStore } from '../stores/storeLimit.js'
import { useUsers } from '@/stores/storeUser'
import { useTasks } from '../stores/store.js'
import { useRoute, useRouter } from 'vue-router'
import SideBar from '../component/bar/SideBar.vue'
import Modal from '../component/modal/Modal.vue'
import Alert from '@/component/alert/Alert.vue'
import Navbar from '@/component/bar/Navbar.vue'
import LodingPage from '@/component/ui/LodingPage.vue'

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const taskStore = useTasks()
const limitStore = useLimitStore()
const userStore = useUsers()

// ----------------------- Params -----------------------

const boardId = ref()
const userName = userStore.getUser().username
const token = localStorage.getItem('access_token')
const boardName = ref('')
const isLoading = ref(true)
watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

// ----------------------- List Items -----------------------

const attachmentsNumber = ref()
const todoList = ref([])
const statusList = ref([])
let items = []

// ----------------------- Selected Items -----------------------

const selectedTodoId = ref(0)
const selectedItemIdToDelete = ref(0)
const indexDelete = ref(0)

// ----------------------- Alerts -----------------------

const deleteComplete = ref(false)
const alertLimit = ref(false)
const alertEnabledFail = ref(false)
const alertEnabledSuc = ref(false)
const messageLimit = ref('')
const messageAlert = ref()

// ----------------------- Enable & Disable -----------------------

const showDetail = ref(false)
const disabledButtonWhileOpenPublic = ref(false)
const isModalVisible = ref(false)
const visibility = ref('')
const tempVisibility = ref('')

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlTask = `${baseUrlboards}/${boardId.value}/tasks`
const baseUrlStatus = `${baseUrlboards}/${boardId.value}/statuses`
const baseUrlLimit = `${baseUrlboards}/${boardId.value}/statuses/limit`
const baseUrlLimitMax = `${baseUrlboards}/${boardId.value}/statuses/maximumtask`

onMounted(async () => {
  try {
    isLoading.value = true
    userStore.setToken(token)

    if (taskStore.getTasks().length === 0) {
      const items = await getItems(baseUrlTask)
      taskStore.addTasks(items)
    }

    for (const task of taskStore.getTasks()) {
      const { statusCode, data } = await getAttachments(boardId.value, task.id)
      taskStore.updateAttachments(task.id, data)
    }

    const board = await getBoardById(boardId.value)

    if (!userName) {
      disabledButtonWhileOpenPublic.value = true
    } else if (
      board.item.owner.name !== userName &&
      !board.item.collaborators.some(
        (collab) => collab.name === userName && collab.accessRight === 'WRITE'
      )
    ) {
      disabledButtonWhileOpenPublic.value = true
    } else {
      disabledButtonWhileOpenPublic.value = false
    }
    boardName.value = board.item.name
    visibility.value = board.item.visibility
    todoList.value = items

    const itemsStatus = await getItems(baseUrlStatus)
    statusList.value = itemsStatus

    const itemLimit = await getItems(baseUrlLimit)
    limitStore.setLimit(itemLimit)

    const taskId = route.params.id
    if (taskId !== undefined) {
      const response = await getItemById(taskId)
      if (response && (response.status === 404 || response.status === 400)) {
        router.push('/error')
      }
    }
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    isLoading.value = false
  }

  console.log(taskStore.getTasks())
})

// ----------------------- Edit Limit -----------------------

const UpdateLimit = async () => {
  const updatedLimit = await editLimit(
    baseUrlLimitMax,
    limitStore.getLimit().maximumTask,
    limitStore.getLimit().isLimit
  )
  limitStore.updateLimit(
    updatedLimit.maximumTask,
    updatedLimit.limitMaximumtask
  )
  alertLimit.value = limitStore.getLimit().isLimit

  if (alertLimit.value) {
    messageLimit.value =
      'The Kanban board now limits ' + limitStore.getLimit().maximumTask
  } else {
    alertLimit.value = true
    messageLimit.value = 'The Kanban board has disabled the task limit in each '
  }
  setTimeout(() => {
    alertLimit.value = false
  }, 3000)
}

// ----------------------- Delete -----------------------

const selectTodo = (todoId) => {
  if (todoId !== 0) {
    selectedTodoId.value = todoId
    showDetail.value = true
    console.log('Opening TaskDetail with ID:', todoId)
    router.push({ name: 'TaskDetail', params: { taskid: todoId } })
  }
}

const deleteTodo = async (todoId, index) => {
  try {
    const status = await deleteItemById(baseUrlTask, todoId)
    if (status === 200) {
      todoList.value.splice(index, 1)
    } else {
      console.error(`Failed to delete item with ID ${todoId}`)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${todoId}:`, error)
  }
}

const openModalToDelete = (itemId, index) => {
  selectedItemIdToDelete.value = itemId
  indexDelete.value = index
  const modal = document.getElementById('my_modal_delete')
  modal.showModal()
}

const confirmDelete = () => {
  taskStore.removeTask(selectedItemIdToDelete.value)
  deleteTodo(selectedItemIdToDelete.value, indexDelete.value)
  deleteComplete.value = true
  setTimeout(() => {
    deleteComplete.value = false
  }, 2300)
}

// ----------------------- filterAndLogTitleById -----------------------

const filterAndLogTitleById = (id) => {
  const item = items.find((item) => item.id === id)
  if (item) {
    return item.title
  } else {
    return ''
  }
}

// ----------------------- STATUS SORT -----------------------
const showIcon = ref('default')
const statusSortOrder = ref('default')

const toggleIcon = () => {
  if (showIcon.value === 'default') {
    showIcon.value = 'asc'
    statusSortOrder.value = 'asc'
  } else if (showIcon.value === 'asc') {
    showIcon.value = 'desc'
    statusSortOrder.value = 'desc'
  } else {
    showIcon.value = 'default'
    statusSortOrder.value = 'default'
  }
  sortByStatus()
}

const sortByStatus = () => {
  const currentSortOrder = statusSortOrder.value
  if (currentSortOrder === 'asc') {
    taskStore.getTasks().sort((a, b) => a.status.localeCompare(b.status))
  } else if (currentSortOrder === 'desc') {
    taskStore.getTasks().sort((a, b) => b.status.localeCompare(a.status))
  } else {
    taskStore.getTasks().sort((a, b) => a.id - b.id)
  }
}

// ----------------------- Filter -----------------------

const filter = ref([])

const filteredTasks = computed(() => {
  if (filter.value.length === 0) {
    return taskStore.getTasks()
  }
  return taskStore
    .getTasks()
    .filter((task) => filter.value.includes(task.status))
})

const removeStatus = (status) => {
  const index = filter.value.indexOf(status)
  if (index > -1) {
    filter.value.splice(index, 1)
  }
}

// ----------------------- Filter -----------------------

const openNewStatus = () => {
  router.push({ name: 'StatusesList' })
}

const openCollaborator = () => {
  router.push({ name: 'Collab' })
}

const clearToken = () => {
  router.push({ name: 'Login' })
  localStorage.removeItem('access_token')
}

// ----------------------- Limit ---------------------------

const closeLimit = () => {
  my_modal_limit.close()
}

const handleToggleClick = () => {
  if (disabledButtonWhileOpenPublic.value) {
    return
  }
  tempVisibility.value = visibility.value === 'PUBLIC' ? 'PRIVATE' : 'PUBLIC'
  isModalVisible.value = true
}

const confirmChangeVisibility = async () => {
  visibility.value = tempVisibility.value
  isModalVisible.value = false
  await changeVisibility()
}

const cancelChange = () => {
  isModalVisible.value = false
}

if (userStore.getLoginSuccess() && !alertEnabledSuc.value) {
  alertEnabledSuc.value = true
  messageAlert.value = 'Welcome, You have logged in successfully'

  setTimeout(() => {
    alertEnabledSuc.value = false
    userStore.setLoginSuccess(false)
  }, 3000)
}

const changeVisibility = async () => {
  const updatedBoard = await boardVisibility(
    boardId.value,
    visibility.value === 'PUBLIC' ? 'PRIVATE' : 'PUBLIC'
  )

  if (updatedBoard && updatedBoard.success) {
    alertEnabledSuc.value = true
    setTimeout(() => {
      alertEnabledSuc.value = false
    }, 3000)
    visibility.value = updatedBoard.visibility
    messageAlert.value = `Visibility updated to: ${updatedBoard.visibility} `
  } else {
    messageAlert.value = updatedBoard.message
    alertEnabledFail.value = true
    setTimeout(() => {
      alertEnabledFail.value = false
    }, 3000)
  }
}
</script>

<template>
  <div v-if="isLoading">
    <LodingPage />
  </div>

  <div v-else class="min-h-full max-h-fit">
    <!-- Modal -->

    <div class="min-h-screen flex">
      <!-- Sidebar -->
      <SideBar />
      <!-- Main Content -->
      <div class="flex-1 flex flex-col">
        <!-- Navbar -->
        <Navbar>
          {{ boardName }}
        </Navbar>

        <div class="flex mt-9 px-6">
          <!-- LIMIT -->
          <button
            class="itbkk-status-setting btn mr-2 mt-1"
            style="border-radius: 30px; background-color: #aff3c9"
            onclick="my_modal_limit.showModal()"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4"
              fill="none"
              viewBox="0 0 14 14"
            >
              <path
                fill="white"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M13.43 3.59a.76.76 0 0 0-.35-.51l-2 2a1 1 0 0 1-1.44 0l-.76-.68a1 1 0 0 1 0-1.4l2-2a.76.76 0 0 0-.48-.43A3.8 3.8 0 0 0 6.26 6L.8 11.41a1 1 0 0 0 0 1.43l.36.36a1 1 0 0 0 1.43 0l5.46-5.45a3.81 3.81 0 0 0 5.38-4.16Z"
              />
            </svg>
          </button>
          <dialog
            id="my_modal_limit"
            class="modal modal-bottom sm:modal-middle"
          >
            <div class="modal-box" style="max-width: 400px; width: 100%">
              <h3 class="font-bold text-lg" style="color: #9391e4">
                Status Settings
              </h3>
              <p class="py-4">
                User can limit the number of tasks in status by setting the
                Maximum task in each status
                <br /><span style="color: #eb4343"
                  >( except "No Status" and "Done" statuses )</span
                >
              </p>
              <hr />
              <div class="flex items-center mt-4 justify-center">
                <span class="mr-2">Limit tasks in this status</span>

                <input
                  type="checkbox"
                  class="toggle"
                  v-model="limitStore.getLimit().isLimit"
                />
              </div>

              <div
                v-if="limitStore.getLimit().isLimit"
                class="mt-4 flex flex-col items-center"
              >
                <div class="flex items-center justify-center">
                  <label for="status-limit" class="mr-2"
                    >Set maximum tasks</label
                  >
                </div>

                <input
                  type="number"
                  id="status-limit"
                  class="input input-bordered input-centered"
                  v-model.number="limitStore.getLimit().maximumTask"
                  max="10"
                  min="1"
                />
              </div>

              <div class="modal-action">
                <form method="dialog">
                  <button
                    class="btn mr-2 bg-green-400 text-w"
                    @click="UpdateLimit"
                  >
                    Confirm
                  </button>

                  <button
                    class="btn mr-2 bg-grey-400 text-w"
                    @click="closeLimit()"
                  >
                    Close
                  </button>
                </form>
              </div>
            </div>
          </dialog>
          <!-- <TaskDetail :todo-id="selectedTodoId" :isOpenModal="showDetail" /> -->

          <!-- Limit alert -->
          <Alert :is-alert-success="alertLimit">
            {{ messageLimit }}
          </Alert>

          <!-- FILTER -->
          <details class="dropdown">
            <summary
              class="itbkk-status-filter m-1 btn"
              style="border-radius: 30px"
            >
              <button>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4"
                  fill="none"
                  viewBox="0 0 14 14"
                >
                  <g
                    fill="#9FC3E9"
                    stroke="currentColor"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <circle cx="2" cy="2" r="1.5" />
                    <path d="M3.5 2h10" />
                    <circle cx="7" cy="7" r="1.5" />
                    <path d="M.5 7h5m3 0h5" />
                    <circle cx="12" cy="12" r="1.5" />
                    <path d="M10.5 12H.5" />
                  </g>
                </svg>
              </button>
              Filter
            </summary>

            <ul
              class="itbkk-status-filter p-2 shadow menu dropdown-content z-[1] bg-base-100 rounded-box w-52"
            >
              <li
                v-for="status in statusList"
                :key="status.name"
                class="flex items-center"
              >
                <label
                  class="itbkk-status-choice flex items-center space-x-2 w-full"
                >
                  <input
                    type="checkbox"
                    :value="status.name"
                    v-model="filter"
                    class="mr-2"
                  />
                  <span class="itbkk-status-choice">{{ status.name }}</span>
                </label>
              </li>
            </ul>
          </details>

          <div class="selected-filters flex flex-wrap mt-2">
            <div
              v-for="status in filter"
              :key="status"
              class="selected-filter text-gray-900 rounded-full px-4 py-2 ml-4 mb-3 flex items-center"
              style="background-color: rgb(247, 133, 177)"
            >
              <span>{{ status }}</span>
              <button
                @click="removeStatus(status)"
                class="ml-2 text-gray-900 hover:text-white"
              >
                &times;
              </button>
            </div>
          </div>

          <div class="flex-grow flex justify-end items-center">
            <div class="hidden md:block">
              <!-- Limit Notice -->
              <div class="flex space-x-1">
                <!-- Add Task Button and Tooltip-->
                <div class="relative group">
                  <div
                    v-show="disabledButtonWhileOpenPublic"
                    class="tooltip tooltip-error group-hover:opacity-100 opacity-0 transition-opacity duration-200 ease-in-out absolute bottom-full mb-2 px-3 py-2 text-xs text-gray-600 bg-white border border-gray-200 rounded-md shadow-lg w-max"
                  >
                    <span class="text-red-400"
                      >You need to be board owner or have write access to
                      perform this action</span
                    >
                    <div class="tooltip-arrow bg-white"></div>
                  </div>
                  <AddTask :disabledBtn="disabledButtonWhileOpenPublic" />
                </div>

                <button
                  class="itbkk-manage-Collaboator btn text-white rounded-full bg-orange-300"
                  @click="openCollaborator()"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                  >
                    <g
                      fill="none"
                      stroke="currentColor"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                    >
                      <path
                        d="M21 13v-2a1 1 0 0 0-1-1h-.757l-.707-1.707l.535-.536a1 1 0 0 0 0-1.414l-1.414-1.414a1 1 0 0 0-1.414 0l-.536.535L14 4.757V4a1 1 0 0 0-1-1h-2a1 1 0 0 0-1 1v.757l-1.707.707l-.536-.535a1 1 0 0 0-1.414 0L4.929 6.343a1 1 0 0 0 0 1.414l.536.536L4.757 10H4a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h.757l.707 1.707l-.535.536a1 1 0 0 0 0 1.414l1.414 1.414a1 1 0 0 0 1.414 0l.536-.535l1.707.707V20a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-.757l1.707-.708l.536.536a1 1 0 0 0 1.414 0l1.414-1.414a1 1 0 0 0 0-1.414l-.535-.536l.707-1.707H20a1 1 0 0 0 1-1"
                      />
                      <path d="M12 15a3 3 0 1 0 0-6a3 3 0 0 0 0 6" />
                    </g>
                  </svg>
                  Manage Collaboator
                </button>
                <!-- MANAGE STATUS -->
                <button
                  class="itbkk-manage-status btn text-white rounded-full bg-customPink"
                  @click="openNewStatus()"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                  >
                    <g
                      fill="none"
                      stroke="currentColor"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                    >
                      <path
                        d="M21 13v-2a1 1 0 0 0-1-1h-.757l-.707-1.707l.535-.536a1 1 0 0 0 0-1.414l-1.414-1.414a1 1 0 0 0-1.414 0l-.536.535L14 4.757V4a1 1 0 0 0-1-1h-2a1 1 0 0 0-1 1v.757l-1.707.707l-.536-.535a1 1 0 0 0-1.414 0L4.929 6.343a1 1 0 0 0 0 1.414l.536.536L4.757 10H4a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h.757l.707 1.707l-.535.536a1 1 0 0 0 0 1.414l1.414 1.414a1 1 0 0 0 1.414 0l.536-.535l1.707.707V20a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-.757l1.707-.708l.536.536a1 1 0 0 0 1.414 0l1.414-1.414a1 1 0 0 0 0-1.414l-.535-.536l.707-1.707H20a1 1 0 0 0 1-1"
                      />
                      <path d="M12 15a3 3 0 1 0 0-6a3 3 0 0 0 0 6" />
                    </g>
                  </svg>
                  Manage Status
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="flex flex-col items-center mt-9 h-[60vh] max-sm:h-[50vh]">
          <!-- Toggle Public / Private -->
          <div class="flex mb-4 items-end relative group justify-center">
            <!-- Tooltip -->
            <div
              v-show="disabledButtonWhileOpenPublic"
              class="tooltip tooltip-error group-hover:opacity-100 opacity-0 transition-opacity duration-200 ease-in-out absolute bottom-full mb-2 px-3 py-2 text-xs text-gray-600 bg-white border border-gray-200 rounded-md shadow-lg w-max"
            >
              <span class="text-red-400">
                You need to be board owner or have write access to perform this
                action
              </span>
              <div class="tooltip-arrow bg-white"></div>
            </div>
            <div
              class="absolute pl-12 w-1/12 h-11 z-10 bg-transparent"
              @click="handleToggleClick"
            ></div>
            <label class="relative inline-flex cursor-pointer items-center">
              <input
                type="checkbox"
                :checked="visibility == 'PRIVATE'"
                class="itbkk-board-visibility peer sr-only"
                :disabled="disabledButtonWhileOpenPublic"
              />
              <div
                class="peer flex h-8 w-30 items-center gap-4 rounded-full bg-orange-400 px-3 after:absolute after:left-1 after:h-6 after:w-14 after:rounded-full after:bg-white/40 after:transition-all after:content-[''] peer-checked:bg-stone-500 peer-checked:after:translate-x-full peer-focus:outline-none dark:border-slate-600 dark:bg-slate-700 text-sm text-white"
                :class="
                  disabledButtonWhileOpenPublic ? 'cursor-not-allowed' : ''
                "
              >
                <span> Public </span>
                <span> Private </span>
              </div>
            </label>
          </div>

          <TaskDetail
            :todo-id="selectedTodoId"
            :isOpenModal="showDetail"
            @close="showDetail = false"
          />

          <Alert
            :isAlertFailure="alertEnabledFail"
            :isAlertSuccess="alertEnabledSuc"
          >
            {{ messageAlert }}
          </Alert>

          <Modal
            :isOpen="isModalVisible"
            :tempVisibility="tempVisibility"
            @confirm="confirmChangeVisibility"
            @cancel="cancelChange"
          >
            <template #headerName>Board visibility changed!</template>
            <template #messageName>
              {{
                tempVisibility === 'PUBLIC'
                  ? 'In public, any one can view the board, task list and task detail of tasks in the board. Do you want to change the visibility to Public ? '
                  : 'In private, only board owner can access / control board. Do you want to change the visibility toPrivate ? '
              }}</template
            >
          </Modal>

          <div class="overflow-x-auto max-h-96 w-full mx-auto px-2">
            <div class="w-full mx-auto max-w-screen-lg">
              <table
                class="table-auto w-full mx-auto"
                style="table-layout: fixed"
              >
                <!-- TABLE -->
                <thead>
                  <tr class="bg-base-200 mt-4 md:mt-0">
                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
                        color: #fff;
                      "
                    >
                      No.
                    </th>
                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
                        color: #fff;
                      "
                    >
                      Title
                    </th>
                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
                        color: #fff;
                      "
                    >
                      Assignees
                    </th>
                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
                        color: #fff;
                      "
                    >
                      Attachments
                    </th>

                    <!-- STATUS SORT -->

                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
                        color: #fff;
                      "
                    >
                      <button
                        class="itbkk-status-sort"
                        style="display: flex; align-items: center"
                        @click="sortByStatus(), toggleIcon()"
                      >
                        <div class="mr-2">Status</div>
                        <!-- Default -->
                        <svg
                          v-if="showIcon === 'default'"
                          xmlns="http://www.w3.org/2000/svg"
                          width="20"
                          height="20"
                          viewBox="0 0 24 24"
                        >
                          <g fill="none" fill-rule="evenodd">
                            <path
                              d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                            />
                            <path
                              fill="currentColor"
                              d="M10.759 13c.94 0 1.43 1.092.855 1.792l-.078.086L7.414 19H11a1 1 0 0 1 .117 1.993L11 21H5.241c-.94 0-1.43-1.092-.855-1.792l.078-.086L8.586 15H5a1 1 0 0 1-.117-1.993L5 13zM17 4a1 1 0 0 1 1 1v12.414l1.121-1.121a1 1 0 0 1 1.415 1.414l-2.829 2.828a1 1 0 0 1-1.414 0l-2.828-2.828a1 1 0 0 1 1.414-1.414L16 17.414V5a1 1 0 0 1 1-1M8 3c.674 0 1.28.396 1.556 1.002l.054.133l2.332 6.529a1 1 0 0 1-1.838.78l-.046-.108L9.581 10H6.419l-.477 1.336a1 1 0 0 1-1.917-.56l.033-.112l2.332-6.53A1.71 1.71 0 0 1 8 3m0 2.573L7.133 8h1.734z"
                            />
                          </g>
                        </svg>
                        <!-- Asc -->
                        <svg
                          v-else-if="showIcon === 'asc'"
                          xmlns="http://www.w3.org/2000/svg"
                          width="20"
                          height="20"
                          viewBox="0 0 24 24"
                        >
                          <g fill="none" fill-rule="evenodd">
                            <path
                              d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                            />
                            <path
                              fill="#eb4343"
                              d="M10.759 13c.94 0 1.43 1.092.855 1.792l-.078.086L7.414 19H11a1 1 0 0 1 .117 1.993L11 21H5.241c-.94 0-1.43-1.092-.855-1.792l.078-.086L8.586 15H5a1 1 0 0 1-.117-1.993L5 13zM17 4a1 1 0 0 1 1 1v12.414l1.121-1.121a1 1 0 0 1 1.415 1.414l-2.829 2.828a1 1 0 0 1-1.414 0l-2.828-2.828a1 1 0 0 1 1.414-1.414L16 17.414V5a1 1 0 0 1 1-1M8 3c.674 0 1.28.396 1.556 1.002l.054.133l2.332 6.529a1 1 0 0 1-1.838.78l-.046-.108L9.581 10H6.419l-.477 1.336a1 1 0 0 1-1.917-.56l.033-.112l2.332-6.53A1.71 1.71 0 0 1 8 3m0 2.573L7.133 8h1.734z"
                            />
                          </g>
                        </svg>

                        <!-- Des -->
                        <svg
                          v-else
                          xmlns="http://www.w3.org/2000/svg"
                          width="20"
                          height="20"
                          viewBox="0 0 24 24"
                        >
                          <g fill="none" fill-rule="evenodd">
                            <path
                              d="M24 0v24H0V0zM12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036c-.01-.003-.019 0-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z"
                            />
                            <path
                              fill="#4361ee"
                              d="M4.664 11.942a1 1 0 0 0 1.278-.606L6.419 10h3.162l.477 1.336a1 1 0 0 0 1.884-.672L9.61 4.134a1.71 1.71 0 0 0-3.22 0l-2.332 6.53a1 1 0 0 0 .606 1.278M8 5.573L8.867 8H7.133zm8.293-1.28a1 1 0 0 1 1.414 0l2.829 2.828a1 1 0 0 1-1.415 1.415L18 7.414V20a1 1 0 1 1-2 0V7.414l-1.121 1.122a1 1 0 1 1-1.415-1.415zM5 13a1 1 0 1 0 0 2h3.586l-4.122 4.122C3.77 19.815 4.26 21 5.24 21H11a1 1 0 1 0 0-2H7.414l4.122-4.122c.693-.693.203-1.878-.777-1.878z"
                            />
                          </g>
                        </svg>
                      </button>
                    </th>

                    <!-- STATUS SORT -->

                    <th
                      class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9fc3e9;
                        border-bottom: 2px solid #9fc3e9;
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
                  <!-- Iterate over todoList -->
                  <tr
                    class="itbkk-item"
                    v-for="(item, index) in filteredTasks"
                    :key="index"
                  >
                    <td
                      @click="selectTodo(item.id)"
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{ index + 1 }}
                    </td>
                    <td
                      @click="selectTodo(item.id)"
                      class="itbkk-title px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      <label style="display: block; width: 100%; height: 100%">
                        {{ item.title }}
                      </label>
                    </td>
                    <td
                      @click="selectTodo(item.id)"
                      class="itbkk-assignees px-4 py-2 text-center md:text-left text-sm text-gray-700"
                      :class="{
                        italic: !item.assignees || item.assignees.length === 0
                      }"
                    >
                      {{
                        !item.assignees || item.assignees.length === 0
                          ? 'Unassigned'
                          : item.assignees
                      }}
                    </td>
                    <td
                      class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      {{
                        item.attachments &&
                        Array.isArray(item.attachments) &&
                        item.attachments.length > 0
                          ? item.attachments.length
                          : '-'
                      }}
                    </td>

                    <td
                      class="itbkk-status px-4 py-2 text-center md:text-left text-sm text-gray-700"
                    >
                      <span
                        :class="{
                          'badge badge-outline border border-solid w-20 text-xs px-2 py-1': true,
                          'border-blue-500 text-blue-500':
                            item.status === 'No Status',
                          'border-red-500 text-red-500':
                            item.status === 'To Do',
                          'border-yellow-500 text-yellow-500':
                            item.status === 'Doing',
                          'border-green-500 text-green-500':
                            item.status === 'Done'
                        }"
                      >
                        {{ item.status }}
                      </span>
                    </td>

                    <div class="itbkk-button-action">
                      <td class="flex justify-center">
                        <div
                          class="itbkk-button-edit hidden md:table-cell text-sm py-2 relative"
                        >
                          <!-- Edit Button and Tooltip -->
                          <div class="relative group pointer-events-auto">
                            <div
                              v-show="disabledButtonWhileOpenPublic"
                              class="tooltip tooltip-error group-hover:opacity-100 opacity-0 transition-opacity duration-200 ease-in-out absolute bottom-full mb-2 px-3 py-2 text-xs text-gray-600 bg-white border border-gray-200 rounded-md shadow-lg w-max"
                            >
                              <span class="text-red-400"
                                >You need to be board owner or have write access
                                to perform this action</span
                              >
                              <div class="tooltip-arrow bg-white"></div>
                            </div>
                            <EditTask
                              :todo-id="item.id"
                              :disabledBtn="disabledButtonWhileOpenPublic"
                            />
                          </div>
                        </div>

                        <div
                          class="itbkk-button-delete hidden md:table-cell text-sm py-2 relative"
                        >
                          <!-- Delete Button and Tooltip -->
                          <div class="relative group">
                            <div
                              v-show="disabledButtonWhileOpenPublic"
                              class="tooltip tooltip-error group-hover:opacity-100 opacity-0 transition-opacity duration-200 ease-in-out absolute bottom-full mb-2 px-3 py-2 text-xs text-gray-600 bg-white border border-gray-200 rounded-md shadow-lg w-max"
                            >
                              <span class="text-red-400"
                                >You need to be board owner or have write access
                                to perform this action</span
                              >
                              <div class="tooltip-arrow bg-white"></div>
                            </div>
                            <button
                              :disabled="disabledButtonWhileOpenPublic"
                              :class="[
                                'itbkk-button-delete ml-2',
                                'btn',
                                'rounded-full',
                                {
                                  'btn-disabled': disabledButtonWhileOpenPublic
                                }
                              ]"
                              :style="{
                                backgroundColor: disabledButtonWhileOpenPublic
                                  ? '#d3d3d3'
                                  : '#f87171',
                                color: disabledButtonWhileOpenPublic
                                  ? '#a9a9a9'
                                  : 'white',
                                borderRadius: '30px',
                                position: 'static',
                                cursor: disabledButtonWhileOpenPublic
                                  ? 'not-allowed'
                                  : 'pointer',
                                opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
                              }"
                              @click="openModalToDelete(item.id, index)"
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
                          </div>
                        </div>

                        <dialog id="my_modal_delete" class="modal">
                          <div class="modal-box" style="max-width: 500px">
                            <h3 class="font-bold text-lg">Delete a Task</h3>
                            <p
                              class="itbkk-message py-4 font-medium"
                              style="word-wrap: break-word"
                            >
                              Do you want to delete the task number
                              {{ selectedItemIdToDelete }} - "{{
                                filterAndLogTitleById(selectedItemIdToDelete)
                              }}"?
                            </p>
                            <form method="dialog">
                              <div class="modal-action">
                                <button
                                  class="itbkk-button-cancel btn"
                                  style="color: #eb4343"
                                >
                                  Cancel
                                </button>
                                <button
                                  class="itbkk-button-confirm btn bg-green-400"
                                  style="color: #fff"
                                  @click="confirmDelete()"
                                >
                                  Confirm
                                </button>
                              </div>
                            </form>
                          </div>
                        </dialog>

                        <!-- DELETE -->
                      </td>
                    </div>
                  </tr>
                  <tr
                    class="bg-base-100 mt-4 md:mt-0"
                    v-if="filteredTasks?.length === 0"
                  >
                    <td colspan="5" class="text-center py-4 text-gray-400">
                      No task
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- DELETE COMPLETE -->
            <div
              role="alert"
              class="alert shadow-lg"
              v-show="deleteComplete"
              style="
                position: fixed;
                top: 20px;
                left: 50%;
                transform: translateX(-50%);
                z-index: 9999;
                width: 500px;
                color: rgb(74 222 128 / var(--tw-text-opacity));
                animation: fadeInOut 1.5s infinite;
              "
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="stroke-current shrink-0 h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <div>
                <h2 class="itbkk-message font-bold text-green-400">
                  The task has been deleted
                </h2>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- <div class="flex justify-center space-x-20 mt-3 mb-3">
  <div class="mt-2 bg-teal-500 text-sm text-white rounded-lg p-4" role="alert" tabindex="-1" aria-labelledby="hs-solid-color-success-label">
    <span id="hs-solid-color-success-label" class="font-bold">Success</span> 
    {{ updateLimitText() }}
  </div>
</div> -->

  <!-- <div class="flex justify-center space-x-20 mt-3 mb-3">
    <button class="flex items-center bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-6 rounded-full shadow-lg transform hover:scale-105 transition-transform duration-300 ease-in-out">
      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 1024 1024">
        <path fill="#eb4343"
          d="M512 244c176.18 0 319 142.82 319 319v233a32 32 0 0 1-32 32H225a32 32 0 0 1-32-32V563c0-176.18 142.82-319 319-319M484 68h56a8 8 0 0 1 8 8v96a8 8 0 0 1-8 8h-56a8 8 0 0 1-8-8V76a8 8 0 0 1 8-8M177.25 191.66a8 8 0 0 1 11.32 0l67.88 67.88a8 8 0 0 1 0 11.31l-39.6 39.6a8 8 0 0 1-11.31 0l-67.88-67.88a8 8 0 0 1 0-11.31l39.6-39.6zm669.6 0l39.6 39.6a8 8 0 0 1 0 11.3l-67.88 67.9a8 8 0 0 1-11.32 0l-39.6-39.6a8 8 0 0 1 0-11.32l67.89-67.88a8 8 0 0 1 11.31 0M192 892h640a32 32 0 0 1 32 32v24a8 8 0 0 1-8 8H168a8 8 0 0 1-8-8v-24a32 32 0 0 1 32-32m148-317v253h64V575z" />
      </svg>
      {{ updateLimitText() }}
    </button>
  </div> -->
  <!-- <footer class="footer footer-center p-5 text-primary-content">
    <aside class="flex items-center space-x-20">
      <p style="color: #f785b1">Thank you for choosing Kradan Kanban!</p>
    </aside>
  </footer> -->
</template>

<style scoped>
@media screen and (max-width: 640px) {
  thead {
    display: none;
  }

  tbody tr {
    display: block;
    margin-bottom: 1rem;
    border: 1px solid #ababab;
    border-radius: 10px;
  }

  td {
    display: block;
    text-align: left;
    border: none;
  }

  td:before {
    display: none;
  }

  .itbkk-button-action td:before {
    display: none;
  }
}

table {
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 10px;
  overflow: hidden;
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

.input-centered {
  text-align: center;
}

.toggle:checked {
  --tw-text-opacity: 1;
  color: rgb(74 222 128 / var(--tw-text-opacity));
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-box {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
  text-align: center;
}
</style>
