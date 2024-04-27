<script setup>
import { ref, onMounted } from 'vue';
import { getItems } from '../libs/fetchUtils.js';
import TaskDetail from '../views/TaskDetail.vue';
import { checkStatus } from '../libs/checkStatus';

const todoList = ref([]);
const selectedTodoId = ref(0);

onMounted(async () => {
  const items = await getItems(import.meta.env.VITE_BASE_URL);
  todoList.value = items;
});

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId;
};

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
</script>
<template>
  <div class="min-h-full">
    <nav class="bg-gray-800" style="background-color: #f785b1">
      <div class="mx-auto max-w-7xl px-1">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="hidden md:block">
              <div class="ml-2 flex items-baseline space-x-4">
                <a href="#" class="bg-gray-900 text-white rounded-md px-3 py-2 text-sm font-medium"
                  aria-current="page">My Task</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </div>

  <!-- header -->
  <header class="bg-white shadow">
    <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10">
      <h1 class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight text-gray-900" style="color: #9391e4">
        IT-Bangmod Kradan Kanban TimeZone : {{ TimeZone }}
      </h1>
    </div>
  </header>
  <!-- header -->

  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <table class="table-auto mt-10 rounded-xl overflow-hidden">
          <!-- head -->
          <thead>
            <tr class="bg-base-200 mt-4 md:mt-0" style="background-color: #9fc3e9">
              <th class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                No.
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Title
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Assignees
              </th>
              <th class="px-4 py-2 text-center md:text-left text-sm font-semibold text-gray-700">
                Status
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
                {{
          item.assignees.length === 0 || item.assignees === null
            ? 'Unassigned'
            : item.assignees
        }}
              </td>
              <td class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status">
                <span :class="{
            'badge badge-outline border border-solid w-20 text-xs px-2 py-1': true,
            'border-blue-500 text-blue-500':
              item.status === 'NO_STATUS',
            'border-red-500 text-red-500': item.status === 'TO_DO',
            'border-yellow-500 text-yellow-500':
              item.status === 'DOING',
            'border-green-500 text-green-500': item.status === 'DONE',
          }">
                  {{ checkStatus(item.status) }}
                </span>
              </td>
            </tr>
            <tr v-if="todoList.length === 0">
              <td colspan="4" class="text-center py-4 text-gray-500">
                No task
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<style scoped></style>