<script setup>
import { ref, watch, computed } from 'vue'
import { getItems, getItemById, editItem, getAttachments } from '@/libs/fetchUtils';
import { useTasks } from '../../stores/store'
import { toDate } from '../../libs/toDate'
import { useRoute, useRouter } from 'vue-router'
import { useLimitStore } from '../../stores/storeLimit'

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

// ----------------------- Params -----------------------

const todo = ref({
  id: '',
  title: '',
  description: '',
  assignees: '',
  status: '',
  createdOn: '',
  updatedOn: '',
  attachments: [],
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

    const { statusCode, data } = await getAttachments(boardId.value, newValue);
    if (statusCode === 200 && Array.isArray(data)) {
      todo.value.attachments = data;
    } else {
      console.error('Failed to fetch attachments:', statusCode);
      todo.value.attachments = [];
    }

    const itemsStatus = await getItems(baseUrlStatus)
    statusList.value = itemsStatus
  },
  { immediate: true }
)

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

const myModal = ref(null)

const openModal = () => {
  router.push({ name: 'TaskEdit', params: { taskid: props.todoId } })
  myModal.value.showModal()
}

const closeModal = () => {
  myModal.value.close()
  router.push({ name: 'TaskList', params: { id: boardId.value } })
}

const UpdateTask = async () => {
  console.log('Todo ID:', props.todoId)

  if (isLimitReached.value) {
    return
  }

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
      edit.updateOn
    )

    showAlertEdit.value = true
    showAlertAfterEdit.value = true
    setTimeout(() => {
      showAlertAfterEdit.value = false
    }, 2300)
  } catch (error) {
    console.error('Error updating task:', error)
  }
}

