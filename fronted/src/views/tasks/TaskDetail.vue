<script setup>
import { getItemById, getAttachments } from '@/libs/fetchUtils'
import { ref, watch } from 'vue'
import { toDate } from '../../libs/toDate'
import { useRoute, useRouter } from 'vue-router'
import PreviewFile from '../../component/files/PreviewFile.vue'

// ----------------------- Router -----------------------

const router = useRouter()
const route = useRoute()

// ----------------------- Params -----------------------

const props = defineProps({
  todoId: Number,
  isOpenModal: Boolean
})
const emit = defineEmits(['close'])

const isFilePreviewOpen = ref(false)
const previewFileData = ref({})

const openPreviewFile = (file) => {
  console.log('Opening file:', file)

  const fileURL = URL.createObjectURL(file)
  previewFileData.value = {
    name: file.name,
    url: fileURL,
    type: file.type,
    size: file.size
  }

  isFilePreviewOpen.value = true
}

const closePreviewFile = () => {
  isFilePreviewOpen.value = false
}

const todo = ref({
  id: '',
  title: '',
  description: '',
  assignees: '',
  status: '',
  createdOn: '',
  updatedOn: '',
  attachments: []
})

const boardId = ref()

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

watch(
  () => props.todoId,
  async (newValue) => {
    if (newValue) {
      const { item, responsed } = await getItemById(newValue, boardId.value)
      if (responsed === 403) {
        router.push({ name: 'ErrorPagePermission' })
      }
      todo.value = item
      const { statusCode, data } = await getAttachments(boardId.value, newValue)
      if (statusCode === 200 && Array.isArray(data)) {
        todo.value.attachments = data
        console.log(todo.value.attachments)
      } else {
        todo.value.attachments = []
        console.error('Failed to fetch attachments')
      }
    }
  },
  { immediate: true }
)

const files = ref([])

watch(
  () => todo.value.attachments,
  (attachments) => {
    if (Array.isArray(attachments) && attachments.length > 0) {
      files.value = attachments.map((attachment) => {
        const byteCharacters = atob(attachment.fileData)
        const byteNumbers = new Uint8Array(
          byteCharacters.split('').map((char) => char.charCodeAt(0))
        )
        const blob = new Blob([byteNumbers], { type: attachment.fileType })

        return new File([blob], attachment.fileName, {
          type: attachment.fileType
        })
      })
    } else {
      files.value = []
    }
  },
  { immediate: true }
)

const myModal = ref(null)

// ฟังก์ชันเปิด modal
// const openModal = () => {
//   if (myModal.value) {
//     myModal.value.showModal()
//   }
// }

// ฟังก์ชันปิด modal
const closeModal = () => {
  // if (myModal.value) {
  //   myModal.value.close()
  // }
  emit('close')
  router.push({ name: 'TaskList', params: { id: boardId.value } })
}

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
</script>

