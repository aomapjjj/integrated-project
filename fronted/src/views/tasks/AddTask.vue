<script setup>
import { getItems, addItem, addAttachments } from '../../libs/fetchUtils.js'
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTasks } from '../../stores/store.js'
import { useLimitStore } from '../../stores/storeLimit.js'

// ----------------------- Router -----------------------

const router = useRouter()
const route = useRoute()

// ----------------------- Alerts -----------------------

const alertAdd = ref(false)
const statusList = ref([])
const alertLimitAdd = ref(false)
const errorMessageLimit = ref('')

// ----------------------- Enable & Disable -----------------------

const props = defineProps({
  disabledBtn: Boolean
})

// ----------------------- Params -----------------------

const boardId = ref()

// ----------------------- object -----------------------

const files = ref([])
const maxFiles = 10
const maxTotalSizePerFile = 20 * 1024 * 1024 // 20 MB

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)

const todo = ref({
  title: '',
  description: '',
  assignees: '',
  status: 'No Status'
})

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlTask = `${baseUrlboards}/${boardId.value}/tasks`
const baseUrlStatus = `${baseUrlboards}/${boardId.value}/statuses`

// ----------------------- Stores -----------------------

const limitStore = useLimitStore()
const taskStore = useTasks()

onMounted(async () => {
  const itemsStatus = await getItems(baseUrlStatus)
  statusList.value = itemsStatus
})

const handleFileChange = (event) => {
  const selectedFiles = Array.from(event.target.files)

  if (files.value.length + selectedFiles.length > maxFiles) {
    alert(`You can upload up to ${maxFiles} files.`)
    return
  }

  const newFiles = []
  for (const file of selectedFiles) {
    if (file.size > maxTotalSizePerFile) {
      alert(`File ${file.name} exceeds the maximum size of 20 MB.`)
      continue
    }
    newFiles.push(file)
  }

  files.value = [...files.value, ...newFiles]
}

watch(
  () => files.value,
  (newFiles) => {
    files.value = newFiles
    console.log('Updated files:', newFiles)
  }
)

const isImage = (file) => {
  return file.type.startsWith('image/')
}

const getFileIcon = (file) => {
  if (!file || typeof file !== 'object' || !file.name) {
    return '/image/files/default.png'
  }

  const extension = file.name.split('.').pop().toLowerCase()
  if (!extension) return '/image/files/default.png'

  // ตรวจสอบนามสกุลของไฟล์เพื่อเลือกไอคอนที่เหมาะสม
  switch (extension) {
    case 'pdf':
      return '/image/files/PDF.png'
    case 'doc':
    case 'docx':
      return '/image/files/DOC.png'
    case 'xls':
    case 'xlsx':
      return '/image/files/XLS.png'
    case 'ppt':
    case 'pptx':
      return '/image/files/PPT.png'
    case 'txt':
      return '/image/files/TXT.png'
    case 'png':
    case 'jpeg':
    case 'jpg':
    case 'gif':
      return file instanceof File
        ? URL.createObjectURL(file)
        : '/image/files/default.png'
    default:
      return '/image/files/default.png'
  }
}

const clearFileUrls = () => {
  files.value.forEach((file) => {
    if (file instanceof File && file.url) {
      URL.revokeObjectURL(file.url)
    }
  })
}

const submitForm = async () => {
  const trimmedTitle = todo.value.title?.trim()
  const trimmedDescription = todo.value.description?.trim()
  const trimmedAssignees = todo.value.assignees?.trim()

  try {
    // เพิ่ม Task ไปยัง backend
    const itemAdd = await addItem(baseUrlTask, {
      title: trimmedTitle,
      description: trimmedDescription,
      assignees: trimmedAssignees,
      status: todo.value.status
    })

    // อัปโหลดไฟล์แนบ
    let attachments = []
    const attachmentsResponse = await addAttachments(
      boardId.value,
      itemAdd.id,
      files.value
    )

    if (
      attachmentsResponse.statusCode === 200 ||
      attachmentsResponse.statusCode === 201
    ) {
      console.log('File added successfully:', attachmentsResponse.data)

      if (
        attachmentsResponse.data &&
        Array.isArray(attachmentsResponse.data.attachments)
      ) {
        attachments = attachmentsResponse.data.attachments
      } else {
        console.error(
          'attachmentsResponse.data.attachments is not an array:',
          attachmentsResponse.data
        )
      }
    } else {
      console.error('Failed to add file:', attachmentsResponse)
    }

    taskStore.addTask(
      itemAdd.id,
      itemAdd.title,
      itemAdd.description,
      itemAdd.assignees,
      itemAdd.status,
      itemAdd.createdOn,
      itemAdd.updateOn,
      attachments
    )

    console.log(taskStore.getTasks())

    alertAdd.value = true
    setTimeout(() => {
      alertAdd.value = false
    }, 2300)
    closeModal()
  } catch (error) {
    console.error('Error adding task:', error)
  }
}

