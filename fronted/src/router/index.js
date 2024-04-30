import { createRouter, createWebHistory } from "vue-router"
import TaskList from "@/views/TaskList.vue"
import ErrorPage from "@/views/ErrorPage.vue"

export const routes = [
  {
    path: '/',
    redirect: { name: "TaskList" },
  },
  {
    path: '/task',
    name: 'TaskList',
    component: TaskList,
  },
  // {
  //   path: '/:catchAll(.*)',
  //   redirect: { name: "TaskList" },
  // },
  {
    path: '/task/:id',
    name: 'TaskDetail',
    component: TaskList,
  },
  {
    path: '/task/error',
    name: 'ErrorPage',
    component: ErrorPage,
  },
  
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
