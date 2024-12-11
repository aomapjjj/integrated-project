<script setup>

const props = defineProps({
  isSuccess: Boolean
})

const emit = defineEmits(['close'])
</script>

<template>
  <div
    v-if="isSuccess"
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
  >
    <div class="bg-white rounded-lg p-6 w-80 text-center relative">
      <div class="flex justify-center mb-4">
        <span class="bg-green-100 text-green-400 rounded-full p-2">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 13l4 4L19 7"
            />
          </svg>
        </span>
      </div>
      <h2 class="text-2xl font-semibold mb-2">Invitation Sent!</h2>
      <p class="text-gray-600 mb-4">
        Your invitation is on its way.

        <span class="font-semibold"
          >Check your email to join the board!</span
        >
      </p>
      <button
        @click="$emit('close')"
        class="bg-green-400 text-white rounded-full  px-4 py-2"
      >
        Got it
      </button>
    </div>
  </div>
</template>

<style scoped></style>
