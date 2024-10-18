import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useBoard = defineStore('board', () => {
  
  const colorBoard = ref()

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

  return {
    setChangeColor,
    getChangeColor
  }
})

export { useBoard }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoard, import.meta.hot))
}
