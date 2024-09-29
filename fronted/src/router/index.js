import { createRouter, createWebHistory } from "vue-router"
import TaskList from "@/views/TaskList.vue"
import ErrorPage from "@/views/ErrorPage.vue"
import AddTask from "@/views/AddTask.vue"
import StatusesList from "@/views/StatusesList.vue"
import TaskDetail from "@/views/TaskDetail.vue"
import EditTask from "@/views/EditTask.vue"
import Login from "@/views/Login.vue"
import Board from "@/views/Board.vue"
import { getBoardById } from "../libs/fetchUtils.js"
const getToken = () => sessionStorage.getItem("access_token")
const getRefreshToken = () => sessionStorage.getItem("refresh_token")

const userNameString = sessionStorage?.getItem("user")
console.log('sessionStorage?.getItem("user")', userNameString)

// แปลง JSON string เป็น object
const userName = userNameString ? JSON.parse(userNameString) : null

// เข้าถึง username

const loginUsername = () => {
  if (userName) {
    return userName.username // แสดงค่า username
  } else {
    return null
  }
}

console.log(loginUsername())

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
        next({ name: "Login" })
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
  
      const userNameBoard = await getBoardById(boardId);
  
      // ตรวจสอบการมีข้อมูลจาก userNameBoard
      if (!userNameBoard || !userNameBoard.item) {
        return next({ name: "ErrorPage" }); // ถ้าไม่สามารถดึงข้อมูลได้
      }
  
      const currentUsername = loginUsername();
  
      // ตรวจสอบความเป็นเจ้าของบอร์ด
      if (userNameBoard.item.owner.name !== currentUsername) {
        // ถ้าบอร์ดเป็น Private ให้ไปหน้า ErrorPagePermission
        if (userNameBoard.item.visibility === "PRIVATE") {
          return next({ name: "ErrorPagePermission" });
        }
      } else {
        console.log("ตรงกันนะจ๊า");
        return next();
      }
  
      try {
        let response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
        );


  
        if (response.status === 404) {
          return next({ name: "ErrorPage" });
        }
  
        if (response.status === 403) {
          return next({ name: "ErrorPagePermission" });
        }
  
        const board = await response.json();
  
        // ถ้าบอร์ดเป็น Public ก็ให้ไปต่อได้
        if (board.visibility === "PUBLIC") {
          return next();
        }
  
        const token = getToken();
        if (!token) {
          return next({ name: "Login" });
        }
  
        response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
  
        if (response.status === 401) {
          return next({ name: "Login" });
        }
        if (response.status === 403) {
          return next({ name: "ErrorPagePermission" });
        }
  
        if (response.ok) {
          return next();
        }
      } catch (error) {
        console.error("Error checking board id:", error);
        return next({ name: "ErrorPagePermission" });
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
      const { id: boardId } = to.params

      try {
        // เรียก API เพื่อตรวจสอบบอร์ดโดยไม่ส่ง token
        let response = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
        )

        // ตรวจสอบสถานะของบอร์ด
        if (response.status === 404) {
          return next({ name: "ErrorPage" })
        }

        const board = await response.json()

        // ถ้า board เป็น PUBLIC ให้ไปต่อได้เลย
        if (board.visibility === "PUBLIC") {
          return next()
        }

        // ถ้า board เป็น PRIVATE ต้องตรวจสอบ token
        const token = getToken()
        if (!token) {
          return next({ name: "Login" })
        }

        // เรียก API อีกครั้งพร้อมกับ token
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
  { path: "/:pathMatch(.*)*", redirect: { name: "ErrorPage" } }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})
router.beforeEach(async (to, from, next) => {
  const accessToken = getToken()
  const refreshToken = getRefreshToken()

  // เช็คว่า route เป็น board และมี id หรือไม่
  if (to.name === "board" && to.params.id) {
    const boardId = to.params.id

    try {
      const token = getToken()

      // เรียก API เพื่อดึงข้อมูลบอร์ด โดยไม่ส่ง Authorization header ก่อน
      const response = await fetch(
        `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`
      )

      const board = await response.json()

      if (response.status === 404) {
        console.error("Board not found.")
        return
      }

      // ถ้า board เป็น Private และต้องการ token ให้เรียก API ใหม่พร้อมส่ง token
      if (board.visibility === "PRIVATE") {
        if (!token) {
          // ถ้าไม่มี token ให้ส่งไปที่หน้า login
          return next({ name: "Login" })
        }

        const privateResponse = await fetch(
          `${import.meta.env.VITE_BASE_URL_MAIN}/boards/${boardId}`,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        )

        if (privateResponse.status === 401) {
          return next({ name: "Login" })
        }

        return next()
      }

      // ถ้าเป็น Public ให้ไปต่อได้เลย
      next()
    } catch (error) {
      console.error("Error fetching board data:", error)
      return next({ name: "ErrorPagePermission" })
    }
  } else {
    // สำหรับ route อื่นๆ ให้ไปต่อได้เลย
    return next()
  }
})

const handleTokenRefresh = async (refreshToken, next) => {
  try {
    const refreshResponse = await refreshAccessToken(refreshToken)
    if (refreshResponse.status === 200) {
      const refreshData = await refreshResponse.json()
      sessionStorage.setItem("access_token", refreshData.access_token)
      return next()
    }
    handleInvalidTokens(next)
  } catch (error) {
    console.error("Error validating refresh token:", error)
    handleInvalidTokens(next)
  }
}

const handleInvalidTokens = (next) => {
  sessionStorage.removeItem("access_token")
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
