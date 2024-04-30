<script setup></script>
<template>

    <button onclick="my_modal_1.showModal()" class="bg-white text-green-500 hover:bg-gray-900 rounded-md px-3 py-2 text-sm font-medium">+ Add</button>
        
<dialog id="my_modal_1" class="modal">
  <div class="modal-box">
    <h3 class="font-bold text-lg">Hello!</h3>
    <p class="py-4">สวาดีจัด</p>
    <div class="modal-action">
      <form method="dialog">
        <!-- if there is a button in form, it will close the modal -->
        <button class="btn">Close</button>
      </form>
    </div>
  </div>
</dialog>
</template>
<style></style>