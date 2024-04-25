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
<!-- nev bar -->
<nav class="bg-gray-800" style="background-color: #F785B1;">
  <div class="mx-auto max-w-7xl px-2 sm:px-6 lg:px-8">
    <div class="relative flex h-16 items-center justify-between">
      <div class="absolute inset-y-0 left-0 flex items-center sm:hidden"></div>
    </div>
  </div>
</nav>

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
      <tbody >
        <!-- row 1 -->
        <tr class="">
          <th>1</th>
          <!-- The button to open modal -->
          <label for="my_modal_6" class="btn">
            <td>Design Backend API for PBI 1,2</td>
          </label>
          <input type="checkbox" id="my_modal_6" class="modal-toggle" />
          <div class="modal" role="dialog">
            <div class="modal-box">
              <h3 class="font-bold text-lg">Design Backend API for PBI 1,2</h3>
              <p class="py-4">Design Backend API for PBI 1,2</p>
              <textarea class="textarea textarea-bordered textarea-lg w-full max-w-xs" placeholder="Discripstion"></textarea>
              <div class="modal-action">
                <label for="my_modal_6" class="btn">Close</label>
              </div>
            </div>
          </div>
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
</div>

</template>

<style scoped></style>
