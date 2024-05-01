<script setup>
import { getItems, getItemById, addItem } from "../libs/fetchUtils.js"
import { ref, watch, onMounted } from "vue"
import { checkStatus } from "../libs/checkStatus"

const props = defineProps({
  todoId: Number
})

const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: ""
})

const todoList = ref([])

onMounted(async () => {
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  todoList.value = items
})

watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue)
    if (response.status === 200) {
      todo.value = await response.json()
    }
  }
)

const submitForm = () => {
const add = addItem(import.meta.env.VITE_BASE_URL, {
  title: todo.value.title,
  description: todo.value.description,
  assignees: todo.value.assignees,
  status: todo.value.status
  });

  console.log(add);
};

const closeModal = () => {
  my_modal_1.close()
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
              <input type="text" class="grow" placeholder="Enter Your Title" maxlength="100"
                v-model="todo.title" />
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
                <option :disabled="!todo.status">
                  {{ todo.status ? checkStatus(todo.status) : checkStatus(todo.status) === "No Status" }}
                </option>
                <option v-for="statusItem in todoList">
                  {{
          statusItem.status === "No Status"
            ? statusItem.status === "No Status"
            : checkStatus(statusItem.status)
        }}
                </option>
              </select>
            </div>
            <!-- Close Button -->
            <div class="modal-action">
              <form method="dialog">
                <button type="submit" class="btn" style="background-color: #f785b1; margin: 10px"
                  :disabled="!todo.title.length">
                  Save
                </button>
              </form>
              <button class="btn" @click="closeModal">
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    </dialog>
  </div>
</template>
<style></style>
