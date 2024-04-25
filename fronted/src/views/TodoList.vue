<script setup>
import { ref, onMounted } from "vue"
import { getItems } from "../libs/fetchUtils.js"
import TaskDetail from '../views/TaskDetail.vue'

const todoList = ref([])

onMounted(async () => {
  console.log(import.meta.env.VITE_BASE_URL)
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  console.log(items)
  todoList.value = items
  console.log(todoList.value)
  console.log(items)
})
</script>

<template>
<div class="min-h-full">
  <nav class="bg-gray-800" style="background-color: #F785B1;">
    <div class="mx-auto max-w-7xl px-1">
      <div class="flex h-16 items-center justify-between">
        <div class="flex items-center">
          <div class="hidden md:block">
            <div class="ml-2 flex items-baseline space-x-4">
              <a href="#" class="bg-gray-900 text-white rounded-md px-3 py-2 text-sm font-medium" aria-current="page">My Task</a>
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
      <h1 class="text-3xl font-bold tracking-tight text-gray-900" style="color:#9391E4;">IT-Bangmod Kradan Kanban</h1>
    </div>
  </header>
<!-- header -->
<div class="flex flex-col items-center mt-1">
  <div class="overflow-x-auto" >
    <table class="table w-full bg-white rounded-xl overflow-hidden mt-10">
      <!-- head -->
      <thead>
        <tr class="bg-base-200">
          <th></th>
          <th>Title</th>
          <th>Assignees</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <!-- Iterate over todoList -->
        <tr v-for="(item, index) in todoList" :key="index" class="">
          <th >{{ index + 1 }}</th> 
          <!-- <TaskDetail /> -->
          <td >{{ item.title }}</td>
          <td>{{ item.assignees }}</td>
          <td>{{ item.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
 </div>
<style scoped></style>
