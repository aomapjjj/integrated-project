<script setup>
import { ref, onMounted } from 'vue'
import { getBoardById } from '../../libs/fetchUtils.js'
import { useRoute } from 'vue-router'

const route = useRoute()
const boardName = ref('')

onMounted(async () => {
  const boardId = route.params.id
  if (boardId) {
    const Board = await getBoardById(boardId)
    if (Board && Board.item && Board.item.name) {
      boardName.value = Board.item.name
    }
  }
})
</script>

<template>
  <div>
    <nav
      class="navbar bg-white shadow px-4 py-6 flex justify-center items-center"
    >
      <div class="text-2xl font-bold tracking-tight">
        <slot></slot>
      </div>
    </nav>
  </div>
</template>

<style scoped>
.navbar {
  color: #9391e4;
  text-shadow: 0 0 5px #ffffff;
}
</style>
