import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"

const useBoard = defineStore("board", () => {
  const colorBoard = ref()
  const boards = ref([])
  const collabs = ref([])

  const setChangeColor = (color) => {
    colorBoard.value = color
    localStorage.setItem("colorBoard", JSON.stringify(colorBoard.value))
  }

  const getChangeColor = () => {
    const storedChangeColor = localStorage.getItem("colorBoard")
    if (storedChangeColor) {
      colorBoard.value = JSON.parse(storedChangeColor)
    }
    return colorBoard.value
  }

  const saveBoardsToLocalStorage = () => {
    localStorage.setItem("boards", JSON.stringify(boards.value))
  }

  const addNewBoard = (newBoard) => {
    if (!boards.value.some((board) => board.id === newBoard.id)) {
      boards.value.push({ ...newBoard })
      saveBoardsToLocalStorage()
    } else {
      console.log(`Board with id ${newBoard.id} already exists.`)
    }
  }

  const addNewBoards = (newBoards) => {
    newBoards.forEach((newBoard) => {
      addNewBoard(newBoard)
    })
    saveBoardsToLocalStorage()
  }
  const updateBoard = (updatedBoard) => {
    boards.value = getBoards()
    const index = boards.value.findIndex((board) => String(board.id) === String(updatedBoard.id));
    if (index !== -1) {
      boards.value.splice(index, 1, { ...boards.value[index], ...updatedBoard }); 
      console.log(`Board with id ${updatedBoard.id} has been updated.`);
      saveBoardsToLocalStorage(); 
    } else {
      console.log(`Board with id ${updatedBoard.id} not found.`);
    }
  }
  

  const removeBoard = (deleteBoardID) => {
    console.log(deleteBoardID)
    boards.value = getBoards()
    console.log("before boards.value" ,boards.value)
    console.log("before boards.value filter", 
      boards.value.filter(
        (board) => String(board.id) !== String(deleteBoardID)
      )
    )
    if (deleteBoardID) {
      boards.value = boards.value.filter(
        (board) => String(board.id) !== String(deleteBoardID)
      )
      console.log(
        boards.value.filter(
          (board) => String(board.id) !== String(deleteBoardID)
        )
      )
      console.log("boards.value", boards.value)
    }
    saveBoardsToLocalStorage()
  }

  const resetBoard = () => {
    boards.value = []
    saveBoardsToLocalStorage()
  }

  const getBoards = () => {
    const storedBoards = localStorage.getItem("boards")
    return storedBoards ? JSON.parse(storedBoards) : []
  }

  const setBoards = (newBoards) => {
    boards.value = newBoards
    localStorage.setItem("boards", JSON.stringify(boards.value))
  }

  //--------------------------------- Collabs -------------------------

  const setCollabs = (newCollabs) => {
    collabs.value = newCollabs
    localStorage.setItem("collabs", JSON.stringify(collabs.value))
  }

  const getCollabs = () => {
    const storedCollabs = localStorage.getItem("collabs")
    return storedCollabs ? JSON.parse(storedCollabs) : []
  }

  const saveCollabsToLocalStorage = () => {
    localStorage.setItem("collabs", JSON.stringify(boards.value))
  }

  const addNewCollab = (newCollab) => {
    collabs.value.push({ ...newCollab })
    saveCollabsToLocalStorage()
  }
  const addNewCollabs = (newCollabs) => {
    newCollabs.forEach((newCollab) => {
      addNewBoard(newCollab)
    })
    saveCollabsToLocalStorage()
  }
  const editCollab = (editedCollab) => {
    collabs.value = { ...editedCollab }
    setCollabs(collabs.value)
  }

  const removeCollabs = (deleteCollabID) => {
    collabs.value = collabs.value.filter(
      (collab) => collab.id !== deleteCollabID
    )
    saveCollabsToLocalStorage()
  }

  const resetCollabs = () => {
    collabs.value = []
    saveCollabsToLocalStorage()
  }

  return {
    setChangeColor,
    getChangeColor,
    setBoards,
    setCollabs,
    getCollabs,
    getBoards,
    addNewBoard,
    addNewBoards,
    removeBoard,
    resetBoard,
    addNewCollab,
    addNewCollabs,
    editCollab,
    removeCollabs,
    resetCollabs,
    updateBoard
  }
})

export { useBoard }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useBoard, import.meta.hot))
}
