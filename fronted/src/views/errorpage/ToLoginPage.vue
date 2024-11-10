<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()
const referringId = computed(() => route.query.refId)
const isSignedIn = ref(false) // แทนที่ด้วยฟังก์ชันตรวจสอบสถานะการเข้าสู่ระบบจริง

const toLogin = () => {
  router.push({ name: 'Login', query: { refId: referringId.value } })
}
</script>

<template>
  <div class="flex justify-center items-center min-h-screen bg-gray-100">
    <img src="/image/signup.png" alt="Log-in" class="w-1/2 md:w-1/3 lg:w-1/4" />
    <div v-if="!isSignedIn">
      <p class="text-3xl font-bold tracking-tight text-gray-900 sm:text-5xl">
        Welcome Back!
      </p>
      <p class="mt-6 text-base leading-7 text-gray-600">
        To access this board, please sign in first < 3
        <br />Once you’re signed in, we’ll bring you right back here!
      </p>
      <button
        @click="toLogin"
        class="mt-10 bg-error px-3.5 py-2.5 text-sm font-semibold text-white rounded-button"
      >
        <i class="mdi mdi-arrow-left mr-2"></i>Log In to Your Board
      </button>
    </div>
  </div>
</template>

<style scoped>
.bg-error {
  background-color: #f785b1;
}
.rounded-button {
  border-radius: 9999px;
}
button:hover {
  background-color: #9391e4;
}
</style>
