<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  isShow: Boolean
})

const emit = defineEmits(['close'])

const closeModal = () => {
  emit('close')
}
</script>

<template>
  <div v-if="isShow" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white rounded-3xl shadow-lg p-8 w-96 max-w-full relative text-center">
      <button @click="closeModal" class="absolute top-3 right-3 text-gray-400 hover:text-gray-600">
        &times;
      </button>
      <h3 class="text-4xl font-extrabold text-gray-900 mb-2">Sign Up</h3>
      <p class="text-gray-600 mb-6">Join the Wasabi Ticket family and never miss your favorite concerts!</p>
      
      <form class="space-y-5">
        <input
          type="text"
          placeholder="Username"
          class="w-full px-5 py-3 bg-gray-100 border border-gray-200 rounded-xl text-sm text-gray-700 placeholder-gray-500 focus:outline-none focus:border-gray-400"
        />
        <input
          type="text"
          placeholder="First Name"
          class="w-full px-5 py-3 bg-gray-100 border border-gray-200 rounded-xl text-sm text-gray-700 placeholder-gray-500 focus:outline-none focus:border-gray-400"
        />
        <input
          type="text"
          placeholder="Last Name"
          class="w-full px-5 py-3 bg-gray-100 border border-gray-200 rounded-xl text-sm text-gray-700 placeholder-gray-500 focus:outline-none focus:border-gray-400"
        />
        <input
          type="email"
          placeholder="Email"
          class="w-full px-5 py-3 bg-gray-100 border border-gray-200 rounded-xl text-sm text-gray-700 placeholder-gray-500 focus:outline-none focus:border-gray-400"
        />
        
        <div class="relative">
          <input
            type="password"
            placeholder="Password"
            class="w-full px-5 py-3 bg-gray-100 border border-gray-200 rounded-xl text-sm text-gray-700 placeholder-gray-500 focus:outline-none focus:border-gray-400"
          />
          <button
            type="button"
            class="absolute inset-y-0 right-4 text-gray-500 hover:text-gray-700"
          >
            <!-- Toggle Password Visibility Icon -->
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.944 9.543 7-1.275 4.056-5.065 7-9.543 7-4.478 0-8.268-2.944-9.542-7z" />
            </svg>
          </button>
        </div>
        
        <button type="submit" class="w-full py-3 bg-purple-blue-500 text-white font-bold rounded-xl hover:bg-purple-blue-600 transition-all duration-300">
          Sign Up
        </button>
      </form>
      
      <p class="mt-4 text-gray-700">Already have an account? 
        <a href="javascript:void(0)" class="text-purple-blue-500 font-bold">Sign In</a>
      </p>
    </div>
  </div>
</template>

<style scoped>
</style>
