import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"

const useTasks = defineStore("tasks", () => {
  const tasks = ref([])

  const getTasks = () => {
    return tasks.value
  }

  // actions
  const addTasks = (newTasks) => {
    newTasks?.forEach((newTask) =>
      addTask(
        newTask.id,
        newTask.title,
        newTask.description,
        newTask.assignees,
        newTask.status,
        newTask.createdOn,
        newTask.updateOn,
        newTask.attachments
      )
    )
  }

  const addTask = (
    id,
    title,
    description,
    assignees,
    status,
    createdOn,
    updateOn,
    attachments = []
  ) => {
    tasks.value.push({
      id: id,
      title: title,
      description: description,
      assignees: assignees,
      status: status,
      createdOn: createdOn,
      updateOn: updateOn,
      attachments: attachments,
    })
  }

  const updateTask = (id, updates) => {
    tasks.value = tasks.value.map((task) =>
      task.id === id ? { ...task, ...updates } : task
    )
  }
  
  const updateAttachments = (id, newAttachments) => {
    const task = tasks.value.find((task) => task.id === id)
    if (task) {
      task.attachments = [...newAttachments]
    } else {
      console.error(`Task with ID ${id} not found.`)
    }
  }

  const removeTask = (removeId) => {
    const index = tasks.value.findIndex((task) => task.id === removeId)
    if (index !== -1) {
      tasks.value.splice(index, 1)
    } else {
      console.error(`Task with ID ${removeId} not found.`)
    }
  }
  

  const getAttachmentsByTaskId = (taskId) => {
    const task = tasks.value.find((task) => task.id === taskId)
    return task?.attachments || []
  }


const clearTasks = () => {
  tasks.value = []
}


  return {
    getTasks,
    addTasks,
    addTask,
    updateTask,
    updateAttachments,
    removeTask,
    getAttachmentsByTaskId,
    clearTasks
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}
