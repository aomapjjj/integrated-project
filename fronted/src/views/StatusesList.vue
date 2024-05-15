<script setup>
import { ref, onMounted, computed, watch } from "vue"
import {
  getItemById,
  getItems,
  addItem,
  editItem,
  deleteItemById,
  deleteItemAndTransfer
} from "../libs/fetchUtils.js"

import { toDate } from "../libs/toDate.js"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()
const statusList = ref([])
const selectedStatusId = ref(0)
const notFound = ref(false)
const myModal = ref(null)

const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`

// ------------------------------

const status = ref({
  id: "",
  name: "",
  description: "",
  createdOn: "",
  updatedOn: ""
})

onMounted(async () => {
  const items = await getItems(baseUrlStatus)
  statusList.value = items

  console.log({ ...statusList.value })
  const statusId = route.params.id
  if (statusId !== undefined) {
    const response = await getItemById(statusId)
    if (response.status === 404 || response.status === 400) {
      router.push("/task")
      notFound.value = true
    }
  }
})

// ----------------------- Add -----------------------

const submitForm = async () => {
  const statusName = status.value.name.trim()
  const statusDescription = status.value.description.trim()
  await addItem(baseUrlStatus, {
    name: statusName,
    description: statusDescription
  })
  clearForm()
}

const clearForm = () => {
  status.value.name = ""
  status.value.description = ""
}

const closeModalAdd = () => {
  const modal = document.getElementById("my_modal_4")
  modal.close()
  router.go()
}

// ----------------------- Add -----------------------

const selectStatus = (statusId) => {
  selectedStatusId.value = statusId
}

// ----------------------- Edit -----------------------
const originalStatus = ref({})

const UpdateStatus = async () => {
  const statusName = status.value.name
  const statusDescription = status.value.description
  const statusId = status.value.id

  const edit = await editItem(baseUrlStatus, statusId, {
    name: statusName,
    description: statusDescription
  })
  console.log(edit)
  console.log(status.value)
  router.go()
}

const openModalToEdit = (statusId) => {
  const statusToEdit = statusList.value.find((item) => item.id === statusId)
  status.value = { ...statusToEdit }
  originalStatus.value = { ...statusToEdit }
  console.log(status.value)
  console.log(originalStatus.value)
  const modal = document.getElementById("my_modal_edit")
  modal.showModal()
}

const closeModalEdit = () => {
  const modal = document.getElementById("my_modal_edit")
  modal.close()
}

const isEdited = computed(() => {
  return (
    (status.value.name !== originalStatus.value.name ||
      status.value.description !== originalStatus.value.description) &&
    (status.value.name.trim().length > 0 ||
      status.value.description.trim().length > 0)
  )
})
// ----------------------- Edit -----------------------

// ----------------------- Delete -----------------------

const getNameById = (id) => {
  const item = statusList.value.find((item) => item.id === id)
  if (item) {
    return item.name
  } else {
    return null // หรือให้คืนค่าที่เหมาะสมในกรณีที่ไม่พบ ID
  }
}

const selectedItemIdToDelete = ref(0)

const deleteStatus = async (statusId) => {
  try {
    const status = await deleteItemById(baseUrlStatus, statusId)
    if (status === 200) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
    } else {
      console.error(`Failed to delete item with ID ${statusId}`)
    }

    if (status === 500) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
      openModalToDeleteTrans(statusId)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${statusId}:`, error)
  }
}

const openModalToDelete = (statusId) => {
  selectedItemIdToDelete.value = statusId
  const modal3 = document.getElementById("my_modal_delete")
  modal3?.showModal()
}

const closeModal = () => {
  const modal3 = document.getElementById("my_modal_delete")
  modal3?.close()
}

const confirmDelete = () => {
  deleteStatus(selectedItemIdToDelete.value)
  closeModal()

  console.log(selectedItemIdToDelete.value)
}

const openModalToDeleteTrans = (statusId) => {
  selectedItemIdToDelete.value = statusId
  const modal3 = document.getElementById("my_modal_deleteTrans")
  modal3?.showModal()
}

const closeModalTrans = () => {
  const modal3 = document.getElementById("my_modal_deleteTrans")
  modal3?.close()
}

const confirmDeleteTrans = (statusId) => {
  deleteandtrans(selectedItemIdToDelete.value, statusId)
  closeModalTrans()
  console.log(selectedItemIdToDelete.value)
}

