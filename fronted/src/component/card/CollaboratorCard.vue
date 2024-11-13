<script setup>
// const emit = defineEmits(['cancel','comfirm'])
</script>

<template>
  <div class="px-4 py-6">
    <div
      class="relative bg-[#eff6e0] p-6 rounded-xl shadow-md text-gray-700 w-80"
    >
      <div class="flex justify-between items-center mb-4">
        <span
          class="bg-white text-gray-500 text-xs font-medium px-2 py-1 rounded-full"
        >
          <slot name="status"></slot>
        </span>
        <button class="text-gray-500 hover:text-gray-700">
          <slot name="btn"> </slot>
        </button>
      </div>

      <div class="flex items-center mb-4">
        <div>
          <p class="text-base font-bold text-gray-900">
            <slot name="name"></slot>
          </p>
          <div class="flex items-center space-x-2 mt-2">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4 text-blue-500"
              viewBox="0 0 24 24"
            >
              <g fill="currentColor">
                <path
                  d="M22 7.535V17a3 3 0 0 1-2.824 2.995L19 20H5a3 3 0 0 1-2.995-2.824L2 17V7.535l9.445 6.297l.116.066a1 1 0 0 0 .878 0l.116-.066z"
                />
                <path
                  d="M19 4c1.08 0 2.027.57 2.555 1.427L12 11.797l-9.555-6.37a3 3 0 0 1 2.354-1.42L5 4z"
                />
              </g>
            </svg>
            <h3 class="text-sm font-semibold text-blue-500">
              <slot name="email"></slot>
            </h3>
          </div>
        </div>
      </div>

      <div class="flex flex-wrap gap-2 mb-4">
        <!-- <span
          class="text-xs font-medium bg-white text-gray-600 px-3 py-1 rounded-full"
          >COLLABORATOR</span
        > -->
        <!-- <span
          class="text-xs font-medium bg-white text-gray-600 px-3 py-1 rounded-full"
          ><slot name="role"></slot
        ></span> -->
      </div>
      <div class="flex justify-between items-center mt-4">
        <div>
          <!-- <p class="text-lg font-semibold text-gray-900">$250/hr</p>
            <p class="text-sm text-gray-500">San Francisco, CA</p> -->
        </div>
        <div class="mt-3 relative inline-flex">
          <slot name="access-right"></slot>
        </div>

        <!-- <button
            class="text-white bg-gray-900 text-sm font-medium px-4 py-2 rounded-full hover:bg-gray-800"
          >
            Details
          </button> -->
      </div>
    </div>
  </div>
</template>

<style scoped>
.customPink {
  color: #f785b1;
}

.customPurple {
  color: #9391e4;
}

.customBlue {
  color: #9fc3e9;
}

.customRed {
  color: #eb4343;
}

.customBgYellow {
  background-color: #fae59d;
}
</style>
