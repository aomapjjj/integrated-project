<script setup>
const props = defineProps({
  isLoading: Boolean
})
</script>

<template>
  <div
    v-if="isLoading"
    class="fixed inset-0 bg-gray-500 bg-opacity-50 flex items-center justify-center z-50"
  >
    <div
      class="itbkk-modal-alert modal-box w-1/3 p-6 text-center flex flex-col items-center justify-center space-y-4"
    >
      <h3 class="font-bold text-lg customPurple">
        Please hold on, we're almost ready...
      </h3>
      <div class="loader"></div>
      <p class="text-gray-700">
        Sending your invitation email now. <br />
        This will only take a moment.
      </p>
      <p class="text-sm text-gray-500">
        please check your email for the board invite!
      </p>
    </div>
  </div>
</template>

<style scoped>
.loader {
  width: 120px;
  height: 20px;
  border-radius: 20px;
  background: repeating-linear-gradient(135deg, #f03355 0 10px, #ffa516 0 20px)
      0/0% no-repeat,
    repeating-linear-gradient(135deg, #ddd 0 10px, #eee 0 20px) 0/100%;
  animation: l3 2s infinite;
}

@keyframes l3 {
  100% {
    background-size: 100%;
  }
}

.space-y-4 > :not([hidden]) ~ :not([hidden]) {
  margin-top: 1rem;
}
</style>
