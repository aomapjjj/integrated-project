<script setup>
import { ref, onMounted , computed } from "vue"
import { getItemById, getItems, deleteItemById } from "../libs/fetchUtils.js"
import TaskDetail from "../views/TaskDetail.vue"
import AddTask from "../views/AddTask.vue"
import EditTask from "../views/EditTask.vue"
import { useTasks } from "../stores/store"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()
const todoList = ref([])
const selectedTodoId = ref(0)
const notFound = ref(false)
const deleteComplete = ref(false)
const showDetail = ref(false)
const statusList = ref([])
let items = [] 
let itemsStatus = [] 
const indexDelete = ref(0)
const isLimitEnabled = ref(false)
const maxTasks = ref(10) 

const baseUrlTask = `${import.meta.env.VITE_BASE_URL_MAIN}/tasks`
const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`

const taskStore = useTasks()
onMounted(async () => {
  if (taskStore.getTasks().length === 0) {
    items = await getItems(baseUrlTask)
    taskStore.addTasks(await items)
  }

  

  itemsStatus = await getItems(baseUrlStatus)
  statusList.value = itemsStatus
  console.log("itemStatuss", itemsStatus)
  todoList.value = items
  console.log("items", items)
  const taskId = route.params.id
  if (taskId !== undefined) {
    console.log(taskId)
    const response = await getItemById(taskId)
    if (response.status === 404 || response.status === 400) {
      router.push("/task/error")
      notFound.value = true
    }
  }
  return items
})

const selectTodo = (todoId) => {
  if (todoId !== 0) {
  router.push({ name: "TaskDetail", params: { id: todoId } });
}
  selectedTodoId.value = todoId
  showDetail.value = true
}

// ----------------------- Delete -----------------------

const selectedItemIdToDelete = ref(0)

const deleteTodo = async (todoId) => {
  try {
    const status = await deleteItemById(baseUrlTask, todoId)
    if (status === 200) {
      todoList.value = todoList.value.filter((todo) => todo.id !== todoId)
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
  const modal = document.getElementById("my_modal_delete")
  modal.showModal()
}

const closeModal = () => {
  const modal = document.getElementById("my_modal_delete")
  modal.close()
}

const confirmDelete = () => {
  taskStore.removeTask(selectTodo.id)
  deleteTodo(selectedItemIdToDelete.value)
  closeModal()
  deleteComplete.value = true
  setTimeout(() => {
    deleteComplete.value = false
  }, 2300)
}

// ----------------------- Delete -----------------------

// ----------------------- filterAndLogTitleById -----------------------

const filterAndLogTitleById = (id) => {
  const item = items.find((item) => item.id === id)
  if (item) {
    console.log(item.title)
    return item.title
  } else {
    console.log(`No item found with id ${id}`)
    return ""
  }
}

// ----------------------- filterAndLogTitleById -----------------------

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

// ----------------------- STATUS SORT -----------------------
const showIcon = ref("default")
const statusSortOrder = ref("asc")

const toggleIcon = () => {
  if (showIcon.value === "default") {
    showIcon.value = "asc"
    statusSortOrder.value = "asc"
  } else if (showIcon.value === "asc") {
    showIcon.value = "desc"
    statusSortOrder.value = "desc"
  } else {
    showIcon.value = "default"
    statusSortOrder.value = "default"
  }

  sortByStatus()
}

const sortByStatus = () => {
  const currentSortOrder = statusSortOrder.value
  if (currentSortOrder === "asc") {
    taskStore.getTasks().sort((a, b) => a.status.localeCompare(b.status))
  } else if (currentSortOrder === "desc") {
    taskStore.getTasks().sort((a, b) => b.status.localeCompare(a.status))
  } else {
    taskStore.getTasks().sort((a, b) => a.id - b.id)
  }
}


// ----------------------- Filter -----------------------

const searchQuery = ref([])

const filteredTasks = computed(() => {
  if (searchQuery.value.length === 0) {
    return taskStore.getTasks()
    
  }
  return taskStore.getTasks().filter(task => searchQuery.value.includes(task.status))
})

const removeStatus = (status) => {
  const index = searchQuery.value.indexOf(status)
  if (index > -1) {
    searchQuery.value.splice(index, 1)
  }
}

// ----------------------- Filter -----------------------

const openNewStatus = () => {
  router.push({ name: "StatusesList" })
}


</script>

<template>
  <div class="min-h-full max-h-fit">
    <nav class="bg-white shadow" style=" background-color: #d8f1f1 ;">
      <div class="mx-auto max-w-7xl px-2 flex items-center justify-between">
        <a href="#" class="flex items-center gap-4">
          <img
            src="/src/image/sj3.png"
            alt="LOGO"
            class="w-[100px] h-[100px]"
          />
          <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10">
            <h2 class="text-sm tracking-tight text-gray-800">Welcome,</h2>
            <h1
              class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight"
              style="color: #9391e4; text-align: center; text-shadow: 0 0 5px #ffffff, 0 0 5px #ffffff, 0 0 5px #ffffff;"
            >
              IT-Bangmod Kradan Kanban
            </h1>
          </div>
        </a>
        <div class="flex items-center">
          <div class="hidden md:block">
            <div class="flex space-x-1.5">
              <!-- ADD BUTTON -->
              <AddTask />
              <!-- MANAGE STATUS -->
              <button
                class="itbkk-manage-status btn bg-gray-200"
                style="
                  color: white;
                  background-color: #f785b1;
                  border-radius: 30px;
                "
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
    </nav>
  </div>

  <div class="flex justify-start mt-9 mx-auto ml-[100px]">
    <!-- LIMIT -->
    <button
      class="btn btn-circle btn-outline mr-2"
      onclick="my_modal_limit.showModal()"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-4 w-4"
        fill="none"
        viewBox="0 0 14 14"
      >
        <path
          fill="#9FC3E9"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M13.43 3.59a.76.76 0 0 0-.35-.51l-2 2a1 1 0 0 1-1.44 0l-.76-.68a1 1 0 0 1 0-1.4l2-2a.76.76 0 0 0-.48-.43A3.8 3.8 0 0 0 6.26 6L.8 11.41a1 1 0 0 0 0 1.43l.36.36a1 1 0 0 0 1.43 0l5.46-5.45a3.81 3.81 0 0 0 5.38-4.16Z"
        />
      </svg>
    </button>
    <dialog id="my_modal_limit" class="modal modal-bottom sm:modal-middle">
      <div class="modal-box">
        <h3 class="font-bold text-lg">Status Settings</h3>
        <p class="py-4">
          User can limit the number of task in status by setting the Maximum
          task in each status
        </p>
        <span style="color: red"
          >( except "No Status" and "Done" statuses )</span
        >

        <div class="flex items-center mt-4">
          <span class="mr-2">Enable Status Limit</span>
          <input type="checkbox" class="toggle" v-model="isLimitEnabled"  />
        </div>

        <div v-if="isLimitEnabled" class="mt-4">
        <label for="status-limit" class="mr-2">Set Maximum Tasks:</label>
        <input type="number" id="status-limit" class="input input-bordered" v-model.number="maxTasks" />
      </div>

        <div class="modal-action">
          <form method="dialog">
            <button class="btn mr-2 bg-green-400">Confirm</button>
            <button class="btn">Close</button>
          </form>
        </div>
      </div>
    </dialog>
    <!-- FILTER -->
     
    <details class="dropdown">
    <summary class="m-1 btn">
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
      filter
    </summary>
    <ul class="p-2 shadow menu dropdown-content z-[1] bg-base-100 rounded-box w-52">
      <li v-for="status in statusList" :key="status.name" class="flex items-center">
        <label class="flex items-center space-x-2 w-full">
          <input type="checkbox" :value="status.name" v-model="searchQuery" class="mr-2" />
          <span>{{ status.name }}</span>
        </label>
      </li>
    </ul>
  </details>

  <div class="selected-filters flex flex-wrap mt-2">
    <div v-for="status in searchQuery" :key="status" class="selected-filter bg-blue-200 text-blue-800 rounded-full px-4 py-2 mr-2 mt-2 flex items-center">
      <span>{{ status }}</span>
      <button @click="removeStatus(status)" class="ml-2 text-blue-600 hover:text-blue-800">
        &times;
      </button>
    </div>
  </div>
  </div>

  <div class="flex flex-col items-center mt-9">
    <div class="overflow-x-auto max-h-96 w-min-full">
      <div class="min-w-full">
        <table class="table-auto" style="table-layout: fixed">
          <!-- table -->
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
            <TaskDetail :todo-id="selectedTodoId" />
            <tr class="itbkk-item" v-for="(item, index) in filteredTasks" :key="index">
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ index + 1 }}
              </td>
              <td
                class="itbkk-title px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                <label
                
                  for="my_modal_6"
                  @click="selectTodo(item.id)"
                  style="display: block; width: 100%; height: 100%"
                >
                  {{ item.title }}
                </label>
              </td>
              <td
                class="itbkk-assignees px-4 py-2 text-center md:text-left text-sm text-gray-700"
                :class="{
                  italic: !item.assignees || item.assignees.length === 0
                }"
              >
                {{
                  !item.assignees || item.assignees.length === 0
                    ? "Unassigned"
                    : item.assignees
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
                    'border-red-500 text-red-500': item.status === 'To Do',
                    'border-yellow-500 text-yellow-500':
                      item.status === 'Doing',
                    'border-green-500 text-green-500': item.status === 'Done'
                  }"
                >
                  {{ item.status }}
                </span>
              </td>

              <div class="itbkk-button-action">
                <td style="display: flex; justify-content: center">
                  <div
                    class="itbkk-button-edit hidden md:table-cell text-sm px-4 py-2"
                  >
                    <div
                      v-if="index !== 0 && index !== 1"
                      class="dropdown dropdown-top dropdown-end"
                    >
                      <div tabindex="0">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="28"
                          height="28"
                          viewBox="0 0 24 24"
                        >
                          <path
                            fill="none"
                            stroke="currentColor"
                            stroke-linecap="round"
                            stroke-width="3"
                            d="M12 6h.01M12 12h.01M12 18h.01"
                          />
                        </svg>
                      </div>
                      <ul
                        tabindex="0"
                        class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52"
                      >
                        <!-- EDIT -->
                        <li>
                          <a><EditTask :todo-id="item.id" /></a>
                        </li>

                        <!-- Delete -->
                        <li>
                          <a
                            style="width: 150px; margin-left: 17px"
                            class="itbkk-button-delete btn"
                            @click="openModalToDelete(item.id, index)"
                            >Delete</a
                          >
                        </li>
                      </ul>
                    </div>
                  </div>

                  <!-- Dropdown  -->
                  <div
                    v-if="index === 0 || index === 1"
                    class="itbkk-button-edit hidden md:table-cell text-sm px-4 py-2 mr-8"
                  >
                    <div class="dropdown dropdown-end">
                      <div tabindex="0">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="28"
                          height="28"
                          viewBox="0 0 24 24"
                        >
                          <path
                            fill="none"
                            stroke="currentColor"
                            stroke-linecap="round"
                            stroke-width="3"
                            d="M12 6h.01M12 12h.01M12 18h.01"
                          />
                        </svg>
                      </div>
                      <ul
                        tabindex="0"
                        class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52"
                      >
                        <!-- EDIT -->
                        <li>
                          <a><EditTask :todo-id="item.id" /></a>
                        </li>

                        <!-- Delete -->
                        <li>
                          <a
                            style="width: 150px; margin-left: 17px"
                            class="itbkk-button-delete btn"
                            @click="openModalToDelete(item.id)"
                            >Delete</a
                          >
                        </li>
                      </ul>
                    </div>
                  </div>

                  <dialog id="my_modal_delete" class="modal">
                    <div class="modal-box" style="max-width: 1000px">
                      <h3 class="font-bold text-lg">Delete a Task</h3>
                      <p
                        class="itbkk-message py-4 font-medium"
                        style="word-wrap: break-word"
                      >
                        Do you want to delete the task number
                        {{ indexDelete + 1 }} - "{{
                          filterAndLogTitleById(selectedItemIdToDelete)
                        }}"?
                      </p>
                      <div class="modal-action">
                        <button
                          class="itbkk-button-cancel btn"
                          @click="closeModal"
                          style="color: #eb4343"
                        >
                          Cancel
                        </button>
                        <button
                          class="itbkk-button-confirm btn bg-green-400"
                          style="color: #fff"
                          @click="confirmDelete"
                        >
                          Confirm
                        </button>
                      </div>
                    </div>
                  </dialog>

                  <!-- DELETE -->
                </td>
              </div>
              
            </tr>
            <tr class="bg-base-100 mt-4 md:mt-0" v-if="filteredTasks?.length === 0" >
              <td colspan="5" class="text-center py-4 text-gray-400">
                No task
              </td>
              </tr>
           


            <!-- DELETE COMPLETE -->
          </tbody>
        </table>
      </div>
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
          <h2 class="itbkk-message font-bold text-green-400">The task has been deleted</h2>
        </div>
        <div>
  
        </div>
      </div>
    </div>
  </div>

  <footer class="footer footer-center p-5 text-primary-content">
    <aside class="flex items-center space-x-20">
      <!-- <img
        src="/src/image/sit.png"
        alt="SIT logo"
        style="width: 180px; margin-right: 100px"
      /> -->
      <!-- <p class="font-bold mb-2 text-gray-900">
        IT-Bangmod Kradan Kanban was created by 2nd year undergraduate students
        <br />( INT221 Integrated Information Technology Project I )
      </p> -->
      <p style="color: #f785b1">Thank you for choosing Kradan Kanban!</p>
    </aside>
  </footer>
</template>

<style scoped>
/* Responsive table styles */
@media screen and (max-width: 640px) {
  /* Hide the table header */
  thead {
    display: none;
  }

  /* Make table rows display as blocks for better stacking on small screens */
  tbody tr {
    display: block;
    margin-bottom: 1rem;
    border: 1px solid #ababab; /* Add border to separate rows */
    border-radius: 10px; /* Add border radius for rounded corners */
  }

  /* Make table cells display as block elements */
  td {
    display: block;
    text-align: left;
    border: none; /* Remove border to separate cells */
  }

  /* Hide the table cell labels */
  td:before {
    display: none;
  }

  /* Hide the "Action" column labels */
  .itbkk-button-action td:before {
    display: none;
  }
}

#tasktable {
  width: 100%;
  overflow: scroll;
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
.selected-filters {
  display: flex;
  flex-wrap: wrap;
}

.selected-filter {
  display: flex;
  align-items: center;
  background-color: #cce5ff;
  color: #004085;
  border-radius: 9999px;
  padding: 0.5rem 1rem;
  margin-right: 0.5rem;
  margin-top: 0.5rem;
}

.selected-filter button {
  margin-left: 0.5rem;
  background: none;
  border: none;
  color: #004085;
  cursor: pointer;
}

.selected-filter button:hover {
  color: #002752;
}
</style>
