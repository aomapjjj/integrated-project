<script setup>
import { ref, onMounted } from "vue"
import { getItemById, getItems } from "../libs/fetchUtils.js"
import TaskDetail from "../views/TaskDetail.vue"
import { checkStatus } from "../libs/checkStatus"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()
const todoList = ref([])
const selectedTodoId = ref(0)
const notFound = ref(false)

onMounted(async () => {
  const items = await getItems(import.meta.env.VITE_BASE_URL)
  todoList.value = items

  const taskId = route.params.id
  if (taskId !== undefined) {
    console.log(taskId)
    const response = await getItemById(taskId)
    if (response.status === 404 || response.status === 400) {
      router.push("/task")
      notFound.value = true
    }
  }
})

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId
}

// const openModalToDelete = (itemId) => {
//   selectedItemIdToDelete.value = itemId
//   const modal = document.getElementById("my_modal_delete")
//   modal.showModal()
// }

// const closeModal = () => {
//   const modal = document.getElementById("my_modal_delete")
//   modal.close()
// }

const isModalOpen = ref(false)

const openModalAdd = () => {
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
}
</script>

<template>
  <div class="min-h-full">
    <nav class="bg-gray-800" style="background-color: #f785b1">
      <div class="mx-auto max-w-7xl px-1">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="hidden md:block">
              <div class="ml-2 flex items-baseline space-x-4">
                <a
                  href="#"
                  class="bg-gray-900 text-white rounded-md px-3 py-2 text-sm font-medium"
                  >My Task</a
                >
              </div>
            </div>
          </div>
          <div class="flex items-center"></div>
        </div>
      </div>
    </nav>
  </div>
  <!-- header -->
  <header class="bg-white shadow">
    <div
      class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10 flex justify-between items-center"
    >
      <h1
        class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight text-gray-900"
        style="color: #9391e4"
      >
        IT-Bangmod Kradan Kanban
      </h1>
      <!-- Add new status -->
      <button class="btn" onclick="my_modal_4.showModal()">Add status</button>

      <dialog id="my_modal_4" class="modal">
        <div class="modal-box w-full md:w-11/12 max-w-5xl mx-auto">
          <span class="block text-2xl font-bold leading-6 text-gray-900 mb-1" style="margin: 15px;">Add Status</span>


          <!-- Modal content -->
          <div class="modal-action flex flex-col justify-between">
            <form method="dialog">
              <!-- Title -->
              <div
                class="modal-content py-4 text-left px-6 flex-grow flex flex-col"
              >
              
                <label
                  class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mb-8"
                >
                  <input
                    type="text"
                    class="grow"
                    placeholder="Enter Your Title"
                    maxlength="100"
                  />
                </label>
                <!-- Description -->
                <label
                  for="description"
                  class="form-control flex-grow ml-4 mb-8"
                >
                  <div class="label">
                    <span
                      class="block text-lg font-bold leading-6 text-gray-900 mb-1" 
                      >Description</span
                    >
                  </div>
                  <textarea
                    id="description"
                    class="itbkk-description textarea textarea-bordered flex-grow w-full"
                    maxlength="500"
                    rows="4"
                    placeholder="No Description Provided"
                  ></textarea>
                </label>
              </div>
              <!-- Buttons -->
              <div class="flex justify-end">
          <button class="btn mr-2" style="background-color: #f785b1;">Save</button>
          <button class="btn">Close</button>
        </div> 
            </form>
          </div>
        </div>
      </dialog>
    </div>
  </header>
  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <!-- HOME -->
        <div class="text-sm breadcrumbs">
          <ul>
            <li>
              <a @click="$router.go(-1)">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  class="w-4 h-4 stroke-current"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
                  ></path>
                </svg>
                Home
              </a>
            </li>
            <li>
              <a>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  class="w-4 h-4 stroke-current"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
                  ></path>
                </svg>
                Task Status
              </a>
            </li>
          </ul>
        </div>
        <table class="table-auto mt-10 rounded-xl overflow-hidden" style="table-layout: fixed">
          <!-- table -->
          <thead>
            <tr class="bg-base-200 mt-4 md:mt-0">
              <th class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                ">
                No.
              </th>
              <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                ">
                Name
              </th>
              <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                ">
                Description
              </th>
              <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                ">
                Action
              </th>

              <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "></th>
            </tr>
          </thead>
          <tbody>
            <!-- Iterate over todoList -->
            <TaskDetail :todo-id="selectedTodoId" />
            <tr
              class="itbkk-item"
              v-for="(item, index) in todoList"
              :key="index"
            >
              <td
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ item.id }}
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-title"
              >
                <label for="my_modal_6" @click="() => selectTodo(item.id)">
                  {{ item.title }}
                </label>
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-assignees"
                :class="{
                  italic: item.assignees.length === 0 || item.assignees === null
                }"
              >
                {{
                  item.assignees.length === 0 || item.assignees === null
                    ? "Unassigned"
                    : item.assignees
                }}
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status"
              >
                <button class="btn btn-outline">Edit</button>
                <button class="btn btn-outline">Delete</button>
              </td>
            </tr>
            <tr v-if="todoList.length === 0">
              <td colspan="4" class="text-center py-4 text-gray-500">
                No Status
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<style scoped></style>