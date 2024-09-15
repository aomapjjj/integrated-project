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
  return localStorage.getItem("access_token")
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
          // ถ้า id ถูกต้อง ให้ทำการโหลดเส้นทางต่อไป
          next()
        } else {
          next({ name: "ErrorPage" })
        }
      } catch (error) {
        // ถ้ามีข้อผิดพลาดในการ fetch (เช่น API ล่ม)
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
        component: EditTask
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
    children: [
      {
        path: ":statusid/edit",
        name: "EditStatus",
        component: StatusesList
      },
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
  const token = localStorage.getItem("access_token")
  console.log("Token on Navigation:", token)

  if (!token && to.name !== "Login") {
    next({ name: "Login" })
  } else if (token) {
    try {
      const validateResponse = await fetch(
        `${import.meta.env.VITE_BASE_URL_MAIN}/validate-token`,
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
        localStorage.removeItem("access_token")
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