const closeModal = () => {
  my_modal_1.close()
  router.go(-1)
  clearForm()
}

const clearForm = () => {
  todo.value.title = ''
  todo.value.description = ''
  todo.value.assignees = ''
  todo.value.status = 'No Status'
  files.value = []
}

// ----------------------- Validate -----------------------

const isValidTitle = (title) => {
  return title && title?.trim().length > 0 && title?.trim().length <= 100
}

const isFormValid = computed(() => {
  return (
    isValidTitle(todo.value.title) &&
    todo.value.description.trim().length <= 500 &&
    todo.value.assignees.trim().length <= 30
  )
})

// ----------------------- Limit -----------------------

const isLimitReached = computed(() => {
  const status = todo.value.status
  if (status === 'No Status' || status === 'Done') {
    return false
  }

  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = taskStore
      .getTasks()
      .filter((task) => task.status === status)
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      setTimeout(() => {
        alertLimitAdd.value = false
      }, 1800)
      alertLimitAdd.value = true
      return (errorMessageLimit.value = `The status "${todo.value.status}" will have too many tasks. Please make progress and update status of existing tasks first.`)
    }
  }

  return false
})
</script>

<template>
  <!-- ADD -->
  <RouterLink :to="{ name: 'AddTask' }">
    <button
      :disabled="disabledBtn"
      :class="['itbkk-button-add', 'btn', { 'btn-disabled': disabledBtn }]"
      :style="{
        backgroundColor: disabledBtn ? '#d3d3d3' : '#9391e4',
        color: disabledBtn ? '#a9a9a9' : 'white',
        borderRadius: '30px',
        position: 'relative',
        cursor: disabledBtn ? 'not-allowed' : 'pointer',
        opacity: disabledBtn ? 0.6 : 1
      }"
      onclick="my_modal_1.showModal()"
      class="itbkk-button-add btn ml-4"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
      >
        <path
          fill="currentColor"
          d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z"
        />
      </svg>
      Add new task
    </button>
  </RouterLink>

  <div class="itbkk-modal-task">
    <dialog
      id="my_modal_1"
      class="itbkk-modal-task modal fixed w-full h-full flex inset-0 z-50 items-center justify-center"
    >
      <div
        class="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] overflow-auto"
      >
        <div form @submit.prevent="submitForm">
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
                Description
              </label>
              <textarea
                id="description"
                v-model="todo.description"
                rows="4"
                placeholder="No Description Provided"
                class="itbkk-description w-full mt-1 px-4 py-2 border border-gray-300 rounded-lg h-24"
              >
 {{ todo.description }}</textarea
              >
              <p class="text-sm text-gray-500 text-right mt-1">
                {{ todo.description?.length }}/500
              </p>
            </div>

            <!-- Assignees -->
            <div>
              <label class="block text-base font-medium text-[#9391e4]">
                Assignees 
              </label>
              <textarea
                id="assignees"
                v-model="todo.assignees"
                rows="4"
                placeholder="Unassigned"
                class="itbkk-assignees w-full px-4 py-2 border border-gray-300 rounded-lg"
              >
          {{ todo.assignees }}
          </textarea
              >
              <p class="text-sm text-gray-500 text-right mt-1">
                {{ todo.assignees?.length }}/30
              </p>
            </div>

            <div
              role="alert"
              v-show="alertLimitAdd"
              class="flex flex-col fixed-alert alert"
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
              <span class="text-red-500">Error! Tasks cannot be added</span>
              <span>{{ errorMessageLimit }}</span>
            </div>

            <!-- Cancel & Save Button -->
            <div
              class="modal-action px-6 py-4 flex justify-end border-t border-gray-200"
            >
              <button
                class="itbkk-button-cancel text-sm px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none"
                @click="closeModal"
              >
                Cancel
              </button>
              <form method="dialog">
                <button
                  type="submit"
                  class="itbkk-button-confirm text-sm ml-3 px-4 py-2 text-white bg-[#f785b1] rounded-lg hover:bg-[#fa619c] focus:outline-none disabled:opacity-50"
                  :class="{
                    'opacity-50 cursor-not-allowed':
                      !isFormValid || isLimitReached
                  }"
                  :disabled="!isFormValid || isLimitReached"
                >
                  Save
                </button>
              </form>
            </div>
          </div>
        </div>

        <!-- ALERT -->
        <div
          role="alert"
          class="alert shadow-lg"
          :class="{ hidden: !alertAdd }"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 400px;
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
          <span class="font-bold text-green-400"
            >The task has been successfully added</span
          >
        </div>
      </div>
    </dialog>
  </div>
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
