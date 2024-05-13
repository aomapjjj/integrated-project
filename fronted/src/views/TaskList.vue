<script setup>
import { ref, onMounted } from "vue"
import { getItemById, getItems, deleteItemById } from "../libs/fetchUtils.js"
import TaskDetail from "../views/TaskDetail.vue"
import AddTask from "../views/AddTask.vue"
import EditTask from "../views/EditTask.vue"
import StatusesList from "../views/StatusesList.vue"
import { checkStatus } from "../libs/checkStatus"
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
const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: ""
})

onMounted(async () => {
  items = await getItems(import.meta.env.VITE_BASE_URL)
  itemsStatus = await getItems(import.meta.env.VITE_BASE_URL_STATUS)
  statusList.value = itemsStatus
  
  console.log('itemStatuss', itemsStatus)
  todoList.value = items
  console.log('items', items)
  const taskId = route.params.id

  if (taskId !== undefined) {
    console.log(taskId);
    const response = await getItemById(taskId);
    if (response.status === 404 || response.status === 400) {
      router.push('/task/error');
      notFound.value = true;
    }
  }
  return items
})


const selectTodo = (todoId) => {
  selectedTodoId.value = todoId;
  showDetail.value = true;
};



// ----------------------- Delete -----------------------

const selectedItemIdToDelete = ref(0);

const deleteTodo = async (todoId) => {
  try {
    const status = await deleteItemById(import.meta.env.VITE_BASE_URL, todoId);
    if (status === 200) {
      todoList.value = todoList.value.filter((todo) => todo.id !== todoId);
    } else {
      console.error(`Failed to delete item with ID ${todoId}`);
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${todoId}:`, error);
  }
};

const openModalToDelete = (itemId) => {
  selectedItemIdToDelete.value = itemId;
  const modal = document.getElementById('my_modal_delete');
  modal.showModal();
};

const closeModal = () => {
  const modal = document.getElementById('my_modal_delete');
  modal.close();
};

const confirmDelete = () => {
  deleteTodo(selectedItemIdToDelete.value);
  closeModal();
  deleteComplete.value = true;
  setTimeout(() => {
    deleteComplete.value = false;
  }, 2300);
};

// ----------------------- Delete -----------------------

// ----------------------- filterAndLogTitleById -----------------------

const filterAndLogTitleById = (id) => {
  const item = items.find((item) => item.id === id);
  if (item) {
    console.log(item.title);
    return item.title;
  } else {
    console.log(`No item found with id ${id}`);
    return '';
  }
};

// ----------------------- filterAndLogTitleById -----------------------

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

const openNewStatus = () => {
  router.push({ name: 'StatusesList' });
};
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

  <div class="flex justify-end mt-9 mr-9">
    <!-- FILTERS -->
    <button class="btn bg-gray-900" style="color: white; margin-right: 10px">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="18"
        height="18"
        viewBox="0 0 24 24"
      >
        <path
          fill="none"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-width="2"
          d="M20 6H10m0 0a2 2 0 1 0-4 0m4 0a2 2 0 1 1-4 0m0 0H4m16 6h-2m0 0a2 2 0 1 0-4 0m4 0a2 2 0 1 1-4 0m0 0H4m16 6H10m0 0a2 2 0 1 0-4 0m4 0a2 2 0 1 1-4 0m0 0H4"
        />
      </svg>
      Filter
    </button>
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

  <div class="flex flex-col items-center mt-5">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <!-- ADD BUTTON -->
        <AddTask :todo="todo" />

        <table
          class="table-auto mt-10 rounded-xl overflow-hidden"
          style="table-layout: fixed"
        >
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
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                Status
              </th>
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                Action
              </th>
            </tr>
          </thead>
          <tbody>
            <!-- Iterate over todoList -->
            <TaskDetail :todo-id="selectedTodoId" v-if="showDetail" />
            <tr
              class="itbkk-item"
              v-for="(item, index) in todoList"
              :key="index"
            >
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ item.id }}
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
                  italic: !item.assignees || item.assignees.length === 0,
                }"
              >
                {{
                  !item.assignees || item.assignees.length === 0
                    ? 'Unassigned'
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
                      item.status === 'NO_STATUS',
                    'border-red-500 text-red-500': item.status === 'TO_DO',
                    'border-yellow-500 text-yellow-500':
                      item.status === 'DOING',
                    'border-green-500 text-green-500': item.status === 'DONE',
                  }"
                >
                  {{ checkStatus(item.status) }}
                </span>
              </td>

              <div class="itbkk-button-action">
                <td style="display: flex; justify-content: center;">
                <div class="itbkk-button-edit hidden md:table-cell text-sm px-4 py-2">
                  <div class="dropdown dropdown-top dropdown-end">
                    <div tabindex="0">
                      <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24">
                        <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="3" d="M12 6h.01M12 12h.01M12 18h.01"/>
                      </svg>
                    </div>
                    <ul tabindex="0" class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                    <!-- EDIT -->
                      <li><a><EditTask :todo-id="item.id" /></a></li>
                    <!-- Delete -->
                      <li><a style="width: 150px; margin-left: 17px;" class="itbkk-button-delete btn" @click="openModalToDelete(item.id)">Delete</a></li>
                    </ul>
                  </div>

                  <dialog id="my_modal_delete" class="modal">
                    <div class="modal-box" style="max-width: 1000px">
                      <h3 class="itbkk-message font-bold text-lg">
                        Delete a Task
                      </h3>
                      <p class="py-4 font-medium" style="word-wrap: break-word">
                        Do you want to delete the task number
                        {{ selectedItemIdToDelete }} - "{{
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
                </div>
                <!-- DELETE -->
              </td>
              </div>
              
            </tr>

            <!-- DELETE COMPLETE -->
            <tr v-if="todoList.length === 0">
              <td colspan="4" class="text-center py-4 text-gray-500">
                No task
              </td>
            </tr>
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
          <h2 class="font-bold">
            Delete Complete "{{
              filterAndLogTitleById(selectedItemIdToDelete)
            }}"
          </h2>
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
</template>
<style scoped>
table {
  border-collapse: collapse;
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}

/* Set equal height for cells in thead */
thead th {
  height: 3rem; /* Adjust the height as needed */
}
</style>
