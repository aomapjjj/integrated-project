import { createRouter, createWebHistory } from "vue-router";
import TaskList from "@/views/TaskList.vue";
import ErrorPage from "@/views/ErrorPage.vue";
import AddTask from "@/views/AddTask.vue";
import StatusesList from "@/views/StatusesList.vue";
import TaskDetail from "@/views/TaskDetail.vue";
import EditTask from "@/views/EditTask.vue";
import Login from "@/views/Login.vue";
import Board from "@/views/Board.vue";

const getToken = () => sessionStorage.getItem("access_token");
const getRefreshToken = () => sessionStorage.getItem("refresh_token");

const routes = [
  { path: "/", redirect: { name: "Login" } },
  {
    path: "/board/:id/status/:statusid/edit",
    name: "EditStatus",
    component: StatusesList,
    beforeEnter: async (to, from, next) => {
      const { id: boardId, statusid: statusId } = to.params;
      try {
        const token = getToken();
        const response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/statuses/${statusId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        response.ok ? next() : next({ name: "ErrorPage" });
      } catch (error) {
        console.error("Error checking board id:", error);
        next({ name: "ErrorPage" });
      }
    }
  },
  {
    path: "/board/:id",
    name: "TaskList",
    component: TaskList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const { id: boardId } = to.params;
      try {
        const token = getToken();
        const response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        response.ok ? next() : next({ name: "ErrorPage" });
      } catch (error) {
        console.error("Error checking board id:", error);
        next({ name: "ErrorPage" });
      }
    },
    children: [
      { path: "task/add", name: "AddTask", component: AddTask },
      { path: "task/:taskid", name: "TaskDetail", component: TaskDetail },
      { path: "task/:taskid/edit", name: "TaskEdit", component: EditTask }
    ]
  },
  { path: "/error", name: "ErrorPage", component: ErrorPage },
  {
    path: "/board/:id/status",
    name: "StatusesList",
    component: StatusesList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const { id: boardId } = to.params;
      try {
        const token = getToken();
        const response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/statuses`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        response.ok ? next() : next({ name: "ErrorPage" });
      } catch (error) {
        console.error("Error checking board id:", error);
        next({ name: "ErrorPage" });
      }
    },
    children: [{ path: "add", name: "AddStatus", component: StatusesList }]
  },
  { path: "/login", name: "Login", component: Login },
  { path: "/board", name: "Board", component: Board },
  { path: "/board/add", name: "BoardAdd", component: Board },
  { path: "/:pathMatch(.*)*", redirect: { name: "ErrorPage" } }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach(async (to, from, next) => {
  const accessToken = getToken();
  const refreshToken = getRefreshToken();

  setInterval(() => {
    router.go(0) 
  }, 30 * 60 * 1000);

  if (!accessToken && to.name !== "Login") {
    return next({ name: "Login" });
  }

  if (!accessToken) {
    return next();
  }

  try {
    const validateResponse = await validateAccessToken(accessToken);

    if (validateResponse.status === 200) {
      return next(); 
    }

    if (validateResponse.status === 401 && refreshToken) {
      return handleTokenRefresh(refreshToken, next);
    }

    handleInvalidTokens(next);

  } catch (error) {
    console.error("Error validating token:", error);
    return handleInvalidTokens(next);
  }
});

const handleTokenRefresh = async (refreshToken, next) => {
  try {
    const refreshResponse = await refreshAccessToken(refreshToken);

    if (refreshResponse.status === 200) {
      const refreshData = await refreshResponse.json();
      sessionStorage.setItem("access_token", refreshData.access_token);
      return next(); 
    }
    handleInvalidTokens(next);
  } catch (error) {
    console.error("Error validating refresh token:", error);
    handleInvalidTokens(next);
  }
};

const handleInvalidTokens = (next) => {
  sessionStorage.removeItem("access_token");
  next({ name: "Login" });
};

const validateAccessToken = async (token) => {
  const response = await fetch(`${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/validate-token`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    }
  });
  return response;
};

const refreshAccessToken = async (refreshToken) => {
  const response = await fetch(`${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/token`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${refreshToken}`
    }
  });
  return response;
};

export default router;