import { createRouter, createWebHistory } from "vue-router"
import TodoList from "@/views/TodoList.vue"

export const routes = [
  {
    path: '/',
    redirec: '/task',
    name: 'TodoList',
    component: TodoList,
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_BASE_URL),
  routes,
})

export default router
