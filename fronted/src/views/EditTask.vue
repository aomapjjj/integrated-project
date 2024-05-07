<script setup>
// Import ref from Vue
import { ref, watch, computed } from "vue"
import { getItems, getItemById, editItem } from "@/libs/fetchUtils"
import { checkStatus } from "../libs/checkStatus"
import { toDate } from "../libs/toDate"
import router from "@/router"

const props = defineProps({
  todoId: Number
})

const todo = ref({
  id: "",
  title: "",
  description: "",
  assignees: "",
  status: "",
  createdOn: "",
  updatedOn: ""
})

const todoList = ref([])
const oldValue = ref({})

watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue)
    if (response.status === 200) {
      todo.value = await response.json()
      oldValue.value = { ...todo.value }
      console.log(oldValue.value)
    }
  },
  { immediate: true }
)

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

const myModal = ref(null)

// Function to open the modal
const openModal = () => {
  myModal.value.showModal()
  console.log([props.todoId])
}

// Function to close the modal
const closeModal = () => {
  myModal.value.close()
}

const UpdateTask = async () => {
  const trimmedTitle = todo.value.title.trim()
  const trimmedDescription = todo.value.description.trim()
  const trimmedAssignees = todo.value.assignees.trim()

  const edit = await editItem(import.meta.env.VITE_BASE_URL, props.todoId, {
    title: trimmedTitle,
    description: trimmedDescription,
    assignees: trimmedAssignees,
    status: todo.value.status
  })
  console.log(edit)
}

router.go()

const checkEqual = computed(() => {
  console.log(JSON.stringify(todo.value))
  console.log(JSON.stringify(oldValue.value))
  return JSON.stringify(todo.value) === JSON.stringify(oldValue.value)
})
</script>

<template>
  <!-- BUTTON -->
  <button
    @click="openModal"
    class="itbkk-button-edit btn btn-circle btn-outline btn-sm"
    style="color: currentColor"
  >
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="1em"
      height="1em"
      viewBox="0 0 24 24"
      style="color: #f785b1"
    >
      <g
        fill="none"
        stroke="currentColor"
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
      >
        <path stroke-dasharray="20" stroke-dashoffset="20" d="M3 21H21">
          <animate
            fill="freeze"
            attributeName="stroke-dashoffset"
            dur="0.3s"
            values="20;0"
          ></animate>
        </path>
        <path
          stroke-dasharray="44"
          stroke-dashoffset="44"
          d="M7 17V13L17 3L21 7L11 17H7"
        >
          <animate
            fill="freeze"
            attributeName="stroke-dashoffset"
            begin="0.4s"
            dur="0.6s"
            values="44;0"
          ></animate>
        </path>
        <path stroke-dasharray="8" stroke-dashoffset="8" d="M14 6L18 10">
          <animate
            fill="freeze"
            attributeName="stroke-dashoffset"
            begin="1s"
            dur="0.2s"
            values="8;0"
          ></animate>
        </path>
      </g>
    </svg>
  </button>
  <!-- Modal window -->

  <dialog ref="myModal" class="w-full h-full">
    <div
      class="modal-container bg-white w-full h-full top-0 left-0 flex items-center justify-center"
    >
      <div class="modal-content py-4 text-left px-6 flex-grow">
        <!-- Title -->
        <label
          class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mb-8"
          style="background-color: #9fc3e9"
        >
          <input
            type="text"
            class="grow"
            v-model="todo.title"
            placeholder="Enter Your Title"
            maxlength="100"
          />
        </label>
        <!-- Description -->
        <label for="description" class="form-control flex-grow ml-4 mb-8">
          <div class="label">
            <span
              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
              style="color: #9391e4"
              >Description</span
            >
          </div>
          <textarea
            id="description"
            class="itbkk-description textarea textarea-bordered h-3/4 mb-8"
            maxlength="500"
            rows="4"
            v-model="todo.description"
            :class="{
              'italic text-gray-500':
                !todo.description || todo.description.trim() === ''
            }"
            placeholder="No Description Provided"
            style="height: 400px"
            >{{ todo.description }} {{ todoId }}</textarea
          >
        </label>
      </div>
      <div
        class="modal-content py-4 text-left px-10 flex-grow w-2/3 max-w-2xl"
        style="margin-top: 65px"
      >
        <!-- Assignees -->
        <div class="mt-10">
          <span
            class="block text-lg font-bold leading-6 text-gray-900 mb-2"
            style="color: #9391e4"
            >Assignees</span
          >
          <textarea
            id="assignees"
            class="itbkk-assignees textarea textarea-bordered w-full mt-1"
            maxlength="30"
            rows="4"
            v-model="todo.assignees"
            :class="{
              'italic text-gray-500':
                !todo.assignees || todo.assignees.trim() === ''
            }"
            placeholder="Unassigned"
            >{{ todo.assignees }}</textarea
          >
        </div>
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
            <option value="NO_STATUS">No Status</option>
            <option
              v-for="status in ['TO_DO', 'DOING', 'DONE']"
              :value="status"
            >
              {{ checkStatus(status) }}
            </option>
          </select>
        </div>
        <!-- TimeZone -->
        <div class="itbkk-timezone">
          <div class="mb-4 flex items-center">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >TimeZone :
            </label>
            <h1>{{ TimeZone }}</h1>
          </div>
          <!-- CreatedOn -->
          <div class="mb-4 flex items-center itbkk-created-on">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >Created On :
            </label>
            <h1>{{ toDate(todo.createdOn) }}</h1>
          </div>
          <!-- UpdatedOn -->
          <div class="mb-4 flex items-center itbkk-updated-on">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >Updated On :
            </label>
            <h1>{{ toDate(todo.updatedOn) }}</h1>
          </div>
        </div>
        <!-- Close Button -->
        <div
          class="modal-action"
          style="display: flex; justify-content: space-around"
        >
          <form method="dialog" style="flex: 1">
            <button
              @click="UpdateTask"
              type="submit"
              class="btn"
              style="background-color: #f785b1; margin: 10px; width: 100%"
              :disabled="
                todo.title.length === 0 ||
                todo.title === null ||
                checkEqual === true
              "
            >
              Save
            </button>
          </form>
          <button class="btn" style="flex: 1; margin: 10px" @click="closeModal">
            Close
          </button>
        </div>
      </div>
    </div>
  </dialog>
</template>
<style></style>