<template>
  <!-- Modal window -->
  <dialog
    v-if="isOpenModal"
    ref="myModal"
    class="itbkk-modal-task w-full h-full flex inset-0 z-20 items-center justify-center bg-gray-500 bg-opacity-50"
  >
    <div
      class="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-auto"
    >
      <div class="p-6 space-y-6">
        <!-- Title and Status -->
        <div class="flex space-x-4">
          <!-- Title -->
          <div class="flex-1 space-y-1">
            <label class="block text-base font-medium text-[#9391e4]">
              Title
            </label>
            <input
              type="text"
              v-model="todo.title"
              placeholder="Title"
              maxlength="100"
              class="itbkk-title w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
          </div>

          <!-- Status -->
          <!-- <div class="w-1/5 space-y-1">
            <label class="block text-base font-medium text-[#9391e4]">
              Status
            </label>
            <select
              v-model="todo.status"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg"
            >
              <option
                class="itbkk-status"
                v-for="status in statusList"
                :value="status.name"
              >
                {{ status.name }}
              </option>
            </select>
          </div> -->
        </div>

        <!-- Status -->
        <!-- <div class="itbkk-status mb-4 mt-2">
            <span
              class="block text-lg font-bold leading-6 text-gray-900 mb-2"
              style="color: #9391e4"
              >Status</span
            >
            {{ todo.status }}
          </div> -->

        <!-- Description -->
        <div class="space-y-1">
          <label class="block text-base font-medium text-[#9391e4]">
            Description
          </label>
          <textarea
            disabled
            id="description"
            maxlength="500"
            rows="4"
            :class="{
              'italic text-gray-500':
                todo.description?.length === 0 ||
                todo.description?.trim() === '' ||
                todo.description === null
            }"
            class="itbkk-description w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg h-24"
          >
          {{ todo.description || 'No Description Provided' }}
          </textarea>
        </div>

        <!-- Assignees -->
        <div>
          <label class="block text-base font-medium text-[#9391e4]">
            Assignees
          </label>
          <textarea
            disabled
            id="assignees"
            maxlength="30"
            rows="4"
            :class="{
              'italic text-gray-500':
                todo.assignees?.length === 0 ||
                todo.assignees?.trim() === '' ||
                todo.assignees === null
            }"
            class="itbkk-assignees w-full px-4 py-2 border border-gray-300 rounded-lg"
          >
          {{ todo.assignees || 'Unassigned' }}
          </textarea>
        </div>

        <!-- Attachments Section -->
        <div class="attachments-section border-t border-gray-300 pt-4 mt-6">
          <h2 class="text-lg font-bold mb-2" style="color: #9391e4">
            Attachments
          </h2>
          <div v-if="todo.attachments?.length > 0">
            <div
              v-for="(file, index) in files"
              :key="index"
              class="flex flex-col items-start bg-gray-100 rounded-lg p-2"
              @click="openPreviewFile(file)"
            >
              <div
                class="w-full h-14 bg-gray-300 rounded mb-1 relative flex items-center justify-center"
              >
                <!-- <p v-if="isImage(file)" class="text-xs text-gray-600">
                      <img :src="file.url || getFileIcon(file)" alt="Preview"
                        class="object-cover w-full h-full rounded" />
                    </p> -->
                <p class="text-xs text-gray-600 truncate">
                  {{ file.name }}
                </p>
              </div>
              <p class="text-xs text-gray-600 truncate">
                {{ file.name }}
              </p>
              <p class="text-xs text-gray-600 truncate">
                {{ (file.size / 1024).toFixed(2) }}
              </p>
            </div>
          </div>
          <div v-else class="italic text-gray-500">
            No attachments available.
          </div>
        </div>
        <PreviewFile
          v-if="isFilePreviewOpen"
          :file="previewFileData"
          @close="closePreviewFile"
        />

        <!-- Metadata Section -->
        <div class="grid grid-cols-3 gap-4 text-sm text-gray-600">
          <!-- TimeZone -->
          <div class="text-center">
            <span class="block font-bold text-[#9391e4]">TimeZone</span>
            <p class="itbkk-timezone">{{ TimeZone }}</p>
          </div>

          <!-- Created On -->
          <div class="text-center">
            <span class="block font-bold text-[#9391e4]">Created On</span>
            <p class="itbkk-created-on">{{ toDate(todo.createdOn) }}</p>
          </div>

          <!-- Updated On -->
          <div class="text-center">
            <span class="block font-bold text-[#9391e4]">Updated On</span>
            <p class="itbkk-updated-on">{{ toDate(todo.updatedOn) }}</p>
          </div>
        </div>

        <!-- Close Button -->
        <div
          class="itbkk-button px-6 py-4 flex justify-end border-t border-gray-200"
        >
          <button
            @click="closeModal"
            class="itbkk-close-button text-sm px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </dialog>
</template>

<style>
.itbkk-modal-task > div {
  margin-left: 18%;
}

@media (max-width: 768px) {
  .itbkk-modal-task > div {
    margin-left: 0;
  }
}
</style>
