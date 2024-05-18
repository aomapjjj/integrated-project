import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useStatuses = defineStore('statuses', () => {
    //state
    const statuses = ref([])
    //view
    const getStatuses = () => {
      return statuses.value
    }
    //actions
    const addStatuses = (newStatuses) => {
      newStatuses.forEach((newStatus) =>
        addStatus(  newStatus.id,
                  newStatus.name, 
                  newStatus.description, 
                  newStatus.createdOn,
                  newStatus.updateOn,
              )
      )
    }
    const addStatus = (id, name, description, createdOn, updateOn) => {
      statuses.value.push({
        id: id,
        name: name,
        description: description,
        createdOn: createdOn,
        updateOn: updateOn,
      })
    }
    const updateTask = (id, category, description) => {
      todos.value = todos.value.map((todo) => {
        return todo.id === id
          ? { ...todo, category: category, description: description }
          : todo
      })
    }
    const findTodo = (searchId) => {
      return todos.value.find((todo) => todo.id === searchId)
    }
    const findIndexTodo = (searchId) => {
      return todos.value.findIndex((todo) => todo.id === searchId)
    }
    const removeStatus = (removeId) => {
      statuses.value.splice(
        statuses.value.findIndex((status) => status.id === removeId),
        1
      )
    }
    return {
      getStatuses,
      addStatuses,
      addStatus,
      updateTask,
      findIndexTodo,
      findTodo,
      removeStatus
    }
  })

  export { useStatuses }

  
if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useStatuses, import.meta.hot))
  }