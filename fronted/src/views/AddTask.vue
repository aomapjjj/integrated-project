<script setup>
import { getItems, getItemById, addItem } from "../libs/fetchUtils.js"
import { ref, watch, onMounted } from "vue"
import { checkStatus } from "../libs/checkStatus"
import { useRouter } from "vue-router"

const router = useRouter()
const showAlertAdd = ref(false)

const props = defineProps({
  todoId: Number
})

const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: "NO_STATUS"
})

const todoList = ref([])

onMounted(async () => {
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  todoList.value = items
})

const submitForm = async () => {
  const add = await addItem(import.meta.env.VITE_BASE_URL, {
    title: todo.value.title,
    description: todo.value.description,
    assignees: todo.value.assignees,
    status: todo.value.status
  })
  console.log(add)
  console.log(todo.value.title)
  const addedTitle = todo.value.title
  closeModal()
  clearForm()
  router.go()
  alert(`Todo with title "${addedTitle}" added successfully!`)
}

const closeModal = () => {
  my_modal_1.close()
}

const clearForm = () => {
  todo.value.title = ""
  todo.value.description = ""
  todo.value.assignees = ""
  todo.value.status = "NO_STATUS"
}
</script>
<template>
  <button onclick="my_modal_1.showModal()"
    class="bg-white text-green-500 hover:bg-gray-900 rounded-md px-3 py-2 text-sm font-medium">
    + Add
  </button>
  <div class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center">
    <dialog id="my_modal_1" class="modal">
      <div
        class="modal-container bg-white w-full md:w-11/12 lg:w-5/6 xl:w-3/4 h-fit mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex">
        <div form @submit.prevent="submitForm" class="flex justify-between w-full h-full"
          style="padding-top: 20px; padding-bottom: 20px; align-items: center">
          <!-- Title -->
          <div class="modal-content py-4 text-left px-6 flex-grow">
            <label class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mb-8">
              <input type="text" class="grow" placeholder="Enter Your Title" maxlength="100" v-model="todo.title" />
            </label>

            <!-- Description -->
            <label for="description" class="form-control flex-grow ml-4 mb-8">
              <div class="label">
                <span class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                  style="color: #9391e4">Description</span>
              </div>
              <textarea id="description" class="itbkk-description textarea textarea-bordered h-3/4 mb-8" maxlength="500"
                rows="4" placeholder="No Description Provided" style="height: 400px"
                v-model="todo.description"></textarea>
            </label>
          </div>
          <div class="modal-content py-4 text-left px-10 flex-grow w-1/3 max-w-sm" style="margin-top: 65px">
            <!-- Assignees -->
            <div class="mt-10">
              <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Assignees</span>
              <textarea id="assignees" class="itbkk-assignees textarea textarea-bordered w-full mt-1" maxlength="30"
                rows="4" placeholder="Unassigned" v-model="todo.assignees"></textarea>
            </div>
            <!-- Status -->
            <div class="itbkk-status mb-4 mt-2">
              <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Status</span>
              <select class="select select-bordered w-full max-w-xs mt-1" v-model="todo.status">
                <option disabled value="NO_STATUS">No Status</option>
                <option v-for="status in ['TO_DO', 'DOING', 'DONE']" :value="status">
                  {{ checkStatus(status) }}
                </option>
              </select>
            </div>
            <!-- Close Button -->
            <div class="modal-action" style="display: flex; justify-content: space-around">
              <form method="dialog" style="flex: 1">
                <button type="submit" class="btn" style="background-color: #f785b1; margin: 10px; width: 100%"
                  :disabled="todo.title.length === 0 || todo.title === null">
                  Save
                </button>
              </form>
              <button class="btn" style="flex: 1; margin: 10px" @click="closeModal">
                Close
              </button>
            </div>
          </div>
        </div>
        <div role="alert" class="alert shadow-lg " v-show="showAlertAdd" style="position: fixed; top: 20px; left: 50%; transform: translateX(-50%); z-index: 9999; width: 500px; color: red; animation: fadeInOut 1.5s infinite;">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="stroke-info shrink-0 w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
              <div>
                <h3 class="font-bold">The requested task does not exist</h3>
                <div class="text-xs">Please check your ID again</div>
              </div>
              <div>
                <button class="btn btn-sm" style="background-color: #9fc3e9;" @click="showAlertAdd = false">Close</button>
              </div>
        </div>
      </div>
    </dialog>
  </div>
</template>
<style></style>
