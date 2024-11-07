<script setup>
const props = defineProps({
  isShow: Boolean
})
</script>

<template>
  <div class="p-3">
    <div>
      <slot name="toggle"></slot>
      <slot name="name"></slot>
      <slot name="ownerName"></slot>
      <div class="px-6 py-2">
        <slot name="colorBoard"></slot>
      </div>
    </div>
    <div class="flex px-4 py-2 justify-between">
      <div class="flex items-center space-x-2">
        <div class="flex gap-2">
          <slot name="Btn"></slot>
          <slot name="deleteBtn"></slot>
          <slot name="viewBtn"></slot>
          <slot name="pendingBtn"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
