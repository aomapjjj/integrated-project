<script setup>
import { getItems, getItemById, addItem } from "../libs/fetchUtils.js"
import { ref, onMounted, watch } from "vue"
import { checkStatus } from "../libs/checkStatus"
import { useRouter } from "vue-router"

const router = useRouter()
const showAlertAdd = ref(false)
const showAlertAfterClose = ref(false)
const statusList = ref([])

const props = defineProps({
  todo: Object
})

watch(
  () => props.todo,
  async () => {
    const itemsStatus = await getItems(import.meta.env.VITE_BASE_URL_STATUS)
    statusList.value = itemsStatus

    console.log('itemStatuss', itemsStatus)
  }, { immediate: true }
)

const submitForm = async () => {
  const trimmedTitle = props.todo.title?.trim();
  const trimmedDescription = props.todo.description?.trim();
  const trimmedAssignees = props.todo.assignees?.trim();

  await addItem(import.meta.env.VITE_BASE_URL, {
    title: trimmedTitle,
    description: trimmedDescription,
    assignees: trimmedAssignees,
    status: props.todo.status
  });

  // clearForm()
  showAlertAdd.value = true
  showAlertAfterClose.value = true
  setTimeout(() => {
    showAlertAfterClose.value = false;
  }, 2300);

}
const closeModal = () => {
  my_modal_1.close()
  router.go()
}

// const clearForm = () => {
//   props.todo.value.title = ""
//   props.todo.value.description = ""
//   props.todo.value.assignees = ""
//   props.todo.value.status = "NO_STATUS"
// }

</script>

<template>
  <div class="flex justify-end mt-9">
    <!-- ADD -->
    <button onclick="my_modal_1.showModal()" class="itbkk-button-add btn bg-green-400 ml-4" style="position: relative">
      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
        <path fill="currentColor"
          d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z" />
      </svg>
      Add new task
    </button>
  </div>

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
                <option value="NO_STATUS">No Status</option>
                <option v-for="status in statusList" :value="status.name">
                  {{ status.name }}
                </option>
              </select>
            </div>

            <!-- Cancel & Save Button -->
            <div class="modal-action" style="display: flex; justify-content: space-around">
              <form method="dialog" style="flex: 1">
                <button type="submit" class="itbkk-button-confirm btn"
                  style="background-color: #f785b1; margin: 10px; width: 100%"
                  :disabled="todo.title.length === 0 || todo.title === null">
                  Save
                </button>
              </form>
              <button class="itbkk-button-cancel btn" style="flex: 1; margin: 10px" @click="closeModal">
                Cancel
              </button>
            </div>
          </div>
        </div>


        <!-- ALERT -->
        <div role="alert" class="alert shadow-lg" :class="{ hidden: !showAlertAfterClose }" style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            width: 300px;
            padding: 10px;
            background-color: #fff;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
          ">

          <h3 class="font-bold">The task has been successfully added</h3>

        </div>
      </div>
    </dialog>
  </div>
</template>

<style>
.add-button:hover {
  border-color: white;
  color: white;
}

.alert {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  width: 300px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}
</style>
