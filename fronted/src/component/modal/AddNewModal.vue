<script setup>
const props = defineProps({
  openModal: {
    type: Boolean,
    required: true
  },
  boardName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['submitForm', 'cancelAction'])
</script>

<template>

</template>

<style scoped></style>
