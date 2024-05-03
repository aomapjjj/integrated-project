<script setup>
// Import ref from Vue
import { ref, watch, onMounted } from 'vue';

// Create a ref for the dialog element
const myModal = ref(null)

// Function to open the modal
const openModal = () => {
  myModal.value.showModal()
}

// Function to close the modal
const closeModal = () => {
  myModal.value.close()
}

const props = defineProps({
  todoId: Number,
});
// Create a reactive variable to store the prop value
const todo = ref({
  id: '',
  title: '',
  description: '',
  assignees: '',
  status: '',
  createdOn: '',
  updatedOn: '',
});

const todoList = ref([]);

onMounted(async () => {
  console.log(import.meta.env.VITE_BASE_URL);
  const items = await getItems(import.meta.env.VITE_BASE_URL);
  console.log(items);
  todoList.value = items;
  console.log(todoList.value);
  console.log(items);
});

// Watch for changes in the prop value
watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue);
    if (response.status === 200) {
      todo.value = await response.json();
    }
  }
);

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
</script>

<template>
  <!-- Modal window -->
  <div>
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

    <dialog ref="myModal">
        <div class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center">
    <div class="modal-container bg-white w-full md:w-11/12 lg:w-5/6 xl:w-3/4 h-fit mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex">
      <div class="flex justify-between w-full h-full" style="padding-top: 20px; padding-bottom: 20px; align-items: center">
        <div class="modal-content py-4 text-left px-6 flex-grow">
          <!-- Title -->
          <label class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mb-8" style="background-color: #9fc3e9">
            <input type="text" class="grow" v-model="todo.title" placeholder="Enter Your Title" maxlength="100"/>
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
              :class="{
                'italic text-gray-500':
                  todo.description.length === 0 ||
                  todo.description.trim() === '' ||
                  todo.description === null,
              }"
             placeholder="No Description Provided"
             style="height: 400px"
              >{{ todo.description }}</textarea
            >
           
          </label>
        </div>

        <div class="modal-content py-4 text-left px-10 flex-grow w-1/3 max-w-sm" style="margin-top: 65px;">
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
              :class="{
                'italic text-gray-500':
                  todo.assignees.length === 0 ||
                  todo.assignees.trim() === '' ||
                  todo.assignees === null,
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
            <select class="select select-bordered w-full max-w-xs mt-1">
              <option disabled selected>{{ checkStatus(todo.status) }}</option>
              <option v-for="statusItem in todoList" disabled>
                {{ checkStatus(statusItem.status) }}
              </option> 
            </select>
          </div>

          <!-- TimeZone -->
          <div class="itbkk-timezone">
            <div class="mb-4 flex items-center">
              <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">TimeZone : </label>
              <h1>{{ TimeZone }}</h1>
            </div>

            <!-- CreatedOn -->
            <div class="mb-4 flex items-center itbkk-created-on">
              <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">Created On : </label>
              <h1>{{ toDate(todo.createdOn) }}</h1>
            </div>

            <!-- UpdatedOn -->
            <div class="mb-4 flex items-center itbkk-updated-on">
              <label for="timezone" class="label mr-2 text-lg font-bold" style="color: #9391e4">Updated On : </label>
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
    </dialog>
  </div>
</template>
<style></style>