<script setup>
import { getItemById } from "@/libs/fetchUtils"
import { ref, watch } from "vue"
const props = defineProps({
  todoId: Number
})
// Create a reactive variable to store the prop value
const todo = ref({
  id: "",
  title: "",
  description: "",
  assignees: "",
  status: "",
  createdOn: ""
})

// Watch for changes in the prop value
watch(
  () => props.todoId,
  async (newValue) => {
    const response = await getItemById(newValue)
    todo.value = response
  }
)
</script>

<template>
  <input type="checkbox" id="my_modal_6" class="modal-toggle" />
  <div class="modal" role="dialog">
    <div class="modal-box grid grid-rows-4 grid-flow-col gap-4">
      <h3 class="font-bold text-lg">
        {{ todo.title }}
      </h3>
      <div class="label">
        <span class="label-text">{{ todo.description }}</span>
      </div>
      <textarea
        class="textarea textarea-bordered textarea-lg w-full max-w-xs"
        placeholder="Discripstion"
      ></textarea>
      <div class="label">
        <span class="label-text">{{ todo.assignees }}</span>
      </div>
      <textarea
        class="textarea textarea-bordered h-24"
        placeholder="Assignees"
      ></textarea>

      <div class="dropdown">
        <span class="label-text">{{ todo.status }}</span>
        <div tabindex="0" role="button" class="btn m-1">Status</div>
        <ul
          tabindex="0"
          class="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52"
        >
          <li><a>Item 1</a></li>
          <li><a>Item 2</a></li>
          <li><a>Item 2</a></li>
          <li><a>Item 2</a></li>
        </ul>
      </div>
      <div class="modal-action">
        <label for="my_modal_6" class="btn">Close</label>
      </div>
    </div>
  </div>
</template>
<style></style>
