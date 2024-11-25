<script setup>
import { getItemById, getAttachments } from '@/libs/fetchUtils'
import { ref, watch, computed } from 'vue'
import { toDate } from '../../libs/toDate'
import { useRoute, useRouter } from 'vue-router'
import PreviewFile from '../../component/files/PreviewFile.vue'
import Iconfile from '@/component/files/Iconfile.vue'
import { useTasks } from '../../stores/store.js'

// ----------------------- Router -----------------------

const router = useRouter()
const route = useRoute()

// ----------------------- Params -----------------------
const boardId = ref()
const props = defineProps({
  todoId: Number,
  isOpenModal: Boolean
})

const tasksStore = useTasks()
const emit = defineEmits(['close'])

const files = computed(() => {
  const attachments = tasksStore.getAttachmentsByTaskId(props.todoId)
  return attachments.map((attachment) => {
    const byteCharacters = atob(attachment.fileData)
    const byteNumbers = new Uint8Array(
      byteCharacters.split('').map((char) => char.charCodeAt(0))
    )
    const blob = new Blob([byteNumbers], { type: attachment.fileType })
    return new File([blob], attachment.fileName, { type: attachment.fileType })
  })
})
const isFilePreviewOpen = ref(false)
const previewFileData = ref({})

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

// ----------------------- Functions -----------------------

const openPreviewFile = (file) => {
  const fileURL = URL.createObjectURL(file)
  previewFileData.value = {
    name: file.name,
    url: fileURL,
    type: file.type,
    size: file.size,
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

// ----------------------- Modal -----------------------
const myModal = ref(null)

const closeModal = () => {
  emit('close')
  router.push({ name: 'TaskList', params: { id: boardId.value } })
}

const getFilePreview = (file) => {
  if (file.type.startsWith('image/')) {
    return URL.createObjectURL(file)
  } else if (file.type === 'application/pdf') {
    return URL.createObjectURL(file)
  } else if (file.type.startsWith('text/')) {
    return new Promise((resolve) => {
      const reader = new FileReader()
      reader.onload = (event) => {
        resolve(event.target.result)
      }
      reader.readAsText(file)
    })
  }
  return URL.createObjectURL(file)
}

const fileContent = ref([])

const loadTextFileContent = (file, index) => {
  if (file.type.startsWith('text/')) {
    const reader = new FileReader()
    reader.onload = (event) => {
      fileContent.value[index] = event.target.result
    }
    reader.readAsText(file)
  }
}

watch(files, (newFiles) => {
  newFiles.forEach((file, index) => {
    if (file.type.startsWith('text/')) {
      loadTextFileContent(file, index)
    }
  })
})

const selectedTask = computed(() => {
  if (!props.todoId) return null;
  return tasksStore.getTasks().find((task) => task.id === props.todoId) || null;
});

watch(selectedTask, (task) => {
  if (task?.attachments) {
    files.value = task.attachments.map(attachment => {
      const blob = new Blob([attachment.fileData], { type: attachment.fileType })
      return new File([blob], attachment.fileName, { type: attachment.fileType })
    })
  } else {
    files.value = []
  }
})


</script>

<template>
  <!-- Modal window -->
  <dialog v-if="isOpenModal" ref="myModal"
    class="itbkk-modal-task w-full h-full flex inset-0 z-20 items-center justify-center bg-gray-500 bg-opacity-50">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-auto">
      <div class="p-6 space-y-6">
        <!-- Title and Status -->
        <div class="flex space-x-4">
          <!-- Title -->
          <div class="flex-1 space-y-1">
            <label class="block text-base font-medium text-[#9391e4]">
              Title
            </label>
            <input disabled type="text" v-model="todo.title" placeholder="Title" maxlength="100"
              class="itbkk-title w-full px-4 py-2 border border-gray-300 rounded-lg" />
          </div>

          <!-- Status -->
          <div class="w-1/5 space-y-1">
            <label class="block text-base font-medium text-[#9391e4]">
              Status
            </label>
            <div class="itbkk-status w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 flex items-center">
              {{ todo.status }}
            </div>
          </div>
        </div>

        <!-- Description -->
        <div class="space-y-1">
          <label class="block text-base font-medium text-[#9391e4]">
            Description
          </label>
          <textarea disabled id="description" maxlength="500" rows="4" :class="{
            'italic text-gray-500':
              todo.description?.length === 0 ||
              todo.description?.trim() === '' ||
              todo.description === null
          }" class="itbkk-description w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg h-24">
          {{ todo.description || 'No Description Provided' }}
          </textarea>
        </div>

        <!-- Assignees -->
        <div>
          <label class="block text-base font-medium text-[#9391e4]">
            Assignees
          </label>
          <textarea disabled id="assignees" maxlength="30" rows="4" :class="{
            'italic text-gray-500':
              todo.assignees?.length === 0 ||
              todo.assignees?.trim() === '' ||
              todo.assignees === null
          }" class="itbkk-assignees w-full px-4 py-2 border border-gray-300 rounded-lg">
          {{ todo.assignees || 'Unassigned' }}
          </textarea>
        </div>

        <!-- Attachments Section -->
        <div class="attachments-section">
          <label class="block text-base font-medium text-[#9391e4]">
            Attachments
          </label>
          <div v-if="todo.attachments?.length > 0">
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-2">
              <div v-for="(file, index) in files" :key="index"
                class="flex flex-col items-start bg-gray-100 hover:bg-gray-200 rounded-lg p-2">
                <div class="w-full h-20 bg-gray-300 rounded mb-1 relative flex items-center justify-center">
                  <div class="w-full h-20 bg-gray-100 rounded overflow-hidden flex items-center justify-center">
                    <Iconfile :file="file" :fileContent="fileContent[index]" @click="openPreviewFile(file)" />
                  </div>
                </div>
                <p @click="openPreviewFile(file)" class="text-xs text-gray-600 truncate w-full overflow-hidden">
                  {{ file.name }}
                </p>
                <p @click="openPreviewFile(file)" class="text-xs text-gray-600 truncate">
                  {{ (file.size / (1024 * 1024)).toFixed(2) }} MB
                </p>
              </div>
            </div>
          </div>
          <div v-else>
            <ul id="gallery" class="flex flex-1 flex-wrap -m-1">
              <li id="empty" class="h-full w-full text-center flex flex-col justify-center items-center">
                <img class="mx-auto w-28"
                  src="https://user-images.githubusercontent.com/507615/54591670-ac0a0180-4a65-11e9-846c-e55ffce0fe7b.png"
                  alt="no data" />
                <span class="text-small text-gray-500">No files attached</span>
              </li>
            </ul>
          </div>
        </div>
        <PreviewFile v-if="isFilePreviewOpen" :file="previewFileData" @close="closePreviewFile" />

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
        <div class="itbkk-button px-6 py-4 flex justify-end border-t border-gray-200">
          <button @click="closeModal"
            class="itbkk-close-button text-sm px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none">
            Close
          </button>
        </div>
      </div>
    </div>
  </dialog>
</template>

<style>
.itbkk-modal-task>div {
  margin-left: 6%;
}

@media (max-width: 768px) {
  .itbkk-modal-task>div {
    margin-left: 0;
  }
}
</style>
