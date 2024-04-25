<script setup>
import { ref, onMounted } from 'vue'
import { getItems } from '../libs/fetchUtils.js'

const todoList = ref([])

onMounted(async () => {
  console.log(import.meta.env.VITE_BASE_URL)
    const items = await getItems(import.meta.env.VITE_BASE_URL)
    console.log(items)
    todoList.value = items
    console.log(todoList.value)
})
</script>

<template>
 <!-- nev bar -->
<nav class="bg-gray-800" style="background-color: #F785B1;">
  <div class="mx-auto max-w-7xl px-2 sm:px-6 lg:px-8">
    <div class="relative flex h-16 items-center justify-between">
      <div class="absolute inset-y-0 left-0 flex items-center sm:hidden"></div>
    </div>
  </div>
</nav>
 <h3 class="font-bold text-lg">IT-Bangmod Kradan Kanban</h3>
<div class="flex flex-col items-center mt-20">
  <h1>IT-Bangmod Kradan Kanban</h1>
  <div class="overflow-x-auto w-6/12 " >
    <table class="table mt-10">
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
          <th>{{ index + 1 }}</th> 
          <!-- <TaskDetail /> -->
          <td>{{ item.title }}</td>
          <td>{{ item.assignees }}</td>
          <td>{{ item.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
  </div>
</template>

<style scoped></style>
