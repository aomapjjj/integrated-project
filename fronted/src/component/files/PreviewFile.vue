<script setup>
import { ref, watch } from 'vue'

// ------------ Params ------------
const files = ref([])
const fileName = ref('')
const fileUrl = ref('')

const currentPage = ref(1)
const totalPages = ref(1)
const zoomLevel = ref(1)

const isModalOpen = ref(false)

const props = defineProps({
  file: Object
})

const emit = defineEmits(['close'])

watch(
  () => props.file,
  (newFile) => {
    if (newFile && newFile.url) {
      fileName.value = newFile.name
      fileUrl.value = newFile.url
      totalPages.value = newFile.pages || 1
    }
  },
  { immediate: true }
)

const openModal = (file) => {
  fileName.value = file.name
  fileUrl.value = file.url
  totalPages.value = file.pages
  currentPage.value = 1
  isModalOpen.value = true
}

const closeModal = () => {
  emit('close')
}
</script>

<template>
  <!-- File List -->
  <div class="grid grid-cols-2 md:grid-cols-3 gap-4 p-6">
    <div
      v-for="file in files"
      :key="file.name"
      class="p-4 border border-gray-200 rounded-lg shadow-md hover:bg-blue-50 cursor-pointer transition duration-200 ease-in-out"
      @click="openModal(file)"
    >
      <h3 class="text-lg font-semibold text-gray-700 truncate">
        {{ file.name }}
      </h3>
    </div>
  </div>

  <!-- Modal -->
  <div v-if="fileUrl" class="fixed inset-0 flex items-center justify-center">
    <div
      class="w-full min-h-full h-screen mb-6 flex flex-col overflow-hidden bg-customPink"
    >
      <!-- Modal Header -->
      <div class="flex justify-between items-center px-6 py-4 text-white">
        <h3 class="text-base font-semibold truncate">{{ fileName }}</h3>
        <button
          @click="closeModal"
          class="text-white hover:text-gray-100 rounded-full p-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6"
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

      <!-- Modal Content -->
      <div class="flex-grow relative overflow-hidden bg-gray-200">
        <div
          class="absolute inset-0 flex items-center justify-center"
          :style="{
            transform: `scale(${zoomLevel})`,
            transformOrigin: 'center center',
            padding: zoomLevel < 1 ? '10px' : '0'
          }"
        >
          <iframe :src="fileUrl" frameborder="0" class="w-full h-full"></iframe>
        </div>
      </div>

      <!-- Modal Footer -->
      <div
        class="flex justify-end items-center px-6 py-4 bg-gray-50 border-t border-gray-200"
      >
        <div class="flex space-x-2">
          <button
            @click="zoomLevel = Math.min(zoomLevel + 0.2, 2)"
            class="px-3 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 24 24"
            >
              <g fill="none">
                <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M2 11a9 9 0 1 1 16.032 5.618l3.675 3.675a1 1 0 0 1-1.414 1.414l-3.675-3.675A9 9 0 0 1 2 11zm10-3a1 1 0 1 0-2 0v2H8a1 1 0 1 0 0 2h2v2a1 1 0 1 0 2 0v-2h2a1 1 0 1 0 0-2h-2V8z"
                  fill="currentColor"
                />
              </g>
            </svg>
          </button>
          <button
            @click="zoomLevel = Math.max(zoomLevel - 0.2, 0.5)"
            class="px-3 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 24 24"
            >
              <g fill="none">
                <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M2 11a9 9 0 1 1 16.032 5.618l3.675 3.675a1 1 0 0 1-1.414 1.414l-3.675-3.675A9 9 0 0 1 2 11zm6-1a1 1 0 1 0 0 2h6a1 1 0 1 0 0-2H8z"
                  fill="currentColor"
                />
              </g>
            </svg>
          </button>
          <button
            @click="zoomLevel = 1"
            class="px-3 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M20 8V6h-2q-.425 0-.712-.288T17 5t.288-.712T18 4h2q.825 0 1.413.588T22 6v2q0 .425-.288.713T21 9t-.712-.288T20 8M2 8V6q0-.825.588-1.412T4 4h2q.425 0 .713.288T7 5t-.288.713T6 6H4v2q0 .425-.288.713T3 9t-.712-.288T2 8m18 12h-2q-.425 0-.712-.288T17 19t.288-.712T18 18h2v-2q0-.425.288-.712T21 15t.713.288T22 16v2q0 .825-.587 1.413T20 20M4 20q-.825 0-1.412-.587T2 18v-2q0-.425.288-.712T3 15t.713.288T4 16v2h2q.425 0 .713.288T7 19t-.288.713T6 20zm2-6v-4q0-.825.588-1.412T8 8h8q.825 0 1.413.588T18 10v4q0 .825-.587 1.413T16 16H8q-.825 0-1.412-.587T6 14"
              />
            </svg>
          </button>
          <a
            :href="fileUrl"
            :download="fileName"
            class="px-3 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
          >
            Download
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Add a smooth scaling effect for the iframe content */
.file-frame {
  transition: transform 0.3s ease-in-out;
}
</style>
