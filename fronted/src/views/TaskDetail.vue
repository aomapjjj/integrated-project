<script setup>
import { getItems, getItemById } from "@/libs/fetchUtils"
import { ref, watch, onMounted } from "vue"
const props = defineProps({
  todoId: Number
})
// Create a reactive variable to store the prop value
const todo = ref({
  id: "",
  title: "",
  description: "",
  assignees: "",
  status: "",
  createdOn: ""
})

const todoList = ref([])

onMounted(async () => {
  console.log(import.meta.env.VITE_BASE_URL)
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  console.log(items)
  todoList.value = items
  console.log(todoList.value)
  console.log(items)
})

// Watch for changes in the prop value
watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue)
    todo.value = response
  }
)





const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

function date(date) {}
</script>

<template>
  <input type="checkbox" id="my_modal_6" class="modal-toggle hidden" />
  <div
    class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center"
  >
    <div
      class="modal-overlay absolute w-full h-full bg-gray-900 opacity-50"
    ></div>

    <div
      class="modal-container bg-white w-full md:w-5/6 lg:w-2/3 xl:w-1/2 mx-auto rounded shadow-lg z-50 overflow-y-auto"
    >
      <div class="modal-content py-4 text-left px-6">
        <!-- Title -->
        <h3 class="font-bold text-lg mb-4">{{ todo.title }}</h3>

        <!-- Description -->
        <div class="mb-4">
          <label for="description" class="label">Description</label>
          <textarea
            id="description"
            class="textarea textarea-bordered w-full"
            rows="4"
            :class="{
              'italic text-gray-500':
                todo.description.length === 0 ||
                todo.description.trim() === '' ||
                todo.description === null
            }"
            placeholder="No description provided"
            >{{ todo.description }}</textarea
          >
        </div>

        <!-- Assignees -->
        <div class="mb-4">
          <label for="assignees" class="label">Assignees</label>
          <textarea
            id="assignees"
            class="textarea textarea-bordered w-full"
            rows="4"
            :class="{
              'italic text-gray-500':
                todo.assignees.length === 0 ||
                todo.assignees.trim() === '' ||
                todo.assignees === null
            }"
            placeholder="Unassigned"
            >{{ todo.assignees }}</textarea
          >
        </div>

        <!-- Status Dropdown -->
        <div class="mb-4">
          <label for="status" class="label">Status</label>
          <div class="dropdown">
            <div tabindex="0" role="button" class="btn m-1">
              {{ todo.status }}
            </div>
            <ul
              tabindex="0"
              class="dropdown-content z-10 menu p-2 shadow bg-base-100 rounded-box w-52"
            >
              <li v-for="statusItem in todoList" >
                <a>{{ statusItem.status }}</a>
              </li>
            </ul>
          </div>
        </div>
        <!-- TimeZone -->
        <div class="mb-4 flex items-center">
          <label for="timezone" class="label mr-2">TimeZone : </label>
          <h1>{{ TimeZone }}</h1>
        </div>

        <!-- CreatedOn -->
        <div class="mb-4 flex items-center">
          <label for="timezone" class="label mr-2">Created On : </label>
          <h1>{{ todo.createdOn }}</h1>
        </div>

        <!-- UpdatedOn -->
        <div class="mb-4 flex items-center">
          <label for="timezone" class="label mr-2">Updated On : </label>
          <h1>{{ todo.updateOn }}</h1>
        </div>

        <!-- Close Button -->
        <div class="modal-action h-300">
          <label for="my_modal_6" class="btn">Close</label>
        </div>
      </div>
    </div>
  </div>
</template>
<style></style>
