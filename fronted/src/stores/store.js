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
  const updateTask = (id, title, description, assignees, status, createdOn, updateOn) => {
    tasks.value = tasks.value.map((task) => {
      return task.id === id
        ? { ...task, title: title,
          description: description,
          assignees: assignees,
          status: status,
          createdOn: createdOn,
          updateOn: updateOn }
        : task
    })
  }
  const findTodo = (searchId) => {
    return todos.value.find((todo) => todo.id === searchId)
  }
  const findIndexTodo = (searchId) => {
    return todos.value.findIndex((todo) => todo.id === searchId)
  }
  const removeTask = (removeId) => {
    tasks.value.splice(
      tasks.value.findIndex((Task) => Task.id === removeId),
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
    removeTask
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}