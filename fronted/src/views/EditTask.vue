<script setup>
// Import ref from Vue
import { ref, watch, computed } from 'vue';
import { getItems, getItemById, editItem } from '@/libs/fetchUtils';
import { useTasks } from '../stores/store';
import { toDate } from '../libs/toDate';
import { useRouter } from 'vue-router';
import { useLimitStore } from '../stores/storeLimit';

const statusList = ref([]);
const router = useRouter();

const baseUrlTask = `${import.meta.env.VITE_BASE_URL_MAIN}/tasks`;
const baseUrlStatus = `${import.meta.env.VITE_BASE_URL_MAIN}/statuses`;
const cantEdit = ref(false);
const error = ref('');
const limitStore = useLimitStore();
const myTasks = useTasks();



const props = defineProps({
  todoId: Number,
});
const showAlertEdit = ref(false);
const showAlertAfterEdit = ref(false);

const todo = ref({
  id: '',
  title: '',
  description: '',
  assignees: '',
  status: '',
  createdOn: '',
  updatedOn: '',
});

const oldValue = ref({});

watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue);
    if (response.status === 200) {
      todo.value = await response.json();
      oldValue.value = { ...todo.value };
    }
    const itemsStatus = await getItems(baseUrlStatus);
    statusList.value = itemsStatus;

  },
  { immediate: true }
);

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

const myModal = ref(null);

const openModal = () => {
  router.push({ name: 'TaskEdit', params: { id: props.todoId } });
  myModal.value.showModal();
  console.log([props.todoId]);
};

const closeModal = () => {
  myModal.value.close();
  router.go(-1);
};

const UpdateTask = async () => {
  if (isLimitReached.value) {
    console.error(error.value);
    return; // Exit the function if the limit is reached
  }

  const trimmedTodo = {
    title: todo.value.title?.trim(),
    description: todo.value.description?.trim(),
    assignees: todo.value.assignees?.trim(),
    status: todo.value.status,
  };

  try {
    const edit = await editItem(baseUrlTask, props.todoId, trimmedTodo);
    myTasks.updateTask(
      edit.id,
      edit.title,
      edit.description,
      edit.assignees,
      edit.status,
      edit.createdOn,
      edit.updateOn
    );

    showAlertEdit.value = true;
    showAlertAfterEdit.value = true;
    setTimeout(() => {
      showAlertAfterEdit.value = false;
    }, 2300);
    console.log(edit);
  } catch (error) {
    console.error('Error updating task:', error);
  }
};

const checkEqual = computed(() => {
  const trimmedTodo = {
    ...todo.value,
    title: todo.value.title?.trim(),
    description: todo.value.description?.trim(),
    assignees: todo.value.assignees?.trim(),
  };
  const trimmedOldValue = {
    ...oldValue.value,
    title: oldValue.value.title?.trim(),
    description: oldValue.value.description?.trim(),
    assignees: oldValue.value.assignees?.trim(),
  };
  return JSON.stringify(trimmedTodo) === JSON.stringify(trimmedOldValue);
});

// ----------------------- Validate -----------------------

const isValidTitle = (title) => {
  return title && title.trim().length >= 0 && title.trim().length <= 100
}

const isFormValid = computed(() => {
  return (
    isValidTitle(todo.value.title) &&
    (!todo.value.description || todo.value.description.trim().length <= 500) &&
    (!todo.value.assignees || todo.value.assignees.trim().length <= 30) 
  )
})

const assigneesLength = computed(() => {
  return todo.value.assignees ? todo.value.assignees.length : 0
})

const descriptionLength = computed(() => {
  return todo.value.description ? todo.value.description.length : 0
})

//------------------------------------ Limit ----------------------------

const isLimitReached = computed(() => {
  const status = todo.value.status;
  if (status === "No Status" || status === "Done") {
    return false;
  }

  if (limitStore.getLimit().isLimit) {
    const tasksInStatus = myTasks.getTasks()
      .filter((task) => task.status === status);
    if (tasksInStatus.length >= limitStore.getLimit().maximumTask) {
      setTimeout(() => {
        cantEdit.value = false;
      }, 1800);
      cantEdit.value = true;
      error.value = `The status "${todo.value.status}" will have too many tasks. Please make progress and update status of existing tasks first.`;
      return true;
    }
  }

  return false;
});

</script>

