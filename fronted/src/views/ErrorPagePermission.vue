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
    <h1 class="font-semibold text-4xl " style="color: #f785b1 ">403</h1>
    <p class="mt-6 text-3xl leading-7 text-gray-600">Access denied, you do not have permission to view this page.</p>
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
