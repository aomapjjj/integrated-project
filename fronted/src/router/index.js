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
        } else if(response.status === 404) {
          next({ name: "ErrorPage" })
        }
        else if (response.status === 401){
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
      },
      {
        path: "task/:taskid/edit",
        name: "TaskEdit",
        component: EditTask,
        props: true,
        beforeEnter: async (to, from, next) => {
          const boardId = to.params.id
          const taskId = to.params.taskid
          try {
            const token = getToken()
            const response = await fetch(
              `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/tasks/${taskId}`,
              {
                headers: {
                  Authorization: `Bearer ${token}`
                }
              }
            )
      
            console.log("Response status:", response.status);  // Debug จุดนี้
      
            if (response.ok) {
              next()
            } else if (response.status === 404) {
              next({ name: "ErrorPage" })
            } else if (response.status === 401) {
              next({ name: "Login" })
            } else {
              next({ name: "ErrorPage" })
            }
          } catch (error) {
            console.error("Error checking task id:", error)
            next({ name: "ErrorPage" })
          }
        }
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
        } else if(response.status === 404) {
          next({ name: "ErrorPage" })
        }
        else if (response.status === 401){
          next({ name: "Login" })
        }
      } catch (error) {
        
        console.error("Error checking board id:", error)
        next({ name: "ErrorPage" })
      }
    },
    children: [
      {
        path: ":statusid/edit",
        name: "EditStatus",
        component: StatusesList,
        props: true,
        beforeEnter: async (to, from, next) => {
          const boardId = to.params.id
          const statusId = to.params.statusid
          try {
            const token = getToken()
            const response = await fetch(
              `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}/statuses/${statusId}`,
              {
                headers: {
                  Authorization: `Bearer ${token}`
                }
              }
            )
      
            console.log("Response status:", response.status);  // Debug จุดนี้
      
            if (response.ok) {
              next()
            } else if (response.status === 404) {
              next({ name: "ErrorPage" })
            } else if (response.status === 401) {
              next({ name: "Login" })
            } else {
              next({ name: "ErrorPage" })
            }
          } catch (error) {
            console.error("Error checking status id:", error)
            next({ name: "ErrorPage" })
          }
        }
      }
      ,
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
  const token = sessionStorage.getItem("access_token")
 

  if (!token && to.name !== "Login") {
    next({ name: "Login" })
  } else if (token) {
    try {
      const validateResponse = await fetch(
        `${import.meta.env.
          VITE_BASE_URL_MAIN_LOGIN}/validate-token`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
          }
        }
      )

      console.log("Validate Response:", validateResponse.status)

      if (validateResponse.status === 200) {
        next()
      } else if (validateResponse.status === 401) {
        sessionStorage.removeItem("access_token")
        next({ name: "Login" })
      } else {
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
