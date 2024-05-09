<script setup>
import { ref, onMounted } from 'vue';
import { getItemById, getItems } from '../libs/fetchUtils.js';
import TaskDetail from '../views/TaskDetail.vue';
import { checkStatus } from '../libs/checkStatus';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute()
const router = useRouter()
const todoList = ref([]);
const selectedTodoId = ref(0);
const notFound = ref(false)

onMounted(async () => {
  const items = await getItems(import.meta.env.VITE_BASE_URL);
  todoList.value = items;

  const taskId = route.params.id
  if (taskId !== undefined) {
    console.log(taskId)
    const response = await getItemById(taskId)
    if (response.status === 404 || response.status === 400) {
      router.push('/task')
      notFound.value = true
    }
  }
});

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId;
};

// const openModalToDelete = (itemId) => {
//   selectedItemIdToDelete.value = itemId
//   const modal = document.getElementById("my_modal_delete")
//   modal.showModal()
// }

// const closeModal = () => {
//   const modal = document.getElementById("my_modal_delete")
//   modal.close()
// }

const isModalOpen = ref(false);

const openModalAdd = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
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
                <a href="#" class="bg-gray-900 text-white rounded-md px-3 py-2 text-sm font-medium">My Task</a>
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
    <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10 flex justify-between items-center">
      <h1 class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight text-gray-900" style="color: #9391e4">
        IT-Bangmod Kradan Kanban
      </h1>
      <!-- Add new status -->
      <button class="btn" onclick="my_modal_4.showModal()">Add status</button>
      <dialog id="my_modal_4" class="modal">
        <div class="modal-box w-11/12 max-w-5xl">
          <h3 class="font-bold text-lg">Add Status</h3>
          <div class="modal-action">
            <form method="dialog">
              <label for="description" class="form-control flex-grow ml-4 mb-8">
                <div class="label">
                  <span class="block text-lg font-bold leading-6 text-gray-900 mb-1" style="color: #9391e4">Name</span>
                </div>
                <textarea id="description" class="itbkk-description textarea textarea-bordered h-3/4 mb-8"
                  maxlength="500" rows="4" placeholder="No Description Provided"></textarea>
              </label>
              <label for="description" class="form-control flex-grow ml-4 mb-8">
                <div class="label">
                  <span class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                    style="color: #9391e4">Description</span>
                </div>
                <textarea id="description" class="itbkk-description textarea textarea-bordered h-3/4 mb-8"
                  maxlength="500" rows="4" placeholder="No Description Provided"></textarea>
              </label>
              <button class="btn">Save</button>
              <button class="btn">Close</button>
            </form>
          </div>
        </div>
      </dialog>
      <!-- Modal -->
      <div v-if="isModalOpen" id="default-modal" tabindex="-1" aria-hidden="true"
        class="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
        <div class="relative p-4 w-full max-w-2xl max-h-full">
          <!-- Modal content -->
          <div class="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <!-- Modal header -->
            <div class="flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600">
              <h3 class="text-xl font-semibold text-gray-900 dark:text-white">
              </h3>
              <button @click="closeModal" type="button"
                class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
                data-modal-hide="default-modal">
                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                  viewBox="0 0 14 14">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
                </svg>
                <span class="sr-only">Close modal</span>
              </button>
            </div>
            <!-- Modal body -->
            <div class="p-4 md:p-5 space-y-4">
              <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
              </p>
              <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
              </p>
            </div>
            <!-- Modal footer -->
            <div class="flex items-center p-4 md:p-5 border-t border-gray-200 rounded-b dark:border-gray-600">
              <button @click="closeModal" type="button"
                class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">I
                accept</button>
              <button @click="closeModal" type="button"
                class="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">Decline</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <!-- HOME -->
        <div class="text-sm breadcrumbs">
          <ul>
            <li>
              <a @click="$router.go(-1)">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="w-4 h-4 stroke-current">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"></path>
                </svg>
                Home
              </a>
            </li>
            <li>
              <a>
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="w-4 h-4 stroke-current">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"></path>
                </svg>
                Task Status
              </a>
            </li>
          </ul>
        </div>
        <table class="table-auto mt-10 rounded-xl overflow-hidden" style="table-layout: fixed">
          <!-- table -->
          <thead>
            <tr class="bg-base-200 mt-4 md:mt-0" style="background-color: #9fc3e9">
              <th class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                No.
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Name
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Description
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Action
              </th>
            </tr>
          </thead>
          <tbody>
            <!-- Iterate over todoList -->
            <TaskDetail :todo-id="selectedTodoId" />
            <tr class="itbkk-item" v-for="(item, index) in todoList" :key="index">
              <td class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700">
                {{ item.id }}
              </td>
              <td class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-title">
                <label for="my_modal_6" @click="() => selectTodo(item.id)">
                  {{ item.title }}
                </label>
              </td>
              <td class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-assignees"
                :class="{ 'italic': item.assignees.length === 0 || item.assignees === null }">
                {{ item.assignees.length === 0 || item.assignees === null ? 'Unassigned' : item.assignees }}
              </td>
              <td class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status">
                <button class="btn btn-outline">Edit</button>
                <button class="btn btn-outline">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<style scoped></style>