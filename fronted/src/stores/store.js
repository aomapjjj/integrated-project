import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref , computed } from 'vue'


const useTasks = defineStore('tasks', () => {
  
  const tasks = ref([])
  const limit = ref({})

  const getTasks = () => {
    return tasks.value
  }

  const getLimit = () => {
    return limit.value
  }

  const isLimitEnabled = ref(false)
  const maxTasks = ref(10)
  const getIsLimitEnabled = computed(() => isLimitEnabled.value)
  const getMaxTasks = computed(() => maxTasks.value)


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

  const addLimits = (newLimits) => {
    newLimits.forEach((newLimit) => ({
      id: newLimit.id,
      maximumTask: newLimit.maximumTask,
      isLimit: newLimit.isLimit,
    }));
  }

  const addLimit = (id, maximumTask, isLimit) => {
    limit.value.push({
      id: id,
      maximumTask: maximumTask,
      isLimit: isLimit,
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
  const setLimitEnabled = (enabled) => {
    isLimitEnabled.value = enabled
  }

  const setMaxTasks = (limit) => {
    maxTasks.value = limit
  }
  return {
    getTasks,
    addTasks,
    addTask,
    updateTask,
    findIndexTodo,
    findTodo,
    removeTask,
    setLimitEnabled,
    setMaxTasks,
    getIsLimitEnabled,
    getMaxTasks,
    getLimit,
    addLimit,
    addLimits,
  }
})

export { useTasks }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useTasks, import.meta.hot))
}