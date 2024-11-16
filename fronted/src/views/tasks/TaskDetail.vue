<script setup>
import { getItemById, getAttachments } from "@/libs/fetchUtils";
import { ref, watch } from "vue";
import { toDate } from "../../libs/toDate";
import { useRoute, useRouter } from "vue-router";

// ----------------------- Router -----------------------

const router = useRouter();
const route = useRoute();

// ----------------------- Params -----------------------

const props = defineProps({
  todoId: Number,
});

const todo = ref({
  id: "",
  title: "",
  description: "",
  assignees: "",
  status: "",
  createdOn: "",
  updatedOn: "",
  attachments: [],
});

const boardId = ref();

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId;
  },
  { immediate: true }
);

watch(
  () => props.todoId,
  async (newValue) => {
    if (newValue) {
      const { item, responsed } = await getItemById(newValue, boardId.value);
      if (responsed === 403) {
        router.push({ name: "ErrorPagePermission" });
      }
      todo.value = item;
      const { statusCode, data } = await getAttachments(boardId.value, newValue);
      if (statusCode === 200 && Array.isArray(data)) {
        todo.value.attachments = data;
      } else {
        todo.value.attachments = [];
        console.error("Failed to fetch attachments");
      }
    }
  },
  { immediate: true }
);

const closeModal = () => {
  router.go(-1);
};

const TimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
</script>



<template>
  <!-- Modal window -->
  <input type="checkbox" id="my_modal_6" class="modal-toggle hidden" />
  <div class="modal fixed w-full h-full top-0 left-0 flex items-center justify-center">
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
            <textarea disabled id="description" class="itbkk-description textarea textarea-bordered h-3/4 mb-8"
              maxlength="500" rows="4" :class="{
                'italic text-gray-500':
                  todo.description?.length === 0 ||
                  todo.description?.trim() === '' ||
                  todo.description === null
              }" style="height: 400px">{{ todo.description || "No Description Provided" }}</textarea>
          </label>

          <!-- Attachments Section -->
          <!-- Attachments Section -->
          <div class="attachments-section border-t border-gray-300 pt-4 mt-6">
            <h2 class="text-lg font-bold mb-2" style="color: #9391e4">Attachments</h2>
            <div v-if="todo.attachments?.length > 0">
              <ul>
                <li v-for="(attachment, index) in todo.attachments" :key="index" class="mb-2">
                  <a :href="attachment.fileUrl" target="_blank" class="text-blue-500 underline">
                    {{ attachment.fileName }}
                  </a>
                </li>
              </ul>
            </div>
            <div v-else class="italic text-gray-500">No attachments available.</div>
          </div>
        </div>

        <div class="modal-content py-4 text-left px-10 flex-grow w-1/3 max-w-sm" style="margin-top: 65px">
          <!-- Assignees -->
          <div class="mt-10">
            <span class="block text-lg font-bold leading-6 text-gray-900 mb-2" style="color: #9391e4">Assignees</span>
            <textarea disabled id="assignees" class="itbkk-assignees textarea textarea-bordered w-full mt-1"
              maxlength="30" rows="4" :class="{
                'italic text-gray-500':
                  todo.assignees?.length === 0 ||
                  todo.assignees?.trim() === '' ||
                  todo.assignees === null
              }">{{ todo.assignees || "Unassigned" }}</textarea>
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
            <label for="my_modal_6" class="btn" @click="closeModal()">Close</label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style></style>
