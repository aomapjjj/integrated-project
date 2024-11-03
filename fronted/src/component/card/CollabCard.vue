<script setup></script>

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

      <!-- กากบาท -->
      <button
        :disabled="disabledButtonWhileOpenPublic"
        @click="showRemoveModal(item.id)"
        class="itbkk-collab -ml-4 flex items-center justify-center rounded-full px-2 hover:text-red-500 text-red-400 transition duration-200 ease-in-out"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-4 h-4"
          viewBox="0 0 24 24"
        >
          <path
            fill="currentColor"
            fill-rule="evenodd"
            d="m12 10.586l5.657-5.657l1.414 1.414L13.414 12l5.657 5.657l-1.414 1.414L12 13.414l-5.657 5.657l-1.414-1.414L10.586 12L4.929 6.343L6.343 4.93z"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped></style>
