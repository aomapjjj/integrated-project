<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import {
  getItems,
  getItemById,
  editItem,
  getAttachments,
  addAttachments
} from '@/libs/fetchUtils'
import { useTasks } from '../../stores/store'
import { toDate } from '../../libs/toDate'
import { useRoute, useRouter } from 'vue-router'
import { useLimitStore } from '../../stores/storeLimit'
import PreviewFile from '../../component/files/PreviewFile.vue'
import Iconfile from '@/component/files/Iconfile.vue'
import WaitModal from '@/component/modal/WaitModal.vue'

// ----------------------- Router -----------------------

const router = useRouter()
const route = useRoute()

// ----------------------- List Items -----------------------

const statusList = ref([])
const oldValue = ref({})

// ----------------------- Alerts -----------------------

const alertFailToEdit = ref(false)
const aletMessage = ref('')

// ----------------------- Stores -----------------------

const limitStore = useLimitStore()
const myTasks = useTasks()

// ----------------------- Enable & Disable -----------------------

const props = defineProps({
  todoId: Number,
  disabledBtn: Boolean
})

const waitModal = ref(false)

// ----------------------- Params -----------------------

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
// ----------------------- Alerts -----------------------

const showAlertEdit = ref(false)
const showAlertAfterEdit = ref(false)

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlTask = `${baseUrlboards}/${boardId.value}/tasks`
const baseUrlStatus = `${baseUrlboards}/${boardId.value}/statuses`

watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue, boardId.value)
    if (response && response.responsed === 200) {
      todo.value = response.item
      oldValue.value = { ...todo.value }
    }
    const itemsStatus = await getItems(baseUrlStatus)
    statusList.value = itemsStatus
  },
  { immediate: true }
)

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

// ----------------------- Modal -----------------------
const myModal = ref(null)

const openModal = async () => {
  
  router.push({ name: 'TaskEdit', params: { taskid: props.todoId } })
  myModal.value.showModal()
}

const closeModal = () => {
  myModal.value.close()
  router.push({ name: 'TaskList', params: { id: boardId.value } })
}

// ----------------------- messageResponse -----------------------

const messageResponse = ref('')
const messageResponseType = ref('')

const clearMessageResponse = () => {
  messageResponse.value = ''
  messageResponseType.value = ''
}

// const fetchUpdatedTask = async () => {
//   const response = await getItemById(props.todoId, boardId.value)
//   if (response && response.responsed === 200) {
//     todo.value = response.item
//   }
// }

const UpdateTask = async () => {
  if (isLimitReached.value) {
    return
  }
  waitModal.value = true
  const trimmedTodo = {
    title: todo.value.title?.trim(),
    description: todo.value.description?.trim(),
    assignees: todo.value.assignees?.trim(),
    status: todo.value.status
  }

  try {
    const edit = await editItem(baseUrlTask, props.todoId, trimmedTodo)
    myTasks.updateTask(
      edit.id,
      edit.title,
      edit.description,
      edit.assignees,
      edit.status,
      edit.createdOn,
      edit.updatedOn
    )

    if (files.value.length > 0) {
      const attachmentsResponse = await addAttachments(
        boardId.value,
        edit.id,
        files.value
      )

      if (attachmentsResponse.statusCode === 400) {
        messageResponse.value = attachmentsResponse.data.errors
          .map((error) => error.message)
          .join('\n')
        messageResponseType.value = 'error'
      } else if (
        attachmentsResponse.statusCode === 200 ||
        attachmentsResponse.statusCode === 201
      ) {
        messageResponse.value = 'Attachments uploaded successfully.'
        messageResponseType.value = 'success'
        todo.value.attachments = [...todo.value.attachments, ...files.value]
        myTasks.updateAttachments(todo.value.id, files.value)
      } else {
        messageResponse.value = 'Unexpected error occurred. Please try again.'
        messageResponseType.value = 'error'
        // await fetchUpdatedTask()
      }
    } else {
      messageResponse.value = 'Task updated successfully.'
      messageResponseType.value = 'success'
    }
    clearMessageResponse()
    closeModal()

    showAlertEdit.value = true
    showAlertAfterEdit.value = true
    setTimeout(() => {
      showAlertAfterEdit.value = false
    }, 2300)
  } catch (error) {
    console.error('Error updating task:', error)
    messageResponse.value = 'Unexpected error occurred.'
    messageResponseType.value = 'error'
  } finally {
    waitModal.value = false
    // isLoading.value = false
  }
}

