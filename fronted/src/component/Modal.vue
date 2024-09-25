<script setup>
import { ref, watch, computed } from 'vue';
import { useRoute } from 'vue-router';

const boardId = ref()
const route = useRoute()
const emit = defineEmits(['close']);

const props = defineProps({
    isOpen: Boolean,
		tempVisibility: String
})

watch(
    () => route.params.id,
    (newId) => {
        boardId.value = newId
    },
    { immediate: true }
)

</script>

<template>
		<div v-if="isOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
			<div class="modal-box">
				<h3 class="font-bold text-lg">Confirm Visibility Change</h3>
				<p class="py-4">
					<!-- Are you sure you want to change the board visibility to
					{{ visibility }} ? -->
					{{ tempVisibility === "PUBLIC" ? "In public, any one can view the board, task list and task detail of tasks in the board. Do you want to change the visibility to Public ? " : "In private, only board owner can access / control board. Do you want to change the visibility to Private?" }}
				</p>
				<div class="modal-action">
					<button @click="$emit('cancel')" class="btn">Cancel</button>
        	 <button @click="$emit('confirm')" class="btn">Confirm</button>
				</div>
			</div>
    </div>

</template>

<style scoped></style>