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
import { getBoardById } from "../libs/fetchUtils.js"
import CollabManagement from "@/views/boards/CollabManagement.vue"
const getToken = () => localStorage.getItem("access_token")
const getRefreshToken = () => localStorage.getItem("refresh_token")

const routes = [
  { path: "/", redirect: { name: "Login" } },
  {
    path: "/board/:id/status/:statusid/edit",
    name: "EditStatus",
    component: StatusesList,
    beforeEnter: async (to, from, next) => {
      const { id: boardId, statusid: statusId } = to.params
      try {
        const token = getToken()
        const response = await fetch(
          `${
            import.meta.env.VITE_BASE_URL_MAIN
          }/boards/${boardId}/statuses/${statusId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        )

        if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.ok) {
          next()
        }
        if (response.status === 401) {
          next({ name: "Login" })
        }
      } catch (error) {
        console.error("Error checking board id:", error)
      }
    }
  },
  {
    path: "/board/:id",
    name: "TaskList",
    component: TaskList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const { id: boardId } = to.params

      const userNameBoard = await getBoardById(boardId)

      if (userNameBoard.status === 404) {
        return next({ name: "ErrorPage" })
      }
      // ตรวจสอบการมีข้อมูลจาก userNameBoard
      if (!userNameBoard || !userNameBoard.item) {
        return next({ name: "ErrorPagePermission" }) // ถ้าไม่สามารถดึงข้อมูลได้
      }
      const userNameString = localStorage?.getItem("user")
      console.log('localStorage?.getItem("user")', userNameString)

      const userName = userNameString ? JSON.parse(userNameString) : null

      const loginUsername = () => {
        if (userName) {
          return userName.username // แสดงค่า username
        } else {
          return null
        }
      }

      const currentUsername = loginUsername()

      // ตรวจสอบความเป็นเจ้าของบอร์ด
      if (userNameBoard.item.owner.name !== currentUsername) {
        // ถ้าบอร์ดเป็น Private ให้ไปหน้า ErrorPagePermission
        // if (userNameBoard.item.visibility === "PRIVATE") {
        //   return next({ name: "ErrorPagePermission" })
        // }
      } else {
        console.log("ตรงกันนะจ๊า")
        return next()
      }

      try {
        let response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
        )

        // if (response.status === 404) {
        //   return next({ name: "ErrorPage" })
        // }

        // if (response.status === 403) {
        //   return next({ name: "ErrorPagePermission" })
        // }

        const board = await response.json()

        // ถ้าบอร์ดเป็น Public ก็ให้ไปต่อได้
        if (board.visibility === "PUBLIC") {
          return next()
        }

        const token = getToken()
        if (!token) {
          return next({ name: "Login" })
        }

        response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        )

        if (response.status === 401) {
          return next({ name: "Login" })
        }
        if (response.status === 403) {
          return next({ name: "ErrorPagePermission" })
        }

        if (response.ok) {
          return next()
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        return next({ name: "ErrorPagePermission" })
      }
    },
    children: [
      { path: "task/add", name: "AddTask", component: AddTask },
      { path: "task/:taskid", name: "TaskDetail", component: TaskDetail },
      {
        path: "task/:taskid/edit",
        name: "TaskEdit",
        component: EditTask,
        beforeEnter: async (to, from, next) => {
          const { id: boardId, taskid: taskId } = to.params
          try {
            const token = getToken()
            const response = await fetch(
              `${
                import.meta.env.VITE_BASE_URL_MAIN
              }/boards/${boardId}/tasks/${taskId}`,
              { headers: { Authorization: `Bearer ${token}` } }
            )

            if (response.status === 404) {
              next({ name: "ErrorPage" })
            } else if (response.ok) {
              next()
            }
            if (response.status === 401) {
              next({ name: "Login" })
            }
          } catch (error) {
            console.error("Error checking board id:", error)
          }
        }
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
      try {
        const token = getToken()
        const response = await getResponseItems(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/statuses`
        )

        if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.ok) {
          next()
        }
      } catch (error) {
        next()
      }

      const userNameBoard = await getBoardById(boardId)

      // ตรวจสอบการมีข้อมูลจาก userNameBoard
      if (!userNameBoard || !userNameBoard.item) {
        return next({ name: "ErrorPagePermission" }) // ถ้าไม่สามารถดึงข้อมูลได้
      }
      const userNameString = localStorage?.getItem("user")
      console.log('localStorage?.getItem("user")', userNameString)

      const userName = userNameString ? JSON.parse(userNameString) : null

      const loginUsername = () => {
        if (userName) {
          return userName.username // แสดงค่า username
        } else {
          return null
        }
      }
      const currentUsername = loginUsername()

      // ตรวจสอบความเป็นเจ้าของบอร์ด
      if (userNameBoard.item.owner.name !== currentUsername) {
        // ถ้าบอร์ดเป็น Private ให้ไปหน้า ErrorPagePermission
        if (userNameBoard.item.visibility === "PRIVATE") {
          return next({ name: "ErrorPagePermission" })
        }
      } else {
        console.log("ตรงกันนะจ๊า")
        return next()
      }

      try {
        let response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
        )

        if (response.status === 404) {
          return next({ name: "ErrorPage" })
        }

        if (response.status === 403) {
          return next({ name: "ErrorPagePermission" })
        }

        const board = await response.json()

        // ถ้าบอร์ดเป็น Public ก็ให้ไปต่อได้
        if (board.visibility === "PUBLIC") {
          return next()
        }

        const token = getToken()
        if (!token) {
          return next({ name: "Login" })
        }

        response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        )

        if (response.status === 401) {
          return next({ name: "Login" })
        }
        if (response.status === 403) {
          return next({ name: "ErrorPagePermission" })
        }

        if (response.ok) {
          return next()
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        return next({ name: "ErrorPagePermission" })
      }
    },

    children: [{ path: "add", name: "AddStatus", component: StatusesList }]
  },
  { path: "/login", name: "Login", component: Login },
  { path: "/board", name: "Board", component: Board },
  { path: "/board/add", name: "BoardAdd", component: Board },
  { path: "/:pathMatch(.*)*", redirect: { name: "ErrorPage" } },
  { path: "/board/:id/collab", name: "Collab", component: CollabManagement }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const accessToken = getToken()
  const refreshToken = getRefreshToken()

  // Refresh the page every 30 minutes
  setInterval(() => {
    router.go(0)
  }, 30 * 60 * 1000)

  // Handle routes with a board ID (public/private access)
  if (to.name === "board" && to.params.id) {
    const boardId = to.params.id

    try {
      // Fetch the board without authorization header first
      const boardResponse = await fetch(
        `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
      )
      const board = await boardResponse.json()

      if (boardResponse.status === 404) {
        console.error("Board not found.")
        return next({ name: "ErrorPage" })
      }

      // If the board is private, check for access token
      if (board.visibility === "PRIVATE") {
        if (!accessToken) {
          return next({ name: "ErrorPagePermission" })
        }

        // Fetch the private board with authorization
        const privateResponse = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: { Authorization: `Bearer ${accessToken}` }
          }
        )

        if (privateResponse.status === 401 && refreshToken) {
          return handleTokenRefresh(refreshToken, next)
        }

        if (privateResponse.status === 401) {
          return handleInvalidTokens(next)
        }

        return next()
      }

      return next()
    } catch (error) {
      console.error("Error fetching board data:", error)
      return next({ name: "Login" })
    }
  }

  if (!accessToken) {
    return next()
  }

  try {
    const validateResponse = await validateAccessToken(accessToken)

    if (validateResponse.status === 200) {
      return next()
    }

    if (validateResponse.status === 401 && refreshToken) {
      return handleTokenRefresh(refreshToken, next)
    }

    return handleInvalidTokens(next)
  } catch (error) {
    console.error("Error validating token:", error)
    return handleInvalidTokens(next)
  }
})

const handleTokenRefresh = async (refreshToken, next) => {
  try {
    const refreshResponse = await refreshAccessToken(refreshToken)
    if (refreshResponse.status === 200) {
      const refreshData = await refreshResponse.json()
      localStorage.setItem("access_token", refreshData.access_token)
      return next()
    }
    handleInvalidTokens(next)
  } catch (error) {
    console.error("Error refreshing token:", error)
    handleInvalidTokens(next)
  }
}

const handleInvalidTokens = (next) => {
  localStorage.removeItem("access_token")
  next({ name: "Login" })
}

const validateAccessToken = async (token) => {
  const response = await fetch(
    `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/validate-token`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      }
    }
  )
  return response
}

const refreshAccessToken = async (refreshToken) => {
  const response = await fetch(
    `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/token`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${refreshToken}`
      }
    }
  )
  return response
}

export default router
