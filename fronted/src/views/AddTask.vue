<script setup>
import { getItems, addItem } from "../libs/fetchUtils.js"
import { ref, onMounted, computed } from "vue"
import { useRouter } from "vue-router"
import { useTasks } from "../stores/store.js"
import { useLimitStore } from "../stores/storeLimit"

const router = useRouter()
const alertAdd = ref(false)
const statusList = ref([])
const alertLimitAdd = ref(false)
const error = ref("")

const baseUrlTask = `${import.meta.env.VITE_BASE_URL_MAIN}/tasks`
const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`

const limitStore = useLimitStore()
const taskStore = useTasks()

const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: "No Status"
})

onMounted(async () => {
  const itemsStatus = await getItems(baseUrlStatus)
  statusList.value = itemsStatus
})

const submitForm = async () => {
  const trimmedTitle = todo.value.title?.trim()
  const trimmedDescription = todo.value.description?.trim()
  const trimmedAssignees = todo.value.assignees?.trim()
  
  try {
    const itemAdd = await addItem(baseUrlTask, {
      title: trimmedTitle,
      description: trimmedDescription,
      assignees: trimmedAssignees,
      status: todo.value.status
    })

    taskStore.addTask(
      itemAdd.id,
      itemAdd.title,
      itemAdd.description,
      itemAdd.assignees,
      itemAdd.status,
      itemAdd.createdOn,
      itemAdd.updateOn
    )

    alertAdd.value = true
    setTimeout(() => {
      alertAdd.value = false
    }, 2300)
  } catch (error) {
    console.error("Error adding task:", error)
  }
}

const closeModal = () => {
  my_modal_1.close()
  router.go(-1)
  clearForm()
}

const clearForm = () => {
  todo.value.title = ""
  todo.value.description = ""
  todo.value.assignees = ""
  todo.value.status = "No Status"
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


const isLimitReached = computed(() => {
  const status = todo.value.status;
  if (status === "No Status" || status === "Done") {
    return false;
  }

  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = taskStore.getTasks()
      .filter((task) => task.status === status);
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      setTimeout(() => {
        alertLimitAdd.value = false;
      }, 1800);
      alertLimitAdd.value = true;
       return (error.value = `The status "${todo.value.status}" will have too many tasks. Please make progress and update status of existing tasks first.`);
    }
  }

  return false;
});

</script>

<template>
  <!-- ADD -->
  <RouterLink :to="{ name: 'AddTask' }">
    <button
      onclick="my_modal_1.showModal()"
      class="itbkk-button-add btn ml-4"
      style="
        position: relative;
        border-radius: 30px;
        background-color: #9391e4;
        color: white;
      "
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
    <dialog id="my_modal_1" class="modal fixed min-h-full max-h-fit flex">
      <div
        class="modal-container bg-white xl:w-3/4 mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex"
      >
        <div
          form
          @submit.prevent="submitForm"
          class="flex justify-between w-full h-full"
          style="align-items: center"
        >
          <!-- Title -->
          <div class="modal-content py-4 text-left px-6 flex-grow">
            <span
              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
              style="color: #9391e4; margin: 15px"
              >Title<span style="color: red"> *</span>
            </span>
            <label
              class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4"
            >
              <input
                type="text"
                class="grow"
                placeholder="Enter Your Title"
                v-model="todo.title"
              />
            </label>
            <p
              class="text-sm text-gray-400 ml-4 mb-2 mt-2"
              style="text-align: right"
            >
              {{ todo.title?.length }}/100
            </p>
            <!-- Description -->
            <label for="description" class="form-control flex-grow ml-4">
              <div class="label">
                <span
                  class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                  style="color: #9391e4"
                  >Description</span
                >
              </div>
              <textarea
                id="description"
                class="itbkk-description textarea textarea-bordered h-3/4"
                rows="4"
                placeholder="No Description Provided"
                style="height: 200px"
                v-model="todo.description"
              ></textarea>
            </label>
            <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ todo.description?.length }}/500
            </p>
          </div>
          <div class="modal-content py-4 text-left px-10 mb-2">
            <!-- Assignees -->
            <span
              class="block text-lg font-bold leading-6 text-gray-900"
              style="color: #9391e4"
              >Assignees</span
            >
            <textarea
              id="assignees"
              class="itbkk-assignees textarea textarea-bordered w-full mt-1"
              rows="4"
              placeholder="Unassigned"
              v-model="todo.assignees"
            ></textarea>
            <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ todo.assignees?.length }}/30
            </p>
            <!-- Status -->
            <div class="itbkk-status mb-4 mt-2">
              <span
                class="block text-lg font-bold leading-6 text-gray-900 mb-2"
                style="color: #9391e4"
                >Status</span
              >
              <select
                class="select select-bordered w-full max-w-xs mt-1"
                v-model="todo.status"
              >
                <option v-for="status in statusList" :value="status.name">
                  {{ status.name }}
                </option>
              </select>
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
              <span>{{ error }}</span>
            </div>

            <!-- Cancel & Save Button -->
            <div class="modal-action">
              <form method="dialog">
                <button
                  type="submit"
                  class="itbkk-button-confirm btn disabled:{{ todo.title?.length === 0 || todo.title === null }}"
                  style="background-color: #f785b1"
                  :class="{ disabled: !isFormValid || isLimitReached }"
                  :disabled="!isFormValid || isLimitReached"
                >
                  Save
                </button>
              </form>
              <button class="itbkk-button-cancel btn" @click="closeModal">
                Cancel
              </button>
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