const deleteandtrans = async (statusId, newID) => {
  try {
    const status = await deleteItemAndTransfer(baseUrlStatus, statusId, newID)
    if (status === 200) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      )
    } else {
      console.error(`Failed to delete item with ID ${statusId}`)
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${statusId}:`, error)
  }
}

// ----------------------- Delete -----------------------

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
</script>

<template>
  <div class="min-h-full">
    <nav class="bg-gray-800" style="background-color: #f785b1">
      <div class="mx-auto max-w-7xl px-1">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="hidden md:block"></div>
          </div>
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
      <div class="itbkk-button-add">
        <button class="btn" onclick="my_modal_4.showModal()">Add status</button>
      </div>
      <dialog id="my_modal_4" class="itbkk-modal-status modal">
        <div class="modal-box w-full md:w-11/12 max-w-5xl mx-auto">
          <span
            class="block text-2xl font-bold leading-6 text-gray-900 mb-1"
            style="margin: 15px"
            >Add Status</span
          >

          <!-- Modal content -->
          <div class="modal-action flex flex-col justify-between">
            <!-- name -->

            <div
              class="modal-content py-4 text-left px-6 flex-grow flex flex-col"
            >
              <label
                class="itbkk-status-name input input-bordered flex items-center gap-2 font-bold ml-4 mb-8"
              >
                <input
                  type="text"
                  class="grow"
                  placeholder="Enter Your Title"
                  maxlength="100"
                  v-model="status.name"
                />
                <p class="text-sm text-red-400 ml-4 mb-2 mt-2">
              {{ status.name?.length }}/100
            </p>
              </label>
              <!-- Description -->
              <label
                for="description"
                class="itbkk-status-description form-control flex-grow ml-4 mb-8"
              >
                <div class="label">
                  <span
                    class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                    >Description <p class="text-sm text-red-400 ml-4 mb-2 mt-2">
              {{ status.description?.length }}/500
            </p> </span
                  >
                  
                </div>

                <textarea
                  id="description"
                  class="itbkk-description textarea textarea-bordered flex-grow w-full"
                  maxlength="500"
                  rows="4"
                  placeholder="No Description Provided"
                  v-model="status.description"
                ></textarea>
              </label>
            </div>
            <!-- Buttons -->
            <div class="flex justify-end">
              <form form @submit.prevent="submitForm" method="dialog">
                <button
                  type="submit"
                  class="itbkk-button-confirm btn mr-2"
                  style="flex: 3; margin: 10px; background-color: #f785b1"
                  :disabled="status.name?.length === 0 || status.name === null"
                >
                  Save
                </button>
              </form>
              <button
                class="itbkk-button-cancel btn"
                style="margin: 10px"
                @click="closeModalAdd()"
              >
                Cancel
              </button>
            </div>
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
                Description
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
            <tr
              class="itbkk-item"
              v-for="(item, index) in statusList"
              :key="index"
            >
              <!-- ID -->
              <td
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ item.id }}
              </td>
              <!-- NAME -->

              <td class="">
                <label
                  class="itbkk-status-name"
                  for="my_modal_6"
                  @click="selectStatus(item.id)"
                >
                  {{ item.name }}
                </label>
              </td>

              <td
                class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700"
                :class="{
                  italic: !item.description || item.description?.length === 0
                }"
              >
                <label
                  for="my_modal_6"
                  :class="{
                    italic: !item.description || item.description?.length === 0
                  }"
                >
                  {{
                    !item.description || item.description.length === 0
                      ? "No description is provided"
                      : item.description
                  }}
                </label>
              </td>

              <!-- Edit modal-->

              <td
                class="px-4 py-2 text-center md:text-left text-sm text-gray-700 itbkk-status"
              >
                <button
                  class="itbkk-button-edit btn"
                  @click="openModalToEdit(item.id)"
                  v-if="item.name !== 'No Status'"
                >
                  Edit
                </button>

                <dialog id="my_modal_edit" class="modal">
                  <div class="modal-box w-full md:w-11/12 max-w-5xl mx-auto">
                    <span
                      class="block text-2xl font-bold leading-6 text-gray-900 mb-1"
                      style="margin: 15px"
                      >Edit Status</span
                    >

                    <!-- Modal content -->
                    <div class="modal-action flex flex-col justify-between">
                      <div
                        class="modal-content py-4 text-left px-6 flex-grow flex flex-col"
                      >
                        <label
                          class="itbkk-status-name input input-bordered flex items-center gap-2 font-bold ml-4 mb-8"
                        >
                          <input
                            type="text"
                            class="grow"
                            placeholder="Enter Your Title"
                            maxlength="100"
                            v-model="status.name"
                          />
                          <p class="text-sm text-red-400 ml-4 mb-2 mt-2">
              {{ status.name?.length }}/100
            </p>
                        </label>

                        <!-- Description -->
                        <label
                          for="description"
                          class="form-control flex-grow ml-4 mb-8"
                        >
                          <div class="label">
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                              >Description <p class="text-sm text-red-400 ml-4 mb-2 mt-2">
              {{ status.description?.length }}/500
            </p></span
                            >
                            
                          </div>

                          <textarea
                            id="description"
                            class="itbkk-description textarea textarea-bordered flex-grow w-full"
                            maxlength="500"
                            rows="4"
                            placeholder="No Description Provided"
                            v-model="status.description"
                          >
                            No description is provided
                          </textarea>
                        </label>
                        <div style="display: flex; flex-direction: row">
                          <div style="margin-right: 30px">
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                              >Time Zone</span
                            >
                            <label>{{ TimeZone }}</label>
                          </div>
                          <div style="margin-right: 30px">
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                              >Created On</span
                            >
                            <label>{{ toDate(status.createdOn) }}</label>
                          </div>
                          <div>
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                              >Updated On</span
                            >
                            <label>{{ toDate(status.updatedOn) }}</label>
                          </div>
                        </div>
                      </div>

                      <!-- Buttons -->
                      <div class="flex justify-end">
                        <form method="dialog">
                          <button @click="UpdateStatus" type="submit" class="itbkk-button-confirm btn mr-2" :class="{'disabled': !isEdited }" :disabled="!isEdited">
  Save
</button>
                        </form>
                        <button
                          class="itbkk-button-cancel btn"
                          @click="closeModalEdit()"
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  </div>
                </dialog>

                <!-- Delete Modal -->
                <button
                  v-if="item.name !== 'No Status'"
                  class="itbkk-button-delete btn"
                  style="margin-left: 10px"
                  @click="openModalToDelete(item.id)"
                >
                  Delete
                </button>
                <dialog id="my_modal_delete" class="modal">
                  <div class="modal-box" style="max-width: 1000px">
                    <h3 class="font-bold text-lg">Delete a Task</h3>
                    <p
                      class="itbkk-message py-4 font-medium"
                      style="word-wrap: break-word"
                    >
                      Do you want to delete
                      {{ getNameById(selectedItemIdToDelete) }}
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
            
                <!-- Delete And Trans -->

                <dialog id="my_modal_deleteTrans" class="modal">
                  <div class="modal-box" style="max-width: 1000px">
                    <h3 class="itbkk-message font-bold text-lg">
                      Transfer a Status
                    </h3>
                    <p class="py-4 font-medium" style="word-wrap: break-word">
                      There is some task associated with the ...
                    </p>

                    <div class="itbkk-status mb-4 mt-2">
                      <span
                        class="block text-lg font-bold leading-6 text-gray-900 mb-2"
                        >Transfer To</span
                      >
                      <select
                        v-model="status.id"
                        class="select select-bordered w-full max-w-xs mt-1"
                      >
                        <option v-for="status in statusList" :value="status.id">
                          {{ status.name }}
                        </option>
                      </select>
                      <button
                        class="itbkk-button-confirm btn bg-green-400"
                        style="color: #fff"
                        @click="confirmDeleteTrans(status.id)"
                      >
                        Confirm
                        
                      </button>
                    </div>

                    <div class="modal-action">
                      <button
                        class="itbkk-button-cancel btn"
                        @click="closeModalTrans"
                        style="color: #eb4343"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </dialog>
                <!-- Delete And Trans -->
              </td>
            </tr>
            <tr v-if="statusList?.length === 0">
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
<style>
/* เพิ่ม media query เพื่อปรับขนาดของตารางเมื่อจอมีขนาดเล็กกว่า 576px */
@media (max-width: 576px) {
  .table-auto {
    width: 100%;
    overflow-x: auto; /* เพิ่ม overflow-x: auto; เพื่อให้เกิดการเลื่อนตารางแนวนอน */
  }
  .table-auto thead,
  .table-auto tbody {
    display: block;
  }

  .table-auto thead th {
    display: none;
  }
  .table-auto tbody td {
    border-bottom: 1px solid #ccc;
    display: block;
    text-align: center;
  }
}
/* ใช้ word-break เพื่อให้ข้อความยาว ๆ ขึ้นบรรทัดใหม่ */
.itbkk-status-description label {
  word-break: break-word;
}
</style>
