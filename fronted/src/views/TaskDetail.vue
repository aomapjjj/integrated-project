<script setup>
import { getItems, getItemById } from "@/libs/fetchUtils"
import { ref, watch, onMounted } from "vue"

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

const isLoading = ref(true)

// Watch for changes in the prop value
watch(
  () => props.todoId,
  async (newValue) => {
      const response = await getItemById(newValue)
    console.log(newValue)
    if (response.status === 200) {
      todo.value = await response.json()
      console.log(todo.value)
      isLoading.value = false
      console.log(isLoading.value)
    }
  }, { immediate: true }
)

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
</script>

<template>
  <!-- Modal window -->
  <input type="checkbox" id="my_modal_6" class="modal-toggle hidden" />
  <div class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center" v-if="isLoading === false">
    <div
      class="modal-container bg-white w-full md:w-11/12 lg:w-5/6 xl:w-3/4 h-fit mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex">
      <div class="flex justify-between w-full h-full"
        style="padding-top: 20px; padding-bottom: 20px; align-items: center">
        <div class="modal-content py-4 text-left px-6 flex-grow">
          <!-- Title -->
          <label class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mb-8"
            style="background-color: #9fc3e9">
            <input type="text" class="grow" v-model="todo.title" placeholder="Enter Your Title" maxlength="100" />
          </label>

          <!-- Description -->
          <label for="description" class="form-control flex-grow ml-4 mb-8">
            <div class="label">
              <span class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                style="color: #9391e4">Description</span>
            </div>
            <textarea id="description" class="itbkk-description textarea textarea-bordered h-3/4 mb-8" maxlength="500"
              rows="4" :class="{
    'italic text-gray-500':
      todo.description?.length === 0 ||
      todo.description?.trim() === '' ||
      todo.description === null
  }" placeholder="No Description Provided" style="height: 400px">{{ todo.description }}</textarea>
          </label>
        </div>

        <div class="modal-content py-4 text-left px-10 flex-grow w-1/3 max-w-sm" style="margin-top: 65px">
          <!-- Assignees -->
          <div class="mt-10">
            <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Assignees</span>
            <textarea id="assignees" class="itbkk-assignees textarea textarea-bordered w-full mt-1" maxlength="30"
              rows="4" :class="{
    'italic text-gray-500':
      todo.assignees?.length === 0 ||
      todo.assignees?.trim() === '' ||
      todo.assignees === null
  }" placeholder="Unassigned">{{ todo.assignees }}</textarea>
          </div>

          <!-- Status -->
          <div class="itbkk-status mb-4 mt-2">
            <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Status</span>
            {{ todo.status }}
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
          <!-- Close Button -->
          <div class="itbkk-button modal-action">
            <label for="my_modal_6" class="btn">Close</label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style></style>
