<script setup>

import { ref, onMounted } from 'vue';
import { getItemById, getItems, deleteItemById } from '../libs/fetchUtils.js';
import TaskDetail from '../views/TaskDetail.vue';
import AddTask from '../views/AddTask.vue';
import EditTask from '../views/EditTask.vue';
import { checkStatus } from '../libs/checkStatus';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const todoList = ref([]);
const selectedTodoId = ref(0);
const notFound = ref(false);
let items = []; // ประกาศ items เป็นตัวแปร global


onMounted(async () => {
  items = await getItems(import.meta.env.VITE_BASE_URL);
  todoList.value = items;
  const taskId = route.params.id;
  if (taskId !== undefined) {
    console.log(taskId);
    const response = await getItemById(taskId);
    if (response.status === 404 || response.status === 400) {
      router.push('/task/error');
      notFound.value = true;
    }
  }
});

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId;
};

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
const filterAndLogTitleById = (id) => {

  const item = items.find((item) => item.id === id);

  if (item) {
    console.log(item.title);
    return item.title;
  } else {
    console.log(`No item found with id ${id}`);

    return ''; // หรือให้คืนค่า null หรือ undefined ตามที่คุณต้องการ
  }
};


const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

const selectedItemIdToDelete = ref(0);

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
  deleteTodo(selectedItemIdToDelete.value)
  closeModal()

  alert(`จะลบแล้วนร้า ${filterAndLogTitleById(selectedItemIdToDelete.value)}`);
  console.log(filterAndLogTitleById(selectedItemIdToDelete.value)); // เข้าถึง items จากตรงนี้ได้
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
        style="color: #9391e4"
      >
        IT-Bangmod Kradan Kanban
      </h1>
    </div>
  </header>
  <!-- header -->

  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <!-- ADD BUTTON -->
        <AddTask />
        <table
          class="table-auto mt-10 rounded-xl overflow-hidden"
          style="table-layout: fixed"
        >
          <!-- table -->
          <thead>
            <tr class="bg-base-200 mt-4 md:mt-0">
              <th
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
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
              ></th>
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              ></th>
            </tr>
          </thead>
          <tbody>
            <!-- Iterate over todoList -->
            <TaskDetail :todo-id="selectedTodoId" />
            <tr
              class="itbkk-item"
              v-for="(item, index) in todoList"
              :key="index"
            >
              <td
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ item.id }}
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-title"
              >
                <label for="my_modal_6" @click="() => selectTodo(item.id)">
                  {{ item.title }}
                </label>
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-assignees"
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
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status"
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

              <!-- EDIT -->

              <td class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700">

                <button class="itbkk-button-edit btn btn-info">Edit</button>

              </td>


              <td class="hidden md:table-cell text-sm pl-4">
                <EditTask :todo-id="selectedTodoId"/>
            
              </td>

              <!-- DELETE -->
              <td
                class="hidden md:table-cell px-4 py-3 text-center md:text-left text-sm text-gray-700"
              >
                <button
                  class="itbkk-button-delete btn btn-circle btn-outline btn-sm"
                  @click="openModalToDelete(item.id)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    style="color: #eb4343"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>

                </button>

                <dialog id="my_modal_delete" class="modal">

                  <div class="modal-box" style="max-width: 1000px;">
                    <h3 class="itbkk-message font-bold text-lg">
                      Delete a Task
                    </h3>
                    <p class="py-4 font-medium" style="word-wrap: break-word">
                      Do you want to delete the task "{{
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
                        style="color: #fff;"
                        @click="confirmDelete"
                      >
                        Confirm
                      </button>
                    </div>
                  </div>
                </dialog>
              </td>
            </tr>

            <!-- NO TASK -->
            <tr v-if="todoList.length === 0">
              <td colspan="4" class="text-center py-4 text-gray-500">
                No task
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div role="alert" class="alert shadow-lg" v-show="deleteComplete" style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            color: red;
            animation: fadeInOut 1.5s infinite;
          ">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-info shrink-0 w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <div>
            <h3 class="font-bold">The requested task does not exist</h3>
            <div class="text-xs">qq</div>
          </div>
          <div>
            <button class="btn btn-sm" style="background-color: #9fc3e9" @click="deleteComplete = false">
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

table td {
  border-top: 1px solid rgba(182, 182, 188, 0.66); /* เส้นด้านล่างของเซลล์ */
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}
</style>
