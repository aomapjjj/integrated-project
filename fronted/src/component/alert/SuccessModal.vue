<script setup>
import { ref } from 'vue'

const props = defineProps({
  isFailure: Boolean,
  isSuccess: Boolean
})
</script>

<template>
  <div v-if="isSuccess" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white rounded-lg p-6 w-80 text-center relative">
      <button @click="closeModal" class="absolute top-2 right-2 text-gray-400 hover:text-gray-600">
        <span aria-hidden="true">&times;</span>
      </button>
      <div class="flex justify-center mb-4">
        <span class="bg-green-100 text-green-600 rounded-full p-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        </span>
      </div>
      <h2 class="text-2xl font-semibold mb-2">Success</h2>
      <p class="text-gray-600 mb-4">
        Check your email for a booking confirmation. We'll see you soon!
      </p>
      <button @click="closeModal" class="bg-green-500 text-white rounded px-4 py-2">
        OK
      </button>
    </div>
  </div>
</template>

<style scoped>
</style>
