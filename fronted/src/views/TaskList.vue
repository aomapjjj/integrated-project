<script setup>
import { ref, onMounted } from "vue"
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
let items = [] // ประกาศ items เป็นตัวแปร global
let itemsStatus = [] // ประกาศ items เป็นตัวแปร global
const indexDelete = ref(0)

const baseUrlTask = `${import.meta.env.VITE_BASE_URL_MAIN}/tasks`
const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`

const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: "No Status"
})

const taskStore= useTasks()
onMounted(async () => {


  if (taskStore.getTasks().length === 0) {
    items = await getItems(baseUrlTask)
    taskStore.addTasks(await items)
  }

  console.log(taskStore.getTasks())

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
  router.push({ name: 'TaskDetail', params: { id: todoId } })
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

const toggleIcon = () => {
  if (showIcon.value === "default") {
    showIcon.value = "asc"
  } else if (showIcon.value === "asc") {
    showIcon.value = "desc"
  } else {
    showIcon.value = "asc"
  }
}
const statusSortOrder = ref("asc")

const sortByStatus = () => {
  const currentSortOrder = statusSortOrder.value
  
  if (currentSortOrder === "asc") {
    taskStore.getTasks().sort((a, b) => a.status.localeCompare(b.status))
    statusSortOrder.value = "desc"
    
  } else {
    taskStore.getTasks().sort((a, b) => b.status.localeCompare(a.status))
    statusSortOrder.value = "asc"
  }
}

// ----------------------- STATUS SORT -----------------------

const openNewStatus = () => {
  router.push({ name: "StatusesList" })
}




</script>

<template>
  <div class="min-h-full">
    <nav class="bg-gray-800" style="background-color: #f785b1">
      <div class="mx-auto max-w-7xl px-1">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="hidden md:block">
              <div class="ml-2 flex items-baseline space-x-4">
                <a
                  href="#"
                  class="bg-gray-900 text-white rounded-md px-3 py-2 text-sm font-medium"
                  >My Task</a
                >
              </div>
            </div>
          </div>
          <div class="flex items-center"></div>
        </div>
      </div>
    </nav>
  </div>

  <!-- header -->
  <header class="bg-white shadow">
    <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10">
      <h1
        class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight text-gray-900"
        style="color: #9391e4; text-align: center"
      >
        IT-Bangmod Kradan Kanban
      </h1>
    </div>
  </header>
  <!-- header -->

  <div class="flex justify-between mt-9 mx-20">
   

    <!-- SEARCH INPUT -->
    <div class="mr-auto">
      <label class="input input-bordered flex items-center gap-2">
        <input type="text" class="grow" placeholder="Search" />
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 16 16"
          fill="currentColor"
          class="w-4 h-4 opacity-70"
        >
          <path
            fill-rule="evenodd"
            d="M9.965 11.026a5 5 0 1 1 1.06-1.06l2.755 2.754a.75.75 0 1 1-1.06 1.06l-2.755-2.754ZM10.5 7a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0Z"
            clip-rule="evenodd"
          />
        </svg>
      </label>
    </div>
    <div class="flex space-x-4">
      <!-- ADD BUTTON -->
      <AddTask />
      <!-- MANAGE STATUS -->
      <button
        class="itbkk-manage-status btn bg-gray-200"
        style="color: black"
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
            <tr
              class="itbkk-item"
              v-for="(item, index) in taskStore.getTasks()"
              :key="index"
            >
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ index+1}}
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
          color: red;
          animation: fadeInOut 1.5s infinite;
        "
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          class="stroke-info shrink-0 w-6 h-6"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          ></path>
        </svg>
        <div>
          <h2 class="itbkk-message font-bold">The task has been deleted</h2>
        </div>
        <div>
          <button
            class="btn btn-sm"
            style="background-color: #9fc3e9"
            @click="deleteComplete = false"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>

  <footer
    class="footer footer-center p-5 text-primary-content"
    style="background-color: #f785b1; margin-top: 37px"
  >
    <aside class="flex items-center space-x-20">
      <img
        src="/src/image/sit.png"
        alt="SIT logo"
        style="width: 180px; margin-right: 100px"
      />
      <p class="font-bold mb-2 text-gray-900">
        IT-Bangmod Kradan Kanban was created by 2nd year undergraduate students
        <br />( INT221 Integrated Information Technology Project I )
      </p>
      <p class="text-white">Thank you for choosing Kradan Kanban!</p>
    </aside>
  </footer>
</template>
<style scoped>
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
</style>
