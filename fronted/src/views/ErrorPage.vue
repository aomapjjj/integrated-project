<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const countdown = ref(3)
const router = useRouter()

onMounted(() => {
  startCountdown()
})

const startCountdown = () => {
  let interval = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(interval)
      router.push({ name: 'TaskList' })
    }
  }, 1000)
}
</script>


<template>
  <div class="error-page">
    <h1 class="error-title">404 Not Found</h1>
    <p class="error-title" >Oops! Something went wrong</p>
    <p class="error-title" >{{ countdown }}</p>
    <span class="loading loading-spinner loading-lg"></span>
  </div>
</template>

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.error-title {
  font-size: 48px;
  font-weight: bold;
  color: #d1310d ;
}
</style>

