<script setup>
import { ref, onMounted } from "vue"
import { getItems, getItemById } from "../libs/fetchUtils.js"
import TaskDetail from "../views/TaskDetail.vue"

const todoList = ref([])
const selectedTodoId = ref(0)

onMounted(async () => {
  console.log(import.meta.env.VITE_BASE_URL)
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  console.log(items)
  todoList.value = items
  console.log(todoList.value)
  console.log(items)
})

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId
}

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
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
                  aria-current="page"
                  >My Task</a
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </div>
  <!-- header -->
  <header class="bg-white shadow">
    <div class="mx-auto max-w-7xl px-4 py-6">
      <h1
        class="text-3xl font-bold tracking-tight text-gray-900"
        style="color: #9391e4"
      >
        IT-Bangmod Kradan Kanban TimeZone : {{ TimeZone }}
      </h1>
    </div>
  </header>
  <!-- header -->
  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <table class="table mt-10">
        <!-- head -->
        <thead>
          <tr class="bg-base-200">
            <th></th>
            <th>Title</th>
            <th>Assignees</th>
            <th>Status</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <!-- Iterate over todoList -->
          <TaskDetail :todo-id="selectedTodoId" />
          <tr v-for="(item, index) in todoList" :key="index">
            <th>{{ item.id }}</th>
            <label
              for="my_modal_6"
              class="btn"
              @click="() => selectTodo(item.id)"
            >
              <td>{{ item.title }}</td>
            </label>
            <td>
              <span
                :class="{
                  'italic text-gray-500':
                    item.assignees.length === 0 || item.assignees === null
                }"
              >
                {{
                  item.assignees.length === 0 || item.assignees === null
                    ? "Unassignees"
                    : item.assignees
                }}
              </span>
            </td>

            <td
              :class="{
                'bg-red-400': item.status === 'NO_STATUS',
                'bg-orange-400': item.status === 'TO_DO',
                'bg-yellow-400': item.status === 'DOING',
                'bg-green-400': item.status === 'DONE'
              }"
            >
              {{ item.status }}
            </td>
          </tr>
          <tr>
            {{}}
          </tr>
        </tbody>
        <tbody v-if="todoList.length === 0">
          <tr>
            <td
              colspan="4"
              width="300px"
              class="text-center py-4 text-gray-500"
            >
              No task
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<style scoped></style>
