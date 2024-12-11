<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBackHome = () => {
  router.go(-1)
}
</script>

<template>
  <div
    class="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8 text-center"
  >
    <slot name="svgEror"></slot>
    <p
      class="mt-4 text-3xl font-bold tracking-tight text-gray-900 sm:text-5xl error-title"
    >
      Page not found
    </p>
    <p class="mt-6 text-base leading-7 text-gray-600">
      There is a problem, Please try agian later.
    </p>
    <div class="mt-10 flex items-center justify-center gap-x-6">
      <button
        @click="goBackHome"
        class="rounded-button rounded-md bg-error px-3.5 py-2.5 text-sm font-semibold text-white"
      >
        <i class="mdi mdi-arrow-left mr-2"></i>Go back home
      </button>
    </div>
  </div>
</template>

<style scoped></style>
