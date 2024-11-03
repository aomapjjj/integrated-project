<script setup>


</script>

<template>
  <div class="flex flex-col">
    <div
      class="itbkk-item flex items-start rounded-lg bg-white p-4 shadow-lg w-[300px] min-w-[300px]"
    >
      <div class="flex items-center">
        <div
          class="flex h-12 w-12 items-center justify-center rounded-full bg-customPurple"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6 text-white"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
            />
          </svg>
        </div>

        <div class="ml-4">
          <slot name="name"></slot>
          <slot name="email"></slot>
          <slot name="access-right"></slot>
        </div>
      </div>

      <slot name="btn"></slot>
      
    </div>
  </div>
</template>

<style scoped></style>
