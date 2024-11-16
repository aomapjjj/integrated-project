import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useTasks = defineStore('tasks', () => {
  
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

  const addTask = (id, title, description, assignees, status, createdOn, updateOn, attachments = []) => {
    tasks.value.push({
      id: id,
      title: title,
      description: description,
      assignees: assignees,
      status: status,
      createdOn: createdOn,
      updateOn: updateOn,
      attachments: attachments 
    })
  }

  const updateTask = (id, title, description, assignees, status, createdOn, updateOn, attachments) => {
    tasks.value = tasks.value.map((task) => {
      return task.id === id
        ? { ...task, 
            title: title,
            description: description,
            assignees: assignees,
            status: status,
            createdOn: createdOn,
            updateOn: updateOn,
            attachments: attachments || task.attachments 
          }
        : task
    })
  }

  const updateAttachments = (id, newAttachments) => {
    const task = tasks.value.find((task) => task.id === id);
    if (task) {
      task.attachments = [...task.attachments, ...newAttachments];
    } else {
      console.error(`Task with ID ${id} not found.`);
    }
  };
  

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
    updateAttachments, 
    removeTask,
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}
