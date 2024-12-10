<script setup>
import { DotLottieVue } from '@lottiefiles/dotlottie-vue'

const props = defineProps({
  isLoading: Boolean
})
</script>

<template>
  <div
    v-if="isLoading"
    class="fixed inset-0 bg-white bg-opacity-20 backdrop-blur-sm flex items-center justify-center z-50"
  >
    <div
      class="flex flex-col items-center space-y-4 p-8"
    >
      <DotLottieVue
        style="height: 300px; width: 300px"
        autoplay
        loop
        src="https://lottie.host/f6798d97-5e54-472e-80f9-6c074a732376/h0aswDGwkE.lottie"
      />
      <slot>
      <h3 class="font-bold text-lg text-customPurple">
        Saving your task... Almost there!
      </h3>
      <p class="text-sm text-gray-600">
        Please wait while we save your changes.
      </p>
      </slot>
    </div>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.fixed {
  animation: fadeIn 0.3s ease-in-out;
}
</style>