<template>
  <!-- BUTTON -->
  <label @click="openModal" class="itbkk-button-edit btn" style="width: 150px">
    Edit
  </label>
  <!-- Modal window -->
  <dialog ref="myModal" class="itbkk-modal-task modal fixed w-full h-full flex">
    <div
      class="modal-container bg-white w-full xl:w-3/4 h-fit mx-auto rounded-lg shadow-lg z-50 overflow-y-auto flex"
    >
      <div class="modal-content py-4 text-left px-6 flex-grow">
        <!-- Title -->
        <div class="relative">
          <label
            class="itbkk-title input input-bordered flex items-center gap-2 font-bold ml-4 mt-1"
            style="background-color: #9fc3e9"
          >
            <input
              type="text"
              class="grow"
              v-model="todo.title"
              placeholder="Enter Your Title"
            />
          </label>
          <p
            class="text-sm text-gray-400 mb-2 mt-2 ml-4"
            style="text-align: right"
          >
            {{ todo.title.length }}/100
          </p>
        </div>
        <!-- Description -->
        <div class="relative">
          <label for="description" class="form-control flex-grow ml-4 mb-8">
            <div class="label">
              <span
                class="block text-lg font-bold leading-6 text-gray-900 mb-1"
                style="color: #9391e4"
                >Description
              </span>
            </div>
            <textarea
              id="description"
              class="itbkk-description textarea textarea-bordered h-3/4"
              rows="4"
              v-model="todo.description"
              :class="{
                'italic text-gray-500':
                  !todo.description || todo.description.trim() === '',
              }"
              placeholder="No Description Provided"
              style="height: 400px"
              >{{ todo.description }}</textarea
            >
            <p
              class="text-sm text-gray-400 mb-2 mt-2"
              style="text-align: right"
            >
              {{ descriptionLength }}/500
            </p>
          </label>
        </div>
      </div>
      <div class="modal-content py-4 text-left px-10" style="margin-top: 65px">
        <!-- Assignees -->
        <div class="mt-10">
          <span
            class="block text-lg font-bold leading-6 text-gray-900"
            style="color: #9391e4"
            >Assignees</span
          >
          <textarea
            id="assignees"
            class="itbkk-assignees textarea textarea-bordered w-full mt-1"
            rows="4"
            v-model="todo.assignees"
            :class="{
              'italic text-gray-500':
                !todo.assignees || todo.assignees.trim() === '',
            }"
            placeholder="Unassigned"
            >{{ todo.assignees }}
            </textarea
          >
          <p class="text-sm text-gray-400 mb-2 mt-2" style="text-align: right">
            {{ assigneesLength }}/30
          </p>
        </div>
        <!-- Status -->
        <div class="itbkk-status mb-4 mt-2">
          <span
            class="block text-lg font-bold leading-6 text-gray-900 mb-2"
            style="color: #9391e4"
            >Status</span
          >
          <select
            class="select select-bordered w-full max-w-xs mt-1"
            v-model="todo.status"
          >
            <option v-for="status in statusList" :value="status.name">
              {{ status.name }}
            </option>
          </select>
        </div>
        <!-- TimeZone -->
        <div class="itbkk-timezone">
          <div class="mb-4 flex items-center">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >TimeZone :
            </label>
            <h1>{{ TimeZone }}</h1>
          </div>
          <!-- CreatedOn -->
          <div class="mb-4 flex items-center itbkk-created-on">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >Created On :
            </label>
            <h1>{{ toDate(todo.createdOn) }}</h1>
          </div>
          <!-- UpdatedOn -->
          <div class="mb-4 flex items-center itbkk-updated-on">
            <label
              for="timezone"
              class="label mr-2 text-lg font-bold"
              style="color: #9391e4"
              >Updated On :
            </label>
            <h1>{{ toDate(todo.updatedOn) }}</h1>
          </div>
        </div>


        <div
        role="alert"
        class="alert shadow-lg alert-error"
        v-show="cantEdit"
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
          <span>{{ error }}</span>
        </div>

        

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
            The task has been successfully edited
          </h2>
        </div>
      </div>

        <!-- Save & Close Button -->
        <div class="modal-action flex justify-between ml-20">
          <div
            style="
              display: flex;
              justify-content: flex-end;
              margin-left: 10px;
              flex: 1;
            "
          >
            <button
              @click="UpdateTask"
              type="submit"
              class="btn"
              style="background-color: #f785b1"
              :disabled="!isFormValid || checkEqual || isLimitReached"
              :class="{ disabled: !isFormValid || checkEqual || isLimitReached }"
            >
              Save
            </button>
            <button class="btn ml-2" @click="closeModal">Close</button>
          </div>
        </div>
      </div>
    </div>
  </dialog>
</template>

<style>
.fixed-alert {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50%;
  max-width: 600px;
  padding: 15px;
  text-align: center;
  border-radius: 30px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  animation: fadeInOut 4s infinite;
}
</style>
