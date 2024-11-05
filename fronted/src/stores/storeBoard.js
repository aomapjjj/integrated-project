import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useBoard = defineStore('board', () => {
  const colorBoard = ref()
  const boards = ref([])
  const collabs = ref([])

  const setChangeColor = (color) => {
    colorBoard.value = color
    localStorage.setItem('colorBoard', JSON.stringify(colorBoard.value))
  }

  const getChangeColor = () => {
    const storedChangeColor = localStorage.getItem('colorBoard')
    if (storedChangeColor) {
      colorBoard.value = JSON.parse(storedChangeColor)
    }
    return colorBoard.value
  }

  const setBoards = (newBoards) => {
    boards.value = newBoards
  }

  const setCollabs = (newCollabs) => {
    collabs.value = newCollabs
  }

  return {
    setChangeColor,
    getChangeColor,
    setBoards,
    setCollabs
  }
})

export { useBoard }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoard, import.meta.hot))
}
