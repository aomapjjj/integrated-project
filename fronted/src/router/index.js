import { createRouter, createWebHistory } from "vue-router"
import TaskList from "@/views/TaskList.vue"
import ErrorPage from "@/views/errorpage/NotFoundError.vue"
import AddTask from "@/views/tasks/AddTask.vue"
import StatusesList from "@/views/statuses/StatusesList.vue"
import TaskDetail from "@/views/tasks/TaskDetail.vue"
import EditTask from "@/views/tasks/EditTask.vue"
import Login from "@/views/Login.vue"
import Board from "@/views/boards/Board.vue"
import ErrorPagePermission from "@/views/errorpage/PermissionError.vue"
import {
  getBoardById,
  validateAccessToken,
  refreshAccessToken
} from "../libs/fetchUtils.js"
import CollabManagement from "@/views/boards/CollabManagement.vue"
import Invitations from "@/views/boards/Invitations.vue"
const getToken = () => localStorage.getItem("access_token")
const getRefreshToken = () => localStorage.getItem("refresh_token")

const getCurrentUser = () => {
  const userNameString = localStorage?.getItem("user")
  return userNameString ? JSON.parse(userNameString) : null
}

const checkAccessRight = async (boardId, requiredAccess) => {
  const board = await getBoardById(boardId)

  const currentUser = getCurrentUser()
  // if (!currentUser) return { error: "Login" }

  const currentUsername = currentUser?.username
  const { owner, collaborators, visibility } = board.item

 
  if (owner.name === currentUsername) return { hasAccess: true }

  const collaborator = collaborators.find(
    (collab) => collab.name === currentUsername
  )
  if (collaborator) {
    if (
      collaborator.accessRight === requiredAccess ||
      collaborator.accessRight === "WRITE"
    ) {
      return { hasAccess: true }
    }
    return { error: "ErrorPagePermission" }
  }

  if (visibility === "PUBLIC" && requiredAccess === "READ") {
    return { hasAccess: true }
  } else if (visibility === "PUBLIC" && requiredAccess === "WRITE") {
    return { error: "ErrorPagePermission" } 
  }

  return { error: "ErrorPagePermission" }
}

const checkWriteAccess = async (to, from, next) => {
  const { id: boardId } = to.params
  const accessCheck = await checkAccessRight(boardId, "WRITE")

  if (accessCheck.hasAccess) next()

  else next({ name: accessCheck.error })
}

const routes = [
  { path: "/", redirect: { name: "Login" } },

  {
    path: "/board/:id",
    name: "TaskList",
    component: TaskList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const { id: boardId } = to.params
      const accessCheck = await checkAccessRight(boardId, "READ")

      if (accessCheck.hasAccess) next()
      else next({ name: accessCheck.error })
    },
    children: [
      {
        path: "task/add",
        name: "AddTask",
        component: AddTask,
        beforeEnter: checkWriteAccess
      },
      { path: "task/:taskid", name: "TaskDetail", component: TaskDetail },
      {
        path: "task/:taskid/edit",
        name: "TaskEdit",
        component: EditTask,
        beforeEnter: checkWriteAccess
      },
      {
        path: "status/add",
        name: "AddStatus",
        component: StatusesList,
        beforeEnter: checkWriteAccess
      },
      {
        path: "status/:statusid/edit",
        name: "EditStatus",
        component: StatusesList,
        beforeEnter: checkWriteAccess
      }
    ]
  },
  { path: "/error", name: "ErrorPage", component: ErrorPage },
  {
    path: "/errorPermission",
    name: "ErrorPagePermission",
    component: ErrorPagePermission
  },
  {
    path: "/board/:id/status",
    name: "StatusesList",
    component: StatusesList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const { id: boardId } = to.params
      const accessCheck = await checkAccessRight(boardId, "READ")

      if (accessCheck.hasAccess) next()
      else next({ name: accessCheck.error })
    },
    children: [
      {
        path: "add",
        name: "AddStatus",
        component: StatusesList,
        beforeEnter: checkWriteAccess
      },
      {
        path: ":statusid/edit",
        name: "EditStatus",
        component: StatusesList,
        beforeEnter: checkWriteAccess
      }
    ]
  },
  { path: "/login", name: "Login", component: Login },
  {
    path: "/board",
    name: "Board",
    component: Board,
    beforeEnter: (to, from, next) => {
      if (!getToken()) {
        next({ name: "Login" })
      } else {
        next()
      }
    }
  },
  { path: "/board/add", name: "BoardAdd", component: Board },
  { path: "/:pathMatch(.*)*", redirect: { name: "ErrorPage" } },
  { path: "/board/:id/collab", name: "Collab", component: CollabManagement , 
    beforeEnter: async (to, from, next) => {
    const { id: boardId } = to.params
    const accessCheck = await checkAccessRight(boardId, "READ")

    if (accessCheck.hasAccess) next()
    else next({ name: accessCheck.error })
  },},
  {
    path: "/board/:id/collab/invitations",
    name: "Invitations",
    component: Invitations
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

const handleTokenRefresh = async (refreshToken, next) => {
  try {
    const refreshResponse = await refreshAccessToken(refreshToken);
    if (refreshResponse.status === 200) {
      const refreshData = await refreshResponse.json();
      localStorage.setItem("access_token", refreshData.access_token);
      return true
    }
    return false
  } catch (error) {
    console.error("Error refreshing token:", error);
    return false
  }
};

const checkAuthorization = async (next, accessToken, refreshToken) => {
  try {
    const validateResponse = await validateAccessToken(accessToken);
    if (validateResponse.status === 200) return true;

    if (validateResponse.status === 401 && refreshToken) {
      return await handleTokenRefresh(refreshToken, next);
    }
  } catch (error) {
    console.error("Error validating token:", error);
  }
  return false;
};


router.beforeEach(async (to, from, next) => {
  const accessToken = getToken();
  const refreshToken = getRefreshToken();
  
  if (to.name === "board" && to.params.id) {
    const boardId = to.params.id;
    try {
      const boardResponse = await fetch(`
        ${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
      );
      const board = await boardResponse.json();

      if (boardResponse.status === 404) return next({ name: "ErrorPage" });
      if (board.visibility === "PRIVATE" && !accessToken) {
        return next({ name: "ErrorPagePermission" });
      }
      const authorized = await checkAuthorization(next, accessToken, refreshToken);
      if (!authorized) return next({ name: "Login" });
      
      return next();
    } catch (error) {
      console.error("Error fetching board data:", error);
      return next({ name: "Login" });
    }
  }

  if (!accessToken) return next(); 
  const authorized = await checkAuthorization(next, accessToken, refreshToken);
  if (!authorized) return next({ name: "Login" });
  next();
})

export default router
