<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const countdown = ref(3);
const router = useRouter();

onMounted(() => {
  // startCountdown();
});

// const startCountdown = () => {
//   let interval = setInterval(() => {
//     countdown.value--;
//     if (countdown.value <= 0) {
//       clearInterval(interval);
//       router.push({ name: 'Board' });
//     }
//   }, 1000);
// };

const goBackHome = () => {
  router.push({ name: 'Board' });
};


</script>

<template>
  <div class="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8 text-center">
    <h1 class="text-base font-semibold" style="color: #f785b1">404</h1>
    <p class="mt-4 text-3xl font-bold tracking-tight text-gray-900 sm:text-5xl error-title">Page not found</p>
    <p class="mt-6 text-base leading-7 text-gray-600">There is a problem. Please try agian later.</p>
    <div class="mt-10 flex items-center justify-center gap-x-6">
      <button @click="goBackHome" class="rounded-button rounded-md bg-error px-3.5 py-2.5 text-sm font-semibold text-white">Go back home</button>
    </div>
    <!-- <div class="mt-10 flex items-center justify-center gap-x-6">
      <p class="error-title">{{ countdown }}</p>
    </div> -->
  </div>
</template>

<style scoped>
.bg-error {
  background-color: #f785b1;
}

.rounded-button {
  border-radius: 9999px;
}

button:hover{
  background-color: #9391E4
}

.error-title {
  font-size: 48px;
  font-weight: bold;
  color: #eb4343;
}
</style>
