<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const boardId = ref()
const route = useRoute()

// Declare the events 'close', 'cancel', and 'confirm'
const emit = defineEmits(['close', 'cancel', 'confirm'])

const props = defineProps({
  isOpen: Boolean,
  tempVisibility: String
})

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)
</script>

<template>
  <div
    v-if="isOpen"
    class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50 py-10 lg:left-32"
  >
    <div class="itbkk-modal-alert modal-box w-1/3">
      <h3 class="font-bold text-lg customPurple"><slot name="headerName"></slot></h3>

      <p class="py-4 itbkk-message">
        <!-- Are you sure you want to change the board visibility to
					{{ visibility }} ? -->
        <slot name="messageName"></slot>
      </p>
      <div class="modal-action">
        <button
          @click="$emit('cancel')"
          class="btn itbkk-button-cancel bg-customRed text-white"
        >
          Cancel
        </button>
        <button
          @click="$emit('confirm')"
          class="btn itbkk-button-confirm bg-green-400 text-white"
        >
          Confirm
        </button>
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
</style>
