import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useTasks = defineStore('tasks', () => {
  
  const tasks = ref([])

  const getTasks = () => {
    return tasks.value
  }

  //actions
  const addTasks = (newTasks) => {
    newTasks?.forEach((newTask) =>
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
    removeTask,
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}