const checkEqual = computed(() => {
  const trimmedTodo = {
    ...todo.value,
    title: todo.value.title?.trim(),
    description: todo.value.description?.trim(),
    assignees: todo.value.assignees?.trim()
  }
  const trimmedOldValue = {
    ...oldValue.value,
    title: oldValue.value.title?.trim(),
    description: oldValue.value.description?.trim(),
    assignees: oldValue.value.assignees?.trim()
  }
  return JSON.stringify(trimmedTodo) === JSON.stringify(trimmedOldValue)
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
</script>

<template>
  <!-- Edit Button -->
  <button @click="openModal" class="itbkk-button-edit btn rounded-full" :disabled="disabledBtn" :class="[
    'itbkk-button-edit ml-2',
    'btn',
    'rounded-full',
    { 'btn-disabled': disabledBtn }
  ]" :style="{
    backgroundColor: disabledBtn ? '#d3d3d3' : '#fae59d',
    color: disabledBtn ? '#a9a9a9' : 'white',
    borderRadius: '30px',
    position: 'static',
    cursor: disabledBtn ? 'not-allowed' : 'pointer',
    opacity: disabledBtn ? 0.6 : 1
  }">
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
      <g fill="none">
        <path
          d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z" />
        <path fill="currentColor"
          d="M16.035 3.015a3 3 0 0 1 4.099-.135l.144.135l.707.707a3 3 0 0 1 .135 4.098l-.135.144L9.773 19.177a1.5 1.5 0 0 1-.562.354l-.162.047l-4.454 1.028a1.001 1.001 0 0 1-1.22-1.088l.02-.113l1.027-4.455a1.5 1.5 0 0 1 .29-.598l.111-.125zm-.707 3.535l-8.99 8.99l-.636 2.758l2.758-.637l8.99-8.99l-2.122-2.12Zm3.536-2.121a1 1 0 0 0-1.32-.083l-.094.083l-.708.707l2.122 2.121l.707-.707a1 1 0 0 0 .083-1.32l-.083-.094z" />
      </g>
    </svg>
  </button>
  <!-- Modal window -->
  <dialog ref="myModal" class="itbkk-modal-task modal fixed w-full h-full flex">
    <div class="modal-container bg-white w-full xl:w-3/4 h-fit mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex">
      <div class="modal-content py-4 text-left px-6 flex-grow">
        <!-- Title -->
        <div class="relative">
          <label class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mt-1"
            style="background-color: #9fc3e9">
            <input type="text" class="grow" v-model="todo.title" placeholder="Enter Your Title" />
          </label>
          <p class="text-sm text-gray-400 mb-2 mt-2 ml-4" style="text-align: right">
            {{ todo.title.length }}/100
          </p>
        </div>
        <!-- Description -->
        <div class="relative">
          <label for="description" class="form-control flex-grow ml-4 mb-8">
            <div class="label">
              <span class="block text-lg font-bold leading-6 text-gray-900 mb-1" style="color: #9391e4">Description
              </span>
            </div>
            <textarea id="description" class="itbkk-description textarea textarea-bordered h-3/4" rows="4"
              v-model="todo.description" :class="{
                'italic text-gray-500':
                  !todo.description || todo.description.trim() === ''
              }" placeholder="No Description Provided" style="height: 400px">{{ todo.description }}</textarea>
            <p class="text-sm text-gray-400 mb-2 mt-2" style="text-align: right">
              {{ descriptionLength }}/500
            </p>
          </label>
        </div>
        <div class="attachments-section border-t border-gray-300 pt-4 mt-6">
          <h2 class="text-lg font-bold mb-2" style="color: #9391e4">Attachments</h2>
          <div v-if="todo.attachments.length > 0">
            <ul>
              <li v-for="(attachment, index) in todo.attachments" :key="index" class="flex items-center mb-2">
                <a :href="attachment.fileUrl" target="_blank" class="text-blue-500 underline flex-grow">
                  {{ attachment.fileName }}
                </a>
                <button @click="removeAttachment(index)" class="ml-2 text-red-500 hover:text-red-700 focus:outline-none"
                  title="Remove">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                    viewBox="0 0 16 16">
                    <path
                      d="M4.646 4.646a.5.5 0 011 0L8 6.707l2.354-2.061a.5.5 0 01.707.707L8.707 7.414l2.061 2.353a.5.5 0 01-.707.707L8 8.707l-2.354 2.354a.5.5 0 01-.707-.707L7.293 7.414 5.232 5.061a.5.5 0 010-.707z" />
                  </svg>
                </button>
              </li>
            </ul>
          </div>
          <div v-else class="italic text-gray-500">No attachments available</div>
        </div>
      </div>

      <div class="modal-content py-4 text-left px-10" style="margin-top: 65px">
        <!-- Assignees -->
        <div class="mt-10">
          <span class="block text-lg font-bold leading-6 text-gray-900" style="color: #9391e4">Assignees</span>
          <textarea id="assignees" class="itbkk-assignees textarea textarea-bordered w-full mt-1" rows="4"
            v-model="todo.assignees" :class="{
              'italic text-gray-500':
                !todo.assignees || todo.assignees.trim() === ''
            }" placeholder="Unassigned">{{ todo.assignees }}
            </textarea>
          <p class="text-sm text-gray-400 mb-2 mt-2" style="text-align: right">
            {{ assigneesLength }}/30
          </p>
        </div>
        <!-- Status -->
        <div class="mb-4 mt-2">
          <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Status</span>
          <select class="select select-bordered w-full max-w-xs mt-1" v-model="todo.status">
            <option class="itbkk-status " v-for="status in statusList" :value="status.name">
              {{ status.name }}
            </option>
          </select>
        </div>
        <!-- TimeZone -->
        <div class="itbkk-timezone">
          <div class="mb-4 flex items-center">
            <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">TimeZone :
            </label>
            <h1>{{ TimeZone }}</h1>
          </div>
          <!-- CreatedOn -->
          <div class="mb-4 flex items-center itbkk-created-on">
            <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">Created On :
            </label>
            <h1>{{ toDate(todo.createdOn) }}</h1>
          </div>
          <!-- UpdatedOn -->
          <div class="mb-4 flex items-center itbkk-updated-on">
            <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">Updated On :
            </label>
            <h1>{{ toDate(todo.updatedOn) }}</h1>
          </div>
        </div>

        <div role="alert" class="alert shadow-lg alert-error" v-show="alertFailToEdit" style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            animation: fadeInOut 1.5s infinite;
          ">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none"
            viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{{ aletMessage }}</span>
        </div>

        <div role="alert" class="alert shadow-lg" :class="{ hidden: !showAlertAfterEdit }" style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            color: rgb(74 222 128 / var(--tw-text-opacity));
            animation: fadeInOut 1.5s infinite;
          ">
          <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none"
            viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div>
            <h2 class="itbkk-message font-bold text-green-400">
              The task has been successfully edited
            </h2>
          </div>
        </div>

        <!-- Save & Close Button -->
        <div class="modal-action flex justify-between ml-20">
          <div style="
              display: flex;
              justify-content: flex-end;
              margin-left: 10px;
              flex: 1;
            ">
            <button @click="UpdateTask" type="submit" class="btn" style="background-color: #f785b1"
              :disabled="!isFormValid || checkEqual || isLimitReached" :class="{
                disabled: !isFormValid || checkEqual || isLimitReached
              }">
              Save
            </button>
            <button class="btn ml-2" @click="closeModal">Close</button>
          </div>
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