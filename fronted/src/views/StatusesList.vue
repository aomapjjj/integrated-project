<script setup>
import { ref, onMounted } from "vue"
import { getItemById, getItems, deleteItemById } from "../libs/fetchUtils.js"
import TaskDetail from "../views/TaskDetail.vue"
import EditTask from "../views/EditTask.vue"
import AddStatus from "../views/AddStatus.vue"
import { checkStatus } from "../libs/checkStatus"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()


const selectedTodoId = ref(0)
const notFound = ref(false)
const deleteComplete = ref(false)

let items = [] // ประกาศ items เป็นตัวแปร global


const statusList = ref([])

const status = ref({
  statusid: "",
  statusname: "",
  statusdescription: "",
})


onMounted(async () => {
  const lists = await getItems(import.meta.env.VITE_BASE_URL)
  statusList.value = lists
  console.log('list' , lists)
})

const selectTodo = (todoId) => {
  selectedTodoId.value = todoId
}

const deleteTodo = async (todoId) => {
  try {
    const status = await deleteItemById(import.meta.env.VITE_BASE_URL, todoId)
    if (status === 200) {
      todoList.value = todoList.value.filter((todo) => todo.id !== todoId)
    } else {
      console.error(`Failed to delete item with ID ${todoId}`)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${todoId}:`, error)
  }
}
const filterAndLogTitleById = (id) => {
  const item = items.find((item) => item.id === id)

  if (item) {
    console.log(item.title)
    return item.title
  } else {
    console.log(`No item found with id ${id}`)

    return ""
  }
}

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

const selectedItemIdToDelete = ref(0)

// const openModalToDelete = (itemId) => {
//   selectedItemIdToDelete.value = itemId
//   const modal = document.getElementById("my_modal_delete")
//   modal.showModal()
// }

// const closeModal = () => {
//   const modal = document.getElementById("my_modal_delete")
//   modal.close()
// }

const confirmDelete = () => {
  deleteTodo(selectedItemIdToDelete.value)
  closeModal()
  deleteComplete.value = true
  setTimeout(() => {
    deleteComplete.value = false
  }, 2300)
}



const isModalOpen = ref(false);

const openModalAdd = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

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
  <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10 flex justify-between items-center">
    <h1 class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight text-gray-900" style="color: #9391e4">
      IT-Bangmod Kradan Kanban
    </h1>



     <!-- Add new status -->
     
     <button @click="openModalAdd" class="itbkk-button-add btn bg-green-400 ml-4">
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
      <path fill="currentColor" d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z" />
    </svg>
    Add new status
  </button>

  <!-- Modal -->
  <div v-if="isModalOpen" id="default-modal" tabindex="-1" aria-hidden="true" class="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
    <div class="relative p-4 w-full max-w-2xl max-h-full">
      <!-- Modal content -->
      <div class="relative bg-white rounded-lg shadow dark:bg-gray-700">
        <!-- Modal header -->
        <div class="flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600">
          <h3 class="text-xl font-semibold text-gray-900 dark:text-white">
          </h3>
          <button @click="closeModal" type="button" class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-hide="default-modal">
            <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
            </svg>
            <span class="sr-only">Close modal</span>
          </button>
        </div>
        <!-- Modal body -->
        <div class="p-4 md:p-5 space-y-4">
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
          </p>
          <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400">
          </p>
        </div>
        <!-- Modal footer -->
        <div class="flex items-center p-4 md:p-5 border-t border-gray-200 rounded-b dark:border-gray-600">
          <button @click="closeModal" type="button" class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">I accept</button>
          <button @click="closeModal" type="button" class="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700">Decline</button>
        </div>
      </div>
    </div>

</div>
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

        <table
          class="table-auto mt-10 rounded-xl overflow-hidden"
          style="table-layout: fixed"
        >
      
          <!-- table -->
          <thead>
            <tr class="bg-base-200 mt-4 md:mt-0">
              <th
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                No.
              </th>
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                Name
              </th>
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                Descirption
              </th>
              <th
                class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                style="
                  background-color: #9fc3e9;
                  border-bottom: 2px solid #9fc3e9;
                  color: #fff;
                "
              >
                Action
              </th>

              
            </tr>
          </thead>
          <tbody>
            <!-- Iterate over todoList -->
            <TaskDetail :todo-id="selectedTodoId" />
            <tr
              class="itbkk-item"
              v-for="(item, index) in statusList"
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
                <label
                  for="my_modal_6"
                  @click="() => selectTodo(item.id)"
                  style="display: block; width: 100%; height: 100%"
                >
                  {{ item.title }}
                </label>
              </td>
              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-assignees"
                :class="{
                  italic: !item.assignees || item.assignees.length === 0
                }"
              >
                {{
                  !item.assignees || item.assignees.length === 0
                    ? "Unassigned"
                    : item.assignees
                }}
              </td>
             
              <div class="itbkk-button-action">
                <!-- EDIT -->
                <td class="itbkk-button-edithidden md:table-cell text-sm pl-4">
                  <EditTask :todo-id="item.id" />
                </td>
                <!-- DELETE -->
                <td
                  class="hidden md:table-cell px-4 py-3 text-center md:text-left text-sm text-gray-700"
                >
                  <button
                    class="itbkk-button-delete btn btn-circle btn-outline btn-sm"
                    @click="openModalToDelete(item.id)"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-6 w-6"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      style="color: #eb4343"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M6 18L18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                  <dialog id="my_modal_delete" class="modal">
                    <div class="modal-box" style="max-width: 1000px">
                      <h3 class="itbkk-message font-bold text-lg">
                        Delete a Task
                      </h3>
                      <p class="py-4 font-medium" style="word-wrap: break-word">
                        Do you want to delete the task number
                        {{ selectedItemIdToDelete }} - "{{
                          filterAndLogTitleById(selectedItemIdToDelete)
                        }}"?
                      </p>
                      <div class="modal-action">
                        <button
                          class="itbkk-button-cancel btn"
                          @click="closeModal"
                          style="color: #eb4343"
                        >
                          Cancel
                        </button>
                        <button
                          class="itbkk-button-confirm btn bg-green-400"
                          style="color: #fff"
                          @click="confirmDelete"
                        >
                          Confirm
                        </button>
                      </div>
                    </div>
                  </dialog>
                </td>
              </div>
            </tr>

            <!-- DELETE COMPLETE -->
            <tr >
              <td colspan="4" class="text-center py-4 text-gray-500">
                No task
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div
        role="alert"
        class="alert shadow-lg"
        v-show="deleteComplete"
        style="
          position: fixed;
          top: 20px;
          left: 50%;
          transform: translateX(-50%);
          z-index: 9999;
          width: 500px;
          color: red;
          animation: fadeInOut 1.5s infinite;
        "
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          class="stroke-info shrink-0 w-6 h-6"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          ></path>
        </svg>
        <div>
          <h2 class="font-bold">
            Delete Complete "{{
              filterAndLogTitleById(selectedItemIdToDelete)
            }}"
          </h2>
        </div>
        <div>
          <button
            class="btn btn-sm"
            style="background-color: #9fc3e9"
            @click="deleteComplete = false"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
table {
  border-collapse: collapse;
}

table td {
  border-top: 1px solid rgba(182, 182, 188, 0.66);
  /* เส้นด้านล่างของเซลล์ */
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}
</style>
