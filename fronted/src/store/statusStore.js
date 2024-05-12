import { defineStore } from 'pinia'
import axios from 'axios' // แก้ไขตรงนี้

export const useTodo = defineStore('todo', {
  state: () => ({
    list: [],
    selectTodo: {},
    statuses: ['NO_STATUS','TO_DO','DOIND','DONE']
  }),
  actions: {
    async loadTodos() {
      try {
        const response = await axios.get(process.env.VITE_BASE_URL) // แก้ไขเป็น axios.get
        this.list = response.data
      } catch (error) {
        console.error('Failed to load todo:', error)
      }
    }
  },
})
