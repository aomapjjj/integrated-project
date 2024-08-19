import { createRouter, createWebHistory } from "vue-router";
import TaskList from "@/views/TaskList.vue";
import ErrorPage from "@/views/ErrorPage.vue";
import AddTask from "@/views/AddTask.vue";
import StatusesList from "@/views/StatusesList.vue";
import TaskDetail from "@/views/TaskDetail.vue";
import EditTask from "@/views/EditTask.vue";
import Login from "@/views/Login.vue";

export const routes = [
  {
    path: "/",
    redirect: { name: "Login" },
  },
  {
    path: "/task",
    name: "TaskList",
    component: TaskList,
    props: true,
    children: [
      {
        path: "add",
        name: "AddTask",
        component: AddTask,
      },
      {
        path: "/task/:id",
        name: "TaskDetail",
        component: TaskDetail,
      },
      {
        path: "/task/:id/edit",
        name: "TaskEdit",
        component: EditTask,
      },
    ],
  },
  {
    path: "/:catchAll(.*)",
    redirect: { name: "TaskList" },
  },
  {
    path: "/task/error",
    name: "ErrorPage",
    component: ErrorPage,
  },
  {
    path: "/status",
    name: "StatusesList",
    component: StatusesList,
    children: [
      {
        path: "/status/:id/edit",
        name: "EditStatus",
        component: StatusesList,
      },
    ],
  },
  {
    path: "/login",
    name: "Login", 
    component: Login,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