const checkEqual = computed(() => {
  const trimmedTodo = {
    title: todo.value.title?.trim() || '',
    description: todo.value.description?.trim() || '',
    assignees: todo.value.assignees?.trim() || '',
    status: todo.value.status || ''
  }
  const trimmedOldValue = {
    title: oldValue.value.title?.trim() || '',
    description: oldValue.value.description?.trim() || '',
    assignees: oldValue.value.assignees?.trim() || '',
    status: oldValue.value.status || ''
  }

  // ตรวจสอบว่า attachments เป็น array
  const currentFiles = files.value.map((file) => file.name)
  const oldFiles = Array.isArray(todo.value.attachments)
    ? todo.value.attachments.map((attachment) => attachment.fileName)
    : []

  const filesChanged =
    currentFiles.length !== oldFiles.length ||
    !currentFiles.every((fileName) => oldFiles.includes(fileName))

  console.log('Current Files:', currentFiles)
  console.log('Old Files:', oldFiles)
  console.log('Files Changed:', filesChanged)

  return (
    JSON.stringify(trimmedTodo) === JSON.stringify(trimmedOldValue) &&
    !filesChanged
  )
})

// ----------------------- Validate -----------------------

const isValidTitle = (title) => {
  return title && title.trim().length >= 0 && title.trim().length <= 100
}

const isFormValid = computed(() => {
  return (
    isValidTitle(todo.value.title) &&
    (!todo.value.description || todo.value.description.trim().length <= 500) &&
    (!todo.value.assignees || todo.value.assignees.trim().length <= 30)
  )
})

const assigneesLength = computed(() => {
  return todo.value.assignees ? todo.value.assignees.length : 0
})

const descriptionLength = computed(() => {
  return todo.value.description ? todo.value.description.length : 0
})

//------------------------------------ Limit ----------------------------

const isLimitReached = computed(() => {
  const status = todo.value.status
  if (status === 'No Status' || status === 'Done') {
    return false
  }

  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = myTasks
      .getTasks()
      .filter((task) => task.status === status)
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      setTimeout(() => {
        alertFailToEdit.value = false
      }, 1800)
      alertFailToEdit.value = true
      aletMessage.value = `The status "${todo.value.status}" will have too many tasks. Please make progress and update status of existing tasks first.`
      return true
    }
  }

  return false
})

//------------------------------------ File ----------------------------
const files = ref([])
const maxFiles = 10
const maxTotalSizePerFile = 20 * 1024 * 1024

const handleFileChange = (event) => {
  const selectedFiles = Array.from(event.target.files)

  const existingFileNames = files.value.map((file) => file.name)
  const errorMessages = []
  const notAddedFiles = []
  const validFiles = []

  selectedFiles.forEach((file) => {
    // Check if the total file count exceeds the maximum limit
    if (files.value.length + validFiles.length >= maxFiles) {
      notAddedFiles.push(file.name)
      if (
        !errorMessages.includes(`Each task can have at most ${maxFiles} files.`)
      ) {
        errorMessages.push(`Each task can have at most ${maxFiles} files.`)
      }
      return
    }
    // Check if the file size exceeds the limit
    if (file.size > maxTotalSizePerFile) {
      notAddedFiles.push(file.name)
      if (
        !errorMessages.includes(
          `Each file cannot be larger than ${(
            maxTotalSizePerFile /
            (1024 * 1024)
          ).toFixed(2)} MB.`
        )
      ) {
        errorMessages.push(
          `Each file cannot be larger than ${(
            maxTotalSizePerFile /
            (1024 * 1024)
          ).toFixed(2)} MB.`
        )
      }
      return
    }
    if (existingFileNames.includes(file.name)) {
      notAddedFiles.push(file.name)
      if (
        !errorMessages.includes(
          `File with the same filename cannot be added or updated to the attachments. Please delete the attachment and add again to update the file.`
        )
      ) {
        errorMessages.push(
          `File with the same filename cannot be added or updated to the attachments. Please delete the attachment and add again to update the file.`
        )
      }
      return
    }
    validFiles.push(file)
  })

  files.value = [...files.value, ...validFiles]

  // Error Messages
  if (notAddedFiles.length > 0) {
    const formattedErrors = errorMessages
      .filter((msg, index, self) => self.indexOf(msg) === index)
      .map((msg, index) => ` ${index + 1}. ${msg}`)
      .join('\n')

    const formattedFiles = notAddedFiles
      .map((fileName) => `- ${fileName}`)
      .join('\n')

    messageResponse.value =
      `⚠️ Oops! Some issues occurred while uploading files:\n${formattedErrors}\n\n 📂 Files not added:\n${formattedFiles}`.trim()
    messageResponseType.value = 'error'
  } else {
    messageResponse.value = `✅ Files added successfully!\n`.trim()
    messageResponseType.value = 'success'
  }
}

