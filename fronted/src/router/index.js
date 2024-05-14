import { createRouter, createWebHistory } from "vue-router"
import TaskList from "@/views/TaskList.vue"
import ErrorPage from "@/views/ErrorPage.vue"
import AddTask from "@/views/AddTask.vue"
import StatusesList from "@/views/StatusesList.vue"


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
  {
    path: '/task',
    name: 'AddTask',
    component: AddTask,
  },
  {
    path: '/v2/status',
    name: 'StatusesList',
    component: StatusesList,
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes: [
//     {
//       path: '/',
//       redirect: '/task'
//     },
//     {
//       path: '/task',
//       name: 'TaskList',
//       component: TaskList,
//     },
//   ]
// })

export default router
