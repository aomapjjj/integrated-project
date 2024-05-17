import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useTasks = defineStore('tasks', () => {
  //state
  const tasks = ref([])
  //view
  const getTasks = () => {
    return tasks.value
  }
  //actions
  const addTasks = (newTasks) => {
    newTasks.forEach((newTask) =>
      addTask(  newTask.id,
                newTask.title, 
                newTask.description, 
                newTask.assignees,
                newTask.status,
                newTask.createdOn,
                newTask.updateOn,
            )
    )
  }
  const addTask = (id, title, description, assignees, status, createdOn, updateOn) => {
    tasks.value.push({
      id: id,
      title: title,
      description: description,
      assignees: assignees,
      status: status,
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
  const removeTodo = (removeId) => {
    todos.value.splice(
      todos.value.findIndex((todo) => todo.id === removeId),
      1
    )
  }
  return {
    getTasks,
    addTasks,
    addTask,
    updateTask,
    findIndexTodo,
    findTodo,
    removeTodo
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}