watch(
  () => files,
  (newFiles) => {
    files = newFiles
    console.log('Updated files:', newFiles)
  }
)

watch(
  [isFormValid, checkEqual, isLimitReached],
  ([formValid, equalCheck, limitReached]) => {
    console.log('isFormValid:', formValid)
    console.log('checkEqual:', equalCheck)
    console.log('isLimitReached:', limitReached)
    console.log('Button disabled:', !formValid || equalCheck || limitReached)
  }
)

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

const removeFile = (index) => {
  const fileToRemove = files.value[index]
  if (fileToRemove.url) {
    URL.revokeObjectURL(fileToRemove.url)
  }
  files.value.splice(index, 1)
  fileContent.value.splice(index, 1)
  // myTasks.updateAttachments(todo.value.id, files.value)
}

const fetchAttachments = async () => {
  try {
    const response = await getAttachments(boardId.value, props.todoId)
    if (response.statusCode === 200) {
      todo.value.attachments = response.data

      for (let index = 0; index < todo.value.attachments.length; index++) {
        const element = todo.value.attachments[index]

        const byteCharacters = atob(element.fileData)
        const byteNumbers = new Array(byteCharacters.length)
          .fill(0)
          .map((_, i) => byteCharacters.charCodeAt(i))
        const byteArray = new Uint8Array(byteNumbers)
        const blob = new Blob([byteArray], { type: element.fileType })

        const file = new File([blob], element.fileName, {
          type: element.fileType
        })

        files.value.push(file)
      }
    } else {
      console.error('Failed to fetch attachments:', response)
    }
  } catch (error) {
    console.error('Error fetching attachments:', error)
  }
}

onMounted(() => {
  fetchAttachments()
})

// ----------------------- File Preview -----------------------
const previewFile = ref(null)
const isPreviewModalOpen = ref(false)

const openPreview = (file) => {
  previewFile.value = {
    name: file.name,
    url: URL.createObjectURL(file),
    type: file.type,
    size: file.size
  }
  isPreviewModalOpen.value = true
}

const closePreview = () => {
  if (previewFile.value?.url) {
    URL.revokeObjectURL(previewFile.value.url)
  }
  previewFile.value = null
  isPreviewModalOpen.value = false
}
</script>

