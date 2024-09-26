import { createRouter, createWebHistory } from "vue-router"
import TaskList from "@/views/TaskList.vue"
import ErrorPage from "@/views/ErrorPage.vue"
import AddTask from "@/views/AddTask.vue"
import StatusesList from "@/views/StatusesList.vue"
import TaskDetail from "@/views/TaskDetail.vue"
import EditTask from "@/views/EditTask.vue"
import Login from "@/views/Login.vue"
import Board from "@/views/Board.vue"

function getToken() {
  return sessionStorage.getItem("access_token")
}

export const routes = [
  {
    path: "/",
    redirect: { name: "Login" }
  },
  {
    path: "/board/:id/status/:statusid/edit",
    name: "EditStatus",
    component: StatusesList,
    beforeEnter: async (to, from, next) => {
      const boardId = to.params.id
      const statusId = to.params.statusid
      try {
        const token = getToken()
        const response = await fetch(
          `${
            import.meta.env.VITE_BASE_URL_MAIN
          }/boards/${boardId}/statuses/${statusId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        )

        if (response.ok) {
          next()
        } else if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.status === 401) {
          next({ name: "Login" })
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        next({ name: "ErrorPage" })
      }
    }
  },
  {
    path: "/board/:id/task/:taskid/edit",
    name: "TaskEdit",
    component: EditTask,
    beforeEnter: async (to, from, next) => {
      const boardId = to.params.id
      const taskId = to.params.taskid
      try {
        const token = getToken()
        const response = await fetch(
          `${
            import.meta.env.VITE_BASE_URL_MAIN
          }/boards/${boardId}/tasks/${taskId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        )

        if (response.ok) {
          next({ name: "EditTask" })
        } else if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.status === 401) {
          next({ name: "Login" })
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        next({ name: "ErrorPage" })
      }
    }
  },
  {
    path: "/board/:id",
    name: "TaskList",
    component: TaskList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const boardId = to.params.id
      try {
        const token = getToken()
        const response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        )

        if (response.ok) {
          next()
        } else if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.status === 401) {
          next({ name: "Login" })
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        next({ name: "ErrorPage" })
      }
    },
    children: [
      {
        path: "task/add",
        name: "AddTask",
        component: AddTask
      },
      {
        path: "task/:taskid",
        name: "TaskDetail",
        component: TaskDetail
      }
    ]
  },
  {
    path: "/error",
    name: "ErrorPage",
    component: ErrorPage
  },

  {
    path: "/board/:id/status",
    name: "StatusesList",
    component: StatusesList,
    props: true,
    beforeEnter: async (to, from, next) => {
      const boardId = to.params.id
      try {
        const token = getToken()
        const response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/statuses`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        )

        if (response.ok) {
          next()
        } else if (response.status === 404) {
          next({ name: "ErrorPage" })
        } else if (response.status === 401) {
          next({ name: "Login" })
        }
      } catch (error) {
        console.error("Error checking board id:", error)
        next({ name: "ErrorPage" })
      }
    },
    children: [
      {
        path: "add",
        name: "AddStatus",
        component: StatusesList
      }
    ]
  },
  {
    path: "/login",
    name: "Login",
    component: Login
  },
  {
    path: "/board",
    name: "Board",
    component: Board
  },
  {
    path: "/board/add",
    name: "BoardAdd",
    component: Board
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: { name: "ErrorPage" }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const accessToken = sessionStorage.getItem("access_token")
  const refreshToken = sessionStorage.getItem("refresh_token")

  if (!accessToken && to.name !== "Login") {
    next({ name: "Login" })
  } else if (accessToken) {
    try {
      const validateResponse = await fetch(
        `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/validate-token`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`
          }
        }
      )

      console.log("Validate Response:", validateResponse.status)

      if (validateResponse.status === 200) {
        next() 
      } else if (validateResponse.status === 401 && refreshToken) {
        try {
          const refreshValidateResponse = await fetch(
            `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/validate-token`,
            {
              method: "GET",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${refreshToken}`
              }
            }
          )

          if (refreshValidateResponse.status === 200) {
            const refreshData = await refreshValidateResponse.json()

            sessionStorage.setItem("access_token", refreshData.access_token)
            sessionStorage.setItem("refresh_token", refreshData.refresh_token)
            next()
          } else {
            sessionStorage.removeItem("access_token")
            sessionStorage.removeItem("refresh_token")
            next({ name: "Login" })
          }
        } catch (refreshError) {
          console.error("Error validating refresh token:", refreshError)
          sessionStorage.removeItem("access_token")
          sessionStorage.removeItem("refresh_token")
          next({ name: "Login" })
        }
      } else {
        sessionStorage.removeItem("access_token")
        sessionStorage.removeItem("refresh_token")
        next({ name: "Login" })
      }
    } catch (error) {
      console.error("Error validating token:", error)
      next({ name: "Login" })
    }
  } else {
    next()
  }
})

export default router
