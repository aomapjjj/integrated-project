<script setup>
import { getItems, getItemById } from "@/libs/fetchUtils"
import { ref, watch, onMounted } from "vue"
import { checkStatus } from "../libs/checkStatus"
import { toDate } from "../libs/toDate"
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
  createdOn: "",
  updatedOn: ""
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
    if (response.status === 200) {
      todo.value = await response.json()
    }
  }
)

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
</script>

<template>
  <!-- Modal window -->
  <input type="checkbox" id="my_modal_6" class="modal-toggle hidden"/>
    <div class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center">
      <div class="modal-overlay absolute w-full h-full bg-gray-900 opacity-50"></div>
      <div class="modal-container bg-white w-full md:w-11/12 lg:w-5/6 xl:w-3/4 h-5/6 mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex">
            <div class="modal-content py-4 text-left px-6 flex-grow">
            <!-- Title -->
            <label class="itbkk-title input input-bordered flex items-center gap-2 font-bold" style="background-color: #9fc3e9;">
              <input type="text" class="grow" v-model="todo.title" placeholder="Enter Your Title" maxlength="100"/>
            </label>

            <!-- Description -->
            <label for="description" class="form-control">
              <div class="label">
                <span class="label-text font-bold input-md">Description</span>
              </div>
              <textarea
                id="description"
                class="itbkk-description textarea textarea-bordered h-3/4 w-11/12"
                maxlength="500"
                rows="4"
                :class="{
                'italic text-gray-500':
                  todo.description.length === 0 ||
                  todo.description.trim() === '' ||
                  todo.description === null }" 
                placeholder="No Description Provided" style="height:400px">{{ todo.description || "No Description Provided" }}
              </textarea>
            </label>
      </div>
      <div class="w-1/3 mr-6">
        <!-- Assignees -->
        <div class="mb-4">
          <label for="assignees" class="label">Assignees</label>
          <textarea
            id="assignees"
            class="textarea textarea-bordered w-full itbkk-assignees"
            maxlength="30"
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

        <!-- Status -->
        <div class="itbkk-status mb-4">
          <label for="status" class="label">Status</label>
          <select class="select select-bordered w-full max-w-xs">
            <!-- <option disabled selected>No Status</option> -->
            <option v-for="statusItem in todoList">{{ checkStatus(statusItem.status) }}</option>
          </select>
        </div>

        <!-- TimeZone -->
        <div class="itbkk-timezone">
          <div class="mb-4 flex items-center">
            <label for="timezone" class="label mr-2">TimeZone : </label>
            <h1>{{ TimeZone }}</h1>
          </div>

          <!-- CreatedOn -->
          <div class="mb-4 flex items-center itbkk-created-on">
            <label for="timezone" class="label mr-2">Created On : </label>
            <h1>{{ toDate(todo.createdOn) }}</h1>
          </div>

          <!-- UpdatedOn -->
          <div class="mb-4 flex items-center itbkk-updated-on">
            <label for="timezone" class="label mr-2">Updated On : </label>
            <h1>{{ toDate(todo.updatedOn) }}</h1>
          </div>
        </div>
        <!-- Close Button -->
        <div class="itbkk-button modal-action h-300">
          <label for="my_modal_6" class="btn" style="background-color: #f785b1">Save</label>
          <label for="my_modal_6" class="btn">Close</label>
        </div>

      </div>
    </div>
  </div>
</template>
<style></style>