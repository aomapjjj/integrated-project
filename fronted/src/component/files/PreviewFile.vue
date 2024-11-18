<script setup>
import { ref } from "vue";

// File preview data
const fileName = ref("");
const currentPage = ref(1);
const totalPages = ref(1);
const zoomLevel = ref(1);
const fileUrl = ref("");

// Modal visibility
const isModalOpen = ref(false);

// Sample files
const files = ref([
  { id: 1, name: "Resume.pdf", url: "path/to/resume.pdf", pages: 10 },
  { id: 2, name: "Portfolio.pdf", url: "path/to/portfolio.pdf", pages: 5 },
  { id: 3, name: "Report.pdf", url: "path/to/report.pdf", pages: 20 },
]);

// File interaction methods
const openModal = (file) => {
  fileName.value = file.name;
  fileUrl.value = file.url;
  totalPages.value = file.pages;
  currentPage.value = 1;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value -= 1;
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value += 1;
};

const downloadFile = () => {
  window.open(fileUrl.value, "_blank");
};

const zoomIn = () => {
  zoomLevel.value += 0.1;
  updateZoom();
};

const zoomOut = () => {
  if (zoomLevel.value > 0.1) zoomLevel.value -= 0.1;
  updateZoom();
};

const fitToScreen = () => {
  zoomLevel.value = 1;
  updateZoom();
};

const updateZoom = () => {
  const iframe = document.querySelector(".file-frame");
  if (iframe) {
    iframe.style.transform = `scale(${zoomLevel.value})`;
  }
};
</script>

<template>
  <!-- File List -->
  <div class="grid grid-cols-3 gap-4 p-6">
    <div
      v-for="file in files"
      :key="file.id"
      class="p-4 border border-gray-300 rounded-lg shadow hover:bg-gray-100 cursor-pointer"
      @click="openModal(file)"
    >
      <h3 class="text-base font-semibold">{{ file.name }}</h3>
      <p class="text-sm text-gray-500">Pages: {{ file.pages }}</p>
    </div>
  </div>

  <!-- Modal -->
  <div
    v-if="isModalOpen"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
  >
    <div class="w-full max-w-4xl bg-white rounded-lg shadow-lg">
      <!-- Modal Header -->
      <div class="flex justify-between items-center px-6 py-4 border-b border-gray-300">
        <h3 class="text-lg font-semibold">{{ fileName }}</h3>
        <button
          @click="closeModal"
          class="text-gray-500 hover:text-gray-700 focus:outline-none"
        >
          ✕
        </button>
      </div>

      <!-- Modal Content -->
      <div class="relative h-[600px] overflow-hidden">
        <iframe :src="fileUrl" frameborder="0" class="w-full h-full file-frame"></iframe>
      </div>

      <!-- Modal Footer -->
      <div class="flex justify-between items-center px-6 py-4 border-t border-gray-300">
        <span class="text-sm">Page {{ currentPage }} / {{ totalPages }}</span>
        <div class="flex space-x-2">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="px-2 py-1 bg-blue-500 text-white rounded disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            &lt;
          </button>
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="px-2 py-1 bg-blue-500 text-white rounded disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            &gt;
          </button>
          <button
            @click="zoomIn"
            class="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            +
          </button>
          <button
            @click="zoomOut"
            class="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            -
          </button>
          <button
            @click="fitToScreen"
            class="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Fit to screen
          </button>
          <button
            @click="downloadFile"
            class="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Download
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* No additional styles are needed since Tailwind CSS is used */
</style>
