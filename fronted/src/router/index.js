import { createRouter, createWebHistory } from "vue-router";
import TaskList from "@/views/TaskList.vue";
import ErrorPage from "@/views/ErrorPage.vue";
import AddTask from "@/views/AddTask.vue";
import StatusesList from "@/views/StatusesList.vue";
import TaskDetail from "@/views/TaskDetail.vue";
import EditTask from "@/views/EditTask.vue";
import Login from "@/views/Login.vue";
import { useUsers } from "@/stores/storeUser";

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


router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem("access_token");
  console.log("Token on Navigation:", token);

  if (!token && to.name !== "Login") {
    next({ name: "Login" });
  } else if (token) {
    try {
      const validateResponse = await fetch(`${import.meta.env.VITE_BASE_URL_MAIN}/validate-token`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      console.log("Validate Response:", validateResponse.status);

      if (validateResponse.status === 200) {
        next();
      } else if (validateResponse.status === 401) {
        localStorage.removeItem("access_token");
        next({ name: "Login" });
      } else {
        next({ name: "Login" });
      }
    } catch (error) {
      console.error("Error validating token:", error);
      next({ name: "Login" });
    }
  } else {
    next();
  }
});



export default router;