<template>
  <!-- Edit Button -->
  <button
    @click="openModal"
    class="itbkk-button-edit btn rounded-full"
    :disabled="disabledBtn"
    :class="[
      'itbkk-button-edit ml-2',
      'btn',
      'rounded-full',
      { 'btn-disabled': disabledBtn }
    ]"
    :style="{
      backgroundColor: disabledBtn ? '#d3d3d3' : '#fae59d',
      color: disabledBtn ? '#a9a9a9' : 'white',
      borderRadius: '30px',
      position: 'static',
      cursor: disabledBtn ? 'not-allowed' : 'pointer',
      opacity: disabledBtn ? 0.6 : 1
    }"
  >
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="18"
      height="18"
      viewBox="0 0 24 24"
    >
      <g fill="none">
        <path
          d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
        />
        <path
          fill="currentColor"
          d="M16.035 3.015a3 3 0 0 1 4.099-.135l.144.135l.707.707a3 3 0 0 1 .135 4.098l-.135.144L9.773 19.177a1.5 1.5 0 0 1-.562.354l-.162.047l-4.454 1.028a1.001 1.001 0 0 1-1.22-1.088l.02-.113l1.027-4.455a1.5 1.5 0 0 1 .29-.598l.111-.125zm-.707 3.535l-8.99 8.99l-.636 2.758l2.758-.637l8.99-8.99l-2.122-2.12Zm3.536-2.121a1 1 0 0 0-1.32-.083l-.094.083l-.708.707l2.122 2.121l.707-.707a1 1 0 0 0 .083-1.32l-.083-.094z"
        />
      </g>
    </svg>
  </button>

  <!-- Modal window -->
  <dialog
    ref="myModal"
    class="itbkk-modal-task modal w-full h-full flex inset-0 z-30 items-center justify-center"
  >
    <div
      class="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-auto"
    >
      <WaitModal :is-loading="waitModal">
        <template #default>
          <p class="text-gray-700">
            We're working on uploading your file. <br />
            This won't take long!
          </p>
          <p class="text-sm text-gray-500">
            Thank you for your patience while we process your file.
          </p>
        </template>
      </WaitModal>
      <div class="p-6 space-y-6">
        <!-- Title and Status -->
        <div class="flex space-x-4">
          <!-- Title -->
          <div class="flex-1 space-y-1">
            <label class="block text-base font-medium text-[#9391e4]">
              Title <span class="text-red-500">*</span>
            </label>
            <input
              type="text"
              v-model="todo.title"
              placeholder="Title"
              class="itbkk-title w-full px-4 py-2 border border-gray-300 rounded-lg"
            />
            <p class="text-sm text-gray-500 text-right">
              {{ todo.title.length }}/100
            </p>
          </div>

          <!-- Status -->
          <div class="w-1/5 space-y-1">
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
          </div>
        </div>

        <!-- Description -->
        <div class="space-y-1">
          <label class="block text-base font-medium text-[#9391e4]">
            Description <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="todo.description"
            :class="{
              'italic text-gray-500':
                !todo.description || todo.description.trim() === ''
            }"
            placeholder="No Description Provided"
            class="itbkk-description w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg h-24"
          >
        {{ todo.description }}
      </textarea
          >
          <p class="text-sm text-gray-500 text-right mt-1">
            {{ descriptionLength }}/500
          </p>
        </div>

        <!-- Assignees -->
        <div>
          <label class="block text-base font-medium text-[#9391e4]">
            Assignees <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="todo.assignees"
            :class="{
              'italic text-gray-500':
                !todo.assignees || todo.assignees.trim() === ''
            }"
            placeholder="Unassigned"
            class="itbkk-assignees w-full px-4 py-2 border border-gray-300 rounded-lg"
          >
        {{ todo.assignees }}
      </textarea
          >
          <p class="text-sm text-gray-500 text-right mt-1">
            {{ assigneesLength }}/30
          </p>
        </div>

        <!-- File Upload Section -->
        <div>
          <label class="block text-base font-medium text-[#9391e4]">
            Attachments
          </label>
          <div v-if="files.length > 0">
            <div>
              <p
                v-if="messageResponse"
                class="whitespace-pre-wrap text-sm mb-2"
                :class="{
                  'text-green-500': messageResponseType === 'success',
                  'text-red-500': messageResponseType === 'error'
                }"
              >
                <span>{{ messageResponse }}</span>
              </p>

              <!-- Upload Section -->
              <div
                class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4"
              >
                <div v-if="files.length < maxFiles" class="relative">
                  <div
                    class="flex items-center justify-center border-2 border-dashed rounded-lg h-full cursor-pointer hover:bg-gray-100 transition ease-in-out duration-150"
                  >
                    <!-- Add file -->
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-8 w-8 text-gray-300"
                      viewBox="0 0 24 24"
                    >
                      <path
                        fill="currentColor"
                        d="M15 12.5h-2.5V15a.5.5 0 0 1-1 0v-2.5H9a.5.5 0 0 1 0-1h2.5V9a.5.5 0 0 1 1 0v2.5H15a.5.5 0 0 1 0 1"
                      />
                      <path
                        fill="currentColor"
                        d="M12 21.932A9.934 9.934 0 1 1 21.932 12A9.944 9.944 0 0 1 12 21.932m0-18.867A8.934 8.934 0 1 0 20.932 12A8.944 8.944 0 0 0 12 3.065"
                      />
                    </svg>
                    <input
                      id="file-upload"
                      type="file"
                      multiple
                      @change="handleFileChange"
                      class="absolute top-0 left-0 w-full h-full opacity-0 cursor-pointer"
                    />
                  </div>
                </div>
                <div
                  v-for="(file, index) in files"
                  :key="index"
                  class="flex flex-col items-start bg-gray-100 hover:bg-gray-200 rounded-lg p-2 relative"
                >
                  <div
                    class="w-full h-20 bg-gray-300 rounded mb-1 relative flex items-center justify-center"
                  >
                    <!-- File Icon or Preview -->
                    <div
                      class="w-full h-20 bg-gray-100 rounded overflow-hidden flex items-center justify-center"
                    >
                      <Iconfile
                        :file="file"
                        :fileContent="fileContent[index]"
                        @click="openPreview(file)"
                      />
                    </div>

                    <button
                      @click="removeFile(index)"
                      class="absolute top-1 right-1 flex items-center justify-center w-5 h-5 rounded-full bg-red-100 hover:bg-red-200"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4 text-red-400 hover:text-red-500"
                        viewBox="0 0 24 24"
                      >
                        <path
                          fill="currentColor"
                          d="M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10s10-4.47 10-10S17.53 2 12 2m4.3 14.3a.996.996 0 0 1-1.41 0L12 13.41L9.11 16.3a.996.996 0 1 1-1.41-1.41L10.59 12L7.7 9.11A.996.996 0 1 1 9.11 7.7L12 10.59l2.89-2.89a.996.996 0 1 1 1.41 1.41L13.41 12l2.89 2.89c.38.38.38 1.02 0 1.41"
                        />
                      </svg>
                    </button>
                  </div>
                  <p
                    @click="openPreview(file)"
                    class="text-xs text-gray-600 truncate w-full overflow-hidden"
                  >
                    {{ file.name }}
                  </p>
                  <p
                    @click="openPreview(file)"
                    class="text-xs text-gray-600 truncate"
                  >
                    {{ (file.size / (1024 * 1024)).toFixed(2) }} MB
                  </p>
                </div>
              </div>
            </div>
            <PreviewFile
              v-if="isPreviewModalOpen"
              :file="previewFile"
              @close="closePreview"
            />
          </div>
          <!-- No have File -->
          <div v-else>
            <div>
              <div class="grid grid-cols-1 space-y-4">
                <div class="flex items-center justify-center w-full">
                  <!-- ใช้ for เชื่อมกับ input -->
                  <div class="relative w-full h-60">
                    <!-- ปุ่มอัปโหลด -->
                    <label
                      for="file-upload"
                      class="flex flex-col items-center rounded-lg border-2 border-dashed w-full h-full p-6 group text-center cursor-pointer transition duration-300 ease-in-out"
                    >
                      <div
                        class="h-full w-full text-center flex flex-col justify-center items-center"
                      >
                        <div class="flex flex-auto max-h-40 w-1/3 mx-auto">
                          <img
                            class="has-mask object-contain"
                            src="https://img.freepik.com/free-vector/image-upload-concept-landing-page_52683-27130.jpg?size=338&ext=jpg"
                            alt="upload illustration"
                          />
                        </div>
                        <p class="pointer-none text-gray-500">
                          <span class="text-sm">Drag and drop</span> files here
                          <br />
                          or
                          <span class="text-blue-500 underline cursor-pointer">
                            select a file
                          </span>
                          from your computer
                        </p>
                      </div>
                    </label>

                    <input
                      id="file-upload"
                      type="file"
                      multiple
                      @change="handleFileChange"
                      class="absolute top-0 left-0 w-full h-full opacity-0 cursor-pointer"
                    />
                  </div>
                </div>
              </div>

              <p class="text-sm text-gray-300 p-2">
                <span
                  >Supported formats: png, jpeg, txt, rtf, pdf (up to 10
                  files)</span
                >
              </p>
            </div>
          </div>
        </div>

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

        <div
          role="alert"
          class="alert shadow-lg alert-error"
          v-show="alertFailToEdit"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            animation: fadeInOut 1.5s infinite;
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ aletMessage }}</span>
        </div>

        <div
          role="alert"
          class="alert shadow-lg"
          :class="{ hidden: !showAlertAfterEdit }"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            color: rgb(74 222 128 / var(--tw-text-opacity));
            animation: fadeInOut 1.5s infinite;
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <div>
            <h2 class="itbkk-message font-bold text-green-400">
              The task has been successfully edited
            </h2>
          </div>
        </div>

        <!-- Save & Cancel Button -->
        <div class="px-6 py-4 flex justify-end border-t border-gray-200">
          <button
            @click="closeModal"
            class="px-4 py-2 btn text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none"
          >
            Cancel
          </button>
          <button
            @click="UpdateTask"
            :disabled="!isFormValid || checkEqual || isLimitReached"
            :class="{
              'opacity-50 cursor-not-allowed':
                !isFormValid || checkEqual || isLimitReached
            }"
            class="ml-3 px-5 py-2 btn text-white bg-[#f785b1] rounded-lg hover:bg-[#fa619c] focus:outline-none"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </dialog>
</template>

<style>
.fixed-alert {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50%;
  max-width: 600px;
  padding: 15px;
  text-align: center;
  border-radius: 30px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  animation: fadeInOut 4s infinite;
}
</style>
