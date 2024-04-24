<script setup>
import { ref, onMounted } from 'vue';

const TodoList = ref([]);

const fetchUsers = async () => {
  try {
    const response = await fetch('/api/...');
    const data = await response.json();
    TodoList.value = data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
};

onMounted(fetchUsers);
</script>

<template>
<div class="overflow-x-auto">
  <table class="table">
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
      <!-- row 1 -->
      <tr class="">
        <th>1</th>
        <td>Design Backend API for PBI 1,2</td>
        <td>Pang;Pong;</td>
        <td>No Status</td>
      </tr>
      <!-- row 2 -->
      <tr class="">
        <th>2</th>
        <td>Design UI for PBI 1,2</td>
        <td>Ping</td>
        <td>To do</td>
      </tr>

    </tbody>
  </table>
</div>
</template>

<style scoped>

</style>
