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
import { useStatuses } from "../stores/storeStatus"
import { useRoute, useRouter } from "vue-router"
import { useLimitStore } from '../stores/storeLimit';
import { useTasks } from '../stores/store';

const route = useRoute()
const router = useRouter()
const statusList = ref([])
const selectedStatusId = ref(0)
const notFound = ref(false)
const myModal = ref(null)
let items = []
const errorAdd = ref("")
const errorEdit = ref("")

const notAdd = ref(false)
const notEdit = ref(false)

const showAlertEdit = ref(false)
const showAlertAfterEdit = ref(false)

const showAlertAdd = ref(false)
const showAlertAfterAdd = ref(false)

const showAlertDelete = ref(false)
const showAlertAfterDelete = ref(false)

const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`

const myStatuses = useStatuses()

const limitAlert = ref(false);
const errorLimit = ref('');

const limitStore = useLimitStore()
const taskStore = useTasks()

// ------------------------------

const status = ref({
  id: "",
  name: "",
  description: "",
  createdOn: "",
  updatedOn: ""
})


const todo = ref({
  title: "",
  description: "",
  assignees: "",
  status: "No Status"
})

onMounted(async () => {
  if (myStatuses.getStatuses().length === 0) {
    items = await getItems(baseUrlStatus)
    myStatuses.addStatuses(await items)
    console.table(items)
  }
  statusList.value = myStatuses.getStatuses()
  console.log(myStatuses.getStatuses())
  console.log({ ...statusList.value })

  const statusId = route.params.id
  if (statusId !== undefined) {
    const response = await getItemById(statusId)
    if (response.status === 404 || response.status === 400) {
      router.push("/status")
      notFound.value = true
      setTimeout(() => {
        notFound.value = false
      }, 1500)
    }
  }
})

// ----------------------- Add -----------------------

const submitForm = async () => {
  const statusName = status.value.name.trim()
  const statusDescription = status.value.description.trim()
  const itemAdd = await addItem(baseUrlStatus, {
    name: statusName,
    description: statusDescription
  })
  if (statusExists(statusName)) {
    setTimeout(() => {
      notAdd.value = false
    }, 1800)
    notAdd.value = true
    return (errorAdd.value = "Status name already exists")
  }
  myStatuses.addStatus(
    itemAdd.id,
    itemAdd.name,
    itemAdd.description,
    itemAdd.createdOn,
    itemAdd.updateOn
  )
  showAlertAdd.value = true
  showAlertAfterAdd.value = true
  setTimeout(() => {
    showAlertAfterAdd.value = false
  }, 2300)

  clearForm()
}

const clearForm = () => {
  status.value.name = ""
  status.value.description = ""
}

const closeModalAdd = () => {
  clearForm()
  const modal = document.getElementById("my_modal_4")
  modal.close()
}

// logic for submitting the form

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

  if (statusExists(statusName, statusId)) {
    setTimeout(() => {
      notEdit.value = false
    }, 1800)
    notEdit.value = true
    return (errorEdit.value = "Status name already exists")
  }

  console.log(edit)
  myStatuses.updateStatus(
    edit.id,
    edit.name,
    edit.description,
    edit.createdOn,
    edit.updateOn
  )

  const statusIndex = statusList.value.findIndex(
    (status) => status.id === statusId
  )
  if (statusIndex !== -1) {
    statusList.value[statusIndex] = { ...edit }
  }
  showAlertEdit.value = true
  showAlertAfterEdit.value = true
  setTimeout(() => {
    showAlertAfterEdit.value = false
  }, 2300)
  router.go(-1)
}

const openModalToEdit = (statusId) => {
  const statusToEdit = statusList.value.find((item) => item.id === statusId)
  status.value = { ...statusToEdit }
  originalStatus.value = { ...statusToEdit }
  const modal = document.getElementById("my_modal_edit")
  modal.showModal()
  router.push({ name: "EditStatus", params: { id: statusId } })
}

const closeModalEdit = () => {
  const modal = document.getElementById("my_modal_edit")
  modal.close()
  router.go(-1)
  clearForm()
}

const isEdited = computed(() => {
  return (
    (status.value.name !== originalStatus.value.name ||
      status.value.description !== originalStatus.value.description) &&
    (status.value.name.trim().length > 0 ||
      status.value.description.trim().length > 0)
  )
})

const statusExists = (name, id) => {
  return statusList.value.some(
    (status) =>
      status.name?.trim().toLowerCase() === name?.trim().toLowerCase() &&
      status.id !== id
  )
}

// ----------------------- Edit -----------------------

// ----------------------- Delete -----------------------

const getNameById = (id) => {
  const item = statusList.value.find((item) => item.id === id)
  if (item) {
    return item.name
  } else {
    return null
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
      showAlertDelete.value = true
      showAlertAfterDelete.value = true
      setTimeout(() => {
        showAlertAfterDelete.value = false
      }, 2300)
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
  myStatuses.removeStatus(selectedItemIdToDelete.value)
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
  if (isLimitReached.value) {
    console.error(errorLimit.value);
    return;
  }
  deleteandtrans(selectedItemIdToDelete.value, statusId);
  closeModalTrans();
  console.log(selectedItemIdToDelete.value);
};

const deleteandtrans = async (statusId, newID) => {
  try {
    const status = await deleteItemAndTransfer(baseUrlStatus, statusId, newID);
    if (status === 200) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      );
    } else if (status === 400) {
      statusList.value = statusList.value.filter(
        (status) => status.id !== statusId
      );
      if (isLimitReached.value) { // Check the computed property value
        console.error(errorLimit.value);
      }
    } else {
      console.error(`Failed to delete item with ID ${statusId}`);
    }
  } catch (error) {
    console.error(`Error deleting item with ID ${statusId}:`, error);
  }
};

const isLimitReached = computed(() => {
  const status = todo.value.status;
  if (status === 'No Status' || status === 'Done') {
    return false;
  }
  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = taskStore
      .getTasks()
      .filter((task) => task.status === status);
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      return true;
    }
  }
  return false;
});

watch(isLimitReached, (newValue) => {
  if (newValue) {
    limitAlert.value = true;
    errorLimit.value = `The status "${todo.value.status}" has reached the maximum limit of ${limitStore.getLimit().maximumTask} tasks.`;
    setTimeout(() => {
      limitAlert.value = false;
    }, 1800);
  } else {
    limitAlert.value = false;
    errorLimit.value = '';
  }
});
// ----------------------- Delete -----------------------

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone

// ----------------------- Validate -----------------------
const isValidName = (name) => {
  return name && name.trim().length > 0 && name.trim().length <= 50
}

const isValidDescription = (description) => {
  return !description || description.trim().length <= 200
}

const isFormValid = computed(() => {
  return (
    isValidName(status.value.name) &&
    isValidDescription(status.value.description)
  )
})
</script>

<template>
  <div class="min-h-full max-h-fit">
    <div role="alert" class="alert alert-error" v-show="notFound">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="stroke-current shrink-0 h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span>Error! Status not found</span>
    </div>
    <nav class="bg-white shadow" style="background-color: #d8f1f1">
      <div class="mx-auto max-w-7xl px-2 flex items-center justify-between">
        <a href="#" class="flex items-center gap-4">
          <img
            src="/src/image/sj3.png"
            alt="LOGO"
            class="w-[100px] h-[100px]"
          />
          <div class="mx-auto max-w-7xl px-4 py-6 md:py-8 lg:py-10">
            <h2 class="text-sm tracking-tight text-gray-800">Welcome,</h2>
            <h1
              class="text-2xl md:text-3xl lg:text-4xl font-bold tracking-tight"
              style="
                color: #9391e4;
                text-align: center;
                text-shadow: 0 0 5px #ffffff, 0 0 5px #ffffff, 0 0 5px #ffffff;
              "
            >
              IT-Bangmod Kradan Kanban
            </h1>
          </div>
        </a>
        <div class="ml-auto">
          <button
            onclick="my_modal_4.showModal()"
            class="itbkk-button-add btn ml-4"
            style="
              position: relative;
              border-radius: 30px;
              background-color: #f785b1;
              color: white;
            "
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="currentColor"
                d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z"
              />
            </svg>
            Add new status
          </button>
        </div>
        <div class="flex items-center">
          <div class="hidden md:block">
            <div class="flex space-x-1.5"></div>
          </div>
        </div>
      </div>
    </nav>
  </div>

  <div>
    <!-- Add new status -->
    <dialog id="my_modal_4" class="itbkk-modal-status modal">
      <div class="modal-box w-full md:w-11/12 max-w-5xl mx-auto">
        <span
          class="block text-2xl font-bold leading-6 mb-1"
          style="margin: 15px; color: #9391e4; text-align: center"
          >Add Status</span
        >

        <!-- Modal content -->
        <div class="modal-action flex flex-col justify-between">
          <!-- name -->

          <div class="modal-content py-4 text-left px-6 flex-grow">
            <div class="label">
              <span
                class="block text-lg font-bold leading-6 text-gray-900 mb-1 ml-4"
                >Name
              </span>
            </div>
            <label
              class="input input-bordered flex items-center gap-2 font-bold ml-4"
            >
              <input
                type="text"
                class="itbkk-status-name grow"
                placeholder="Enter Your Status Name"
                v-model="status.name"
              />
            </label>
            <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ status.name?.length }}/50
            </p>
            <!-- Description -->
            <label for="description" class="form-control flex-grow ml-4">
              <div class="label">
                <span
                  class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                  >Description
                </span>
              </div>

              <textarea
                id="description"
                class="itbkk-status-description textarea textarea-bordered flex-grow w-full"
                rows="4"
                placeholder="No Description Provided"
                v-model="status.description"
              ></textarea>
            </label>
            <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ status.description?.length }}/200
            </p>
          </div>
          <!-- Buttons -->
          <div class="flex justify-end">
            <form form @submit.prevent="submitForm" method="dialog">
              <button
                type="submit"
                class="itbkk-button-confirm btn mr-2"
                style="flex: 3; margin: 10px; background-color: #f785b1"
                :disabled="!isFormValid"
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

        <!-- Add Error Alert -->
        <div
          role="alert"
          class="alert shadow-lg alert-error"
          v-show="notAdd"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            color: rgb(74 222 128 / var(--tw-text-opacity));
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            animation: fadeInOut 1.5s infinite;
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ errorAdd }}</span>
        </div>

        <!-- Add Success Alert -->
        <div
          role="alert"
          class="alert shadow-lg"
          :class="{ hidden: !showAlertAfterAdd }"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            animation: fadeInOut 1.5s infinite;
            color: rgb(74 222 128 / var(--tw-text-opacity));
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <h2 class="itbkk-message font-bold text-green-400">
            The Status has been successfully added
          </h2>
        </div>
      </div>
    </dialog>
  </div>

  <div class="flex flex-col items-center mt-1">
    <div class="overflow-x-auto">
      <div class="min-w-full">
        <!-- HOME -->

        <!-- Edit Alert Success -->
        <div
          role="alert"
          class="alert shadow-lg"
          :class="{ hidden: !showAlertAfterEdit }"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            animation: fadeInOut 1.5s infinite;
            color: rgb(74 222 128 / var(--tw-text-opacity));
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <h2 class="itbkk-message font-bold text-green-400">
            The Status has been successfully edited
          </h2>
        </div>
        <!-- Edit Alert -->
        <div
          role="alert"
          class="alert shadow-lg alert-error"
          v-show="notEdit"
          style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 500px;
            color: rgb(74 222 128 / var(--tw-text-opacity));
            animation: fadeInOut 1.5s infinite;
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="stroke-current shrink-0 h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>{{ errorEdit }}</span>
        </div>
        <div class="text-sm breadcrumbs mt-4">
          <ul>
            <li>
              <a @click="$router.go(-1)">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 256 256"
                  class="w-4 h-4 stroke-current mr-2"
                >
                  <path
                    fill="currentColor"
                    d="m219.31 108.68l-80-80a16 16 0 0 0-22.62 0l-80 80A15.87 15.87 0 0 0 32 120v96a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-56h32v56a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-96a15.87 15.87 0 0 0-4.69-11.32M208 208h-48v-56a8 8 0 0 0-8-8h-48a8 8 0 0 0-8 8v56H48v-88l80-80l80 80Z"
                  />
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
                  class="w-4 h-4 stroke-current mr-2"
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
          class="table-auto1 mt-5 rounded-xl overflow-hidden mb-10"
          style="table-layout: fixed"
        >
          <!-- table -->
          <thead>
            <tr class="bg-base-200">
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
                  text-align: center;
                  vertical-align: middle;
                "
              >
                Action
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              class="itbkk-item"
              v-for="(item, index) in myStatuses.getStatuses()"
              :key="index"
            >
              <!-- ID -->
              <td
                class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700"
              >
                {{ index + 1 }}
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
                  class="itbkk-button-edit btn btn-circle"
                  @click="openModalToEdit(item.id)"
                  v-if="item.name !== 'No Status' && item.name !== 'Done'"
                >
                  <p>Edit</p>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                  >
                    <g fill="none">
                      <path
                        d="M24 0v24H0V0zM12.593 23.258l-.011.002l-.071.035l-.02.004l-.014-.004l-.071-.035c-.01-.004-.019-.001-.024.005l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.017-.018m.265-.113l-.013.002l-.185.093l-.01.01l-.003.011l.018.43l.005.012l.008.007l.201.093c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.004-.011l.017-.43l-.003-.012l-.01-.01z"
                      />
                      <path
                        fill="currentColor"
                        d="M16.035 3.015a3 3 0 0 1 4.099-.135l.144.135l.707.707a3 3 0 0 1 .135 4.098l-.135.144L9.773 19.177a1.5 1.5 0 0 1-.562.354l-.162.047l-4.454 1.028a1.001 1.001 0 0 1-1.22-1.088l.02-.113l1.027-4.455a1.5 1.5 0 0 1 .29-.598l.111-.125zm-.707 3.535l-8.99 8.99l-.636 2.758l2.758-.637l8.99-8.99l-2.122-2.12Zm3.536-2.121a1 1 0 0 0-1.32-.083l-.094.083l-.708.707l2.122 2.121l.707-.707a1 1 0 0 0 .083-1.32l-.083-.094z"
                      />
                    </g>
                  </svg>
                </button>

                <dialog id="my_modal_edit" class="modal">
                  <div
                    class="modal-box w-full md:w-11/12 max-w-5xl mx-auto"
                    :key="index"
                  >
                    <span
                      class="block text-2xl font-bold leading-6 text-gray-900 mb-1"
                      style="margin: 15px"
                    ></span>

                    <!-- Modal content -->
                    <div class="modal-action flex flex-col justify-between">
                      <div
                        class="modal-content py-4 text-left px-6 flex-grow flex flex-col"
                      >
                        <div class="label">
                          <span
                            class="block text-lg font-bold leading-6 text-gray-900 mb-1 ml-4"
                            >Name
                          </span>
                        </div>
                        <label
                          class="input input-bordered flex items-center gap-2 font-bold ml-4"
                        >
                          <input
                            type="text"
                            class="itbkk-status-name grow"
                            placeholder="Enter Your Status Name"
                            maxlength="100"
                            v-model="status.name"
                          />
                        </label>
                        <p
                          class="text-sm text-gray-400 mb-2 mt-2"
                          style="text-align: right"
                        >
                          {{ status.name?.length }}/50
                        </p>

                        <!-- Description -->
                        <label
                          for="description"
                          class="itbkk-status-description form-control flex-grow ml-4"
                        >
                          <div class="label">
                            <span
                              class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                              >Description</span
                            >
                          </div>

                          <textarea
                            id="description"
                            class="textarea textarea-bordered flex-grow w-full"
                            maxlength="500"
                            rows="4"
                            placeholder="No Description Provided"
                            v-model="status.description"
                          >
                            No description is provided
                          </textarea>
                        </label>
                        <p
                          class="text-sm text-gray-400 mb-2 mt-2"
                          style="text-align: right"
                        >
                          {{ status.description?.length }}/200
                        </p>
                      </div>

                      

                      <!-- Buttons -->
                      <div class="flex justify-end">
                        <form method="dialog">
                          <button
                            @click="UpdateStatus"
                            type="submit"
                            class="itbkk-button-confirm btn mr-2"
                            :class="{ disabled: !isEdited }"
                            :disabled="!isFormValid || !isEdited"
                          >
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
                  v-if="item.name !== 'No Status' && item.name !== 'Done'"
                  class="itbkk-button-delete btn btn-circle bg-red-400"
                  style="margin-left: 10px"
                  @click="openModalToDelete(item.id)"
                >
                  <p>Delete</p>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                  >
                    <g fill="none" fill-rule="evenodd">
                      <path
                        d="M24 0v24H0V0zM12.594 23.258l-.012.002l-.071.035l-.02.004l-.014-.004l-.071-.036c-.01-.003-.019 0-.024.006l-.004.01l-.017.428l.005.02l.01.013l.104.074l.015.004l.012-.004l.104-.074l.012-.016l.004-.017l-.017-.427c-.002-.01-.009-.017-.016-.018m.264-.113l-.014.002l-.184.093l-.01.01l-.003.011l.018.43l.005.012l.008.008l.201.092c.012.004.023 0 .029-.008l.004-.014l-.034-.614c-.003-.012-.01-.02-.02-.022m-.715.002a.023.023 0 0 0-.027.006l-.006.014l-.034.614c0 .012.007.02.017.024l.015-.002l.201-.093l.01-.008l.003-.011l.018-.43l-.003-.012l-.01-.01z"
                      />
                      <path
                        fill="white"
                        d="M14.28 2a2 2 0 0 1 1.897 1.368L16.72 5H20a1 1 0 1 1 0 2h-1v12a3 3 0 0 1-3 3H8a3 3 0 0 1-3-3V7H4a1 1 0 0 1 0-2h3.28l.543-1.632A2 2 0 0 1 9.721 2zM17 7H7v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1zm-2.72-3H9.72l-.333 1h5.226z"
                      />
                    </g>
                  </svg>
                </button>
                <dialog id="my_modal_delete" class="modal">
                  <div class="modal-box" style="max-width: 500px">
                    <h3 class="font-bold text-lg">Delete a Task</h3>
                    <p
                      class="itbkk-message py-4 font-medium"
                      style="word-wrap: break-word"
                    >
                      Do you want to delete
                      {{ getNameById(selectedItemIdToDelete) }} ?
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
                <div
                  role="alert"
                  class="alert shadow-lg"
                  :class="{ hidden: !showAlertAfterDelete }"
                  style="
                    position: fixed;
                    top: 20px;
                    left: 50%;
                    transform: translateX(-50%);
                    z-index: 9999;
                    width: 500px;
                    color: rgb(74 222 128 / var(--tw-text-opacity));
                    animation: fadeInOut 1.5s infinite;
                  "
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="stroke-current shrink-0 h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  <div>
                    <h2 class="itbkk-message font-bold text-green-400">
                      The Status has been deleted
                    </h2>
                  </div>
                </div>

                <!-- Delete And Trans -->
                <dialog id="my_modal_deleteTrans" class="modal">
                  <div class="modal-box" style="max-width: 500px">
                    <h3
                      class="itbkk-message font-bold text-lg"
                      style="color: #9391e4"
                    >
                      Transfer a Status
                    </h3>
                    <p class="py-4 font-medium" style="word-wrap: break-word">
                      There is some task associated with the ...
                    </p>

                    <div
                      class="itbkk-status mb-4 mt-2 w-full flex flex-col items-center"
                    >
                      <span
                        class="block text-lg font-bold leading-6 text-gray-900 mb-2 text-center"
                        >Transfer a task to</span
                      >
                      <select
                        v-model="status.id"
                        class="select select-bordered w-full max-w-xs mt-1"
                        style="text-align: center"
                      >
                        <option v-for="status in statusList" :value="status.id">
                          {{ status.name }}
                        </option>
                      </select>

                      <div
                        class="flex justify-center w-full max-w-xs mt-4 space-x-5"
                      >
                        <button
                          class="itbkk-button-cancel btn"
                          @click="closeModalTrans"
                          style="color: #eb4343"
                        >
                          Cancel
                        </button>
                        <button
                          class="itbkk-button-confirm btn bg-green-400"
                          style="color: #fff"
                          @click="confirmDeleteTrans(status.id)"
                        >
                          Confirm
                        </button>
                      </div>
                    </div>
                    <!-- LimitAlert -->
                    <div
                        role="alert"
                        class="alert shadow-lg alert-error"
                        v-show="limitAlert"
                        style="
                          position: fixed;
                          top: 20px;
                          left: 50%;
                          transform: translateX(-50%);
                          z-index: 9999;
                          width: 500px;
                          animation: fadeInOut 1.5s infinite;
                        "
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          class="stroke-current shrink-0 h-6 w-6"
                          fill="none"
                          viewBox="0 0 24 24"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
                          />
                        </svg>
                        <span>{{ errorLimit }}</span>
                      </div>
                  </div>

                </dialog>
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
  .table-auto1 {
    width: 100%;
    overflow-x: auto;
    /* เพิ่ม overflow-x: auto; เพื่อให้เกิดการเลื่อนตารางแนวนอน */
  }
}

td {
  border-bottom: 1px solid #ababab;
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}

thead th {
  height: 3rem;
}

.table-auto1 {
  table-layout: fixed;
}
</style>
