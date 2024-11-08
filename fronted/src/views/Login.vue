<script setup>
import { jwtDecode } from "jwt-decode"
import { useRouter } from "vue-router"
import { ref, computed } from "vue"
import { useUsers } from "../stores/storeUser"
import { useBoard  } from "@/stores/storeBoard"
import { getItems , getBoardItems } from "../libs/fetchUtils.js"

// ----------------------- Router -----------------------

const router = useRouter()

// ----------------------- Alerts -----------------------

const alertLogin = ref(false)

// ----------------------- Enable & Disable -----------------------

const isPasswordVisible = ref(false)

// ----------------------- Stores -----------------------

const userStore = useUsers()
const boardStore = useBoard()

// ----------------------- Params -----------------------

const nameJWT = ref("")
const emailJWT = ref("")
const userInfowhileLogin = ref()
const userInput = ref("")
const passwordInput = ref("")
const boardId = ref()

// ----------------------- BaseUrl -----------------------

const baseUrlUsers = `${import.meta.env.VITE_BASE_URL_MAIN_LOGIN}/login`
const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

// ----------------------- Validate -----------------------

const isValidUsername = computed(() => {
  return (
    userInput.value &&
    userInput.value.length > 0 &&
    userInput.value.length <= 50
  )
})

const isValidPassword = computed(() => {
  return (
    passwordInput.value &&
    passwordInput.value.length > 0 &&
    passwordInput.value.length <= 14
  )
})

const isFormValid = computed(() => {
  return isValidPassword.value && isValidUsername.value
})

// ----------------------- Validate -----------------------

const openHomePage = async () => {
  try {
    userStore.setUser(nameJWT.value)
    console.log(userStore.getUser())
    const itemsBoards = await getItems(baseUrlboards)
    const boardIds = itemsBoards.boards.map((board) => board.id)
    boardId.value = boardIds
    console.log(boardId.value[0])
    router.push({ name: "TaskList", params: { id: boardId.value[0] } })
    userStore.setBoard(boardId.value)
  } catch (error) {
    router.push({ name: "Board" })
    
  }
}

const showAlert = () => {
  alertLogin.value = true
  setTimeout(() => {
    alertLogin.value = false
  }, 2000)
}

const submitForm = async () => {
  try {
    const response = await fetch(baseUrlUsers, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        userName: userInput.value,
        password: passwordInput.value
      })
    })

    if (response.status === 200) {
      const data = await response.json()
  
      userStore.setLoginSuccess(true)
      const decoded = jwtDecode(data.access_token)
      nameJWT.value = decoded.name
      emailJWT.value = decoded.email

      userInfowhileLogin.value = { ...decoded }

      userStore.setUserInfo(userInfowhileLogin.value)
      console.log(userStore.getUserInfo())
      userStore.setEmail(emailJWT.value)
      userStore.setRefreshToken(data.refresh_token)
      userStore.setToken(data.access_token)

      localStorage.setItem("access_token", data.access_token)
      localStorage.setItem("refresh_token", data.refresh_token)

      const itemsBoards = await getBoardItems(baseUrlboards)
      boardStore.addNewBoards(itemsBoards.boards)
      boardStore.setCollabs(itemsBoards.collabs)
      console.log(boardStore.getBoards())
    
      openHomePage()
    } else if (response.status === 401) {
      showAlert()
    } else {
      showAlert()
    }
  } catch (error) {
    showAlert()
  }
}

</script>

<template>
  <div class="w-full h-screen bg-White">
    <!-- Error alert -->
    <div
      class="fixed top-4 left-1/2 transform -translate-x-1/2 w-full max-w-xs bg-red-500 text-sm text-white rounded-xl shadow-lg"
      role="alert"
      tabindex="-1"
      aria-labelledby="hs-toast-solid-color-red-label"
    >
      <div
        id="hs-toast-solid-color-red-label"
        class="flex p-4"
        v-show="alertLogin"
      >
        Username or Password is incorrect.

        <div class="ms-auto">
          <button
            @click="closeAlert"
            type="button"
            class="inline-flex shrink-0 justify-center items-center size-5 rounded-lg text-white hover:text-white opacity-50 hover:opacity-100 focus:outline-none focus:opacity-100"
            aria-label="Close"
          >
            <span class="sr-only">Close</span>
            <svg
              class="shrink-0 size-4"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M18 6 6 18"></path>
              <path d="m6 6 12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div class="flex flex-col md:flex-row min-h-screen">
      <div
        class="flex-1 flex items-center justify-center p-4 lg:p-16 bg-gradient-to-r from-violet-400 to-purple-300"
      >
        <div class="text-focus-in text-center lg:text-left max-w-md">
          <div class="flex justify-center lg:justify-start">
            <img
              src="/image/logo2.png"
              class="h-40 w-40 lg:h-48 lg:w-48"
              alt="Kradan Kanban Logo"
            />
          </div>
          <h1
            class="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-white text-shadowCustom border-black"
          >
            Hi, Welcome to <br />
            Kradan Kanban
          </h1>
          <p class="md:text-lg text-white">
            Visualize your work, maximize efficiency, and manage
            work-in-progress limits with a beautiful board view that works for
            you.
          </p>
        </div>
      </div>

      <!-- Form -->
      <div
        class="flex-1 flex items-center justify-center p-4 lg:p-16 bg-gradient-to-tr from-slate-50 to-gray-50"
      >
        <!-- Main Content Area -->
        <div class="flex-1 flex items-center justify-center">
          <div
            id="formLogin"
            class="w-full max-w-md rounded-lg shadow-lg p-20 bg-white scale-up-center"
          >
            <h2 class="text-2xl font-semibold text-customPink text-center">
              Login
            </h2>
            <p class="mt-2 text-center text-customPink">Access your account</p>
            <form @submit.prevent="submitForm" class="mt-4">
              <!-- User -->
              <div class="flex items-center mb-4">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 256 256"
                >
                  <g fill="#b0b0b0">
                    <path
                      d="M192 96a64 64 0 1 1-64-64a64 64 0 0 1 64 64"
                      opacity=".2"
                    />
                    <path
                      d="M230.92 212c-15.23-26.33-38.7-45.21-66.09-54.16a72 72 0 1 0-73.66 0c-27.39 8.94-50.86 27.82-66.09 54.16a8 8 0 1 0 13.85 8c18.84-32.56 52.14-52 89.07-52s70.23 19.44 89.07 52a8 8 0 1 0 13.85-8M72 96a56 56 0 1 1 56 56a56.06 56.06 0 0 1-56-56"
                    />
                  </g>
                </svg>
                <div class="flex flex-col -mb-6">
                  <input
                    v-model="userInput"
                    id="login"
                    type="text"
                    class="itbkk-username w-full ml-3 px-6 py-2 border border-gray-400 rounded-full focus:outline-none focus:ring-2 focus:ring-customPink"
                    placeholder="Username"
                    maxlength="50"
                    required
                  />
                  <p class="text-xs text-gray-400 ml-auto mr-2 mt-1">
                    {{ userInput.length }}/50
                  </p>
                </div>
              </div>
              <!-- Password -->
              <div class="relative flex items-center mb-6">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="#b0b0b0"
                    d="M8 11a1 1 0 0 1-1-1V7a5.002 5.002 0 0 1 8.532-3.542a5.09 5.09 0 0 1 1.306 2.293a1 1 0 0 1-1.934.505l-.002-.007a3.072 3.072 0 0 0-.786-1.379A3.002 3.002 0 0 0 9 7v3a1 1 0 0 1-1 1m4 7a1 1 0 0 1-1-1v-3a1 1 0 1 1 2 0v3a1 1 0 0 1-1 1"
                    opacity=".5"
                  />
                  <path
                    fill="#b0b0b0"
                    d="M17 9H7a3 3 0 0 0-3 3v7a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-7a3 3 0 0 0-3-3m-4 8a1 1 0 0 1-2 0v-3a1 1 0 1 1 2 0z"
                  />
                </svg>
                <div class="flex flex-col mt-6">
                  <input
                    v-model="passwordInput"
                    id="password"
                    :type="isPasswordVisible ? 'text' : 'password'"
                    class="itbkk-password w-full ml-3 px-6 py-2 border border-gray-400 rounded-full focus:outline-none focus:ring-2 focus:ring-customPink"
                    placeholder="Password"
                    maxlength="14"
                    required
                  />
                  <p class="text-xs text-gray-400 ml-auto mr-2 mt-1">
                    {{ passwordInput.length }}/14
                  </p>
                  <!-- eyes open -->
                  <svg
                    v-if="isPasswordVisible"
                    @click="isPasswordVisible = !isPasswordVisible"
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 1024 1024"
                    class="eye-icon absolute right-6 top-2/4 transform -translate-y-2/4 cursor-pointer"
                  >
                    <path
                      fill="#d9d9d9"
                      fill-opacity=".15"
                      d="M81.8 537.8a60.3 60.3 0 0 1 0-51.5C176.6 286.5 319.8 186 512 186c-192.2 0-335.4 100.5-430.2 300.3a60.3 60.3 0 0 0 0 51.5C176.6 737.5 319.9 838 512 838c-192.1 0-335.4-100.5-430.2-300.2"
                    />
                    <path
                      fill="#d9d9d9"
                      fill-opacity=".15"
                      d="M512 258c-161.3 0-279.4 81.8-362.7 254C232.6 684.2 350.7 766 512 766c161.4 0 279.5-81.8 362.7-254C791.4 339.8 673.3 258 512 258m-4 430c-97.2 0-176-78.8-176-176s78.8-176 176-176s176 78.8 176 176s-78.8 176-176 176"
                    />
                    <path
                      fill="#d9d9d9"
                      d="M942.2 486.2C847.4 286.5 704.1 186 512 186c-192.2 0-335.4 100.5-430.2 300.3a60.3 60.3 0 0 0 0 51.5C176.6 737.5 319.9 838 512 838c192.2 0 335.4-100.5 430.2-300.3c7.7-16.2 7.7-35 0-51.5M512 766c-161.3 0-279.4-81.8-362.7-254C232.6 339.8 350.7 258 512 258s279.4 81.8 362.7 254C791.5 684.2 673.4 766 512 766"
                    />
                    <path
                      fill="#d9d9d9"
                      d="M508 336c-97.2 0-176 78.8-176 176s78.8 176 176 176s176-78.8 176-176s-78.8-176-176-176m0 288c-61.9 0-112-50.1-112-112s50.1-112 112-112s112 50.1 112 112s-50.1 112-112 112"
                    />
                  </svg>
                  <!-- eyes close -->
                  <svg
                    v-else
                    @click="isPasswordVisible = !isPasswordVisible"
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 1024 1024"
                    class="eye-icon absolute right-6 top-2/4 transform -translate-y-2/4 cursor-pointer"
                  >
                    <path
                      fill="#d9d9d9"
                      fill-opacity=".15"
                      d="m254.89 758.85l125.57-125.57a176 176 0 0 1 248.82-248.82L757 256.72Q651.69 186.07 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 0 0 0 51.5q69.27 145.91 173.09 221.05M942.2 486.2Q889.46 375.11 816.7 305L672.48 449.27a176.09 176.09 0 0 1-227.22 227.21L323 798.75Q408 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 0 0 0-51.5"
                    />
                    <path
                      fill="#d9d9d9"
                      d="M942.2 486.2Q889.47 375.11 816.7 305l-50.88 50.88C807.31 395.53 843.45 447.4 874.7 512C791.5 684.2 673.4 766 512 766q-72.67 0-133.87-22.38L323 798.75Q408 838 512 838q288.3 0 430.2-300.3a60.29 60.29 0 0 0 0-51.5m-63.57-320.64L836 122.88a8 8 0 0 0-11.32 0L715.31 232.2Q624.86 186 512 186q-288.3 0-430.2 300.3a60.3 60.3 0 0 0 0 51.5q56.69 119.4 136.5 191.41L112.48 835a8 8 0 0 0 0 11.31L155.17 889a8 8 0 0 0 11.31 0l712.15-712.12a8 8 0 0 0 0-11.32M149.3 512C232.6 339.8 350.7 258 512 258c54.54 0 104.13 9.36 149.12 28.39l-70.3 70.3a176 176 0 0 0-238.13 238.13l-83.42 83.42C223.1 637.49 183.3 582.28 149.3 512m246.7 0a112.11 112.11 0 0 1 146.2-106.69L401.31 546.2A112 112 0 0 1 396 512"
                    />
                    <path
                      fill="#d9d9d9"
                      d="M508 624c-3.46 0-6.87-.16-10.25-.47l-52.82 52.82a176.09 176.09 0 0 0 227.42-227.42l-52.82 52.82c.31 3.38.47 6.79.47 10.25a111.94 111.94 0 0 1-112 112"
                    />
                  </svg>
                </div>
              </div>
              <!-- Sign in Btn -->
              <button
                type="submit"
                :disabled="!isFormValid"
                :class="{ disabled: !isFormValid, '': isFormValid }"
                class="itbkk-button-signin w-full py-2 px-4 bg-customPink text-white rounded-md disabled:bg-gray-300 disabled:cursor-not-allowed"
              >
                Sign in
              </button>

              <!-- Microsoft -->
              <div class="flex flex-col mt-4 items-center">
                <!-- <p class="text-gray-500">or sign up</p> -->
                <div class="items-center justify-center">
                  <button
                    class="flex w-72 py-2 justify-center rounded-md border border-gray-400"
                  >
                    <svg
                      aria-hidden="true"
                      viewBox="0 0 21 21"
                      width="24"
                      height="24"
                    >
                      <path fill="#f25022" d="M1 1h9v9H1z"></path>
                      <path fill="#00a4ef" d="M1 11h9v9H1z"></path>
                      <path fill="#7fba00" d="M11 1h9v9h-9z"></path>
                      <path fill="#ffb900" d="M11 11h9v9h-9z"></path>
                    </svg>
                    <p class="text-black ml-2">Sign in with Microsoft</p>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.text-focus-in {
  -webkit-animation: text-focus-in 0.5s cubic-bezier(0.55, 0.085, 0.68, 0.53)
    both;
  animation: text-focus-in 0.5s cubic-bezier(0.55, 0.085, 0.68, 0.53) both;
}

@-webkit-keyframes text-focus-in {
  0% {
    -webkit-filter: blur(12px);
    filter: blur(12px);
    opacity: 0;
  }

  100% {
    -webkit-filter: blur(0px);
    filter: blur(0px);
    opacity: 1;
  }
}

@keyframes text-focus-in {
  0% {
    -webkit-filter: blur(12px);
    filter: blur(12px);
    opacity: 0;
  }

  100% {
    -webkit-filter: blur(0px);
    filter: blur(0px);
    opacity: 1;
  }
}

.eye-icon {
  display: inline-block;
}

@media (max-width: 640px) {
  .eye-icon {
    display: none;
  }
}

@-webkit-keyframes scale-up-center {
  0% {
    -webkit-transform: scale(0.5);
    transform: scale(0.5);
  }

  100% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }
}

@keyframes scale-up-center {
  0% {
    -webkit-transform: scale(0.5);
    transform: scale(0.5);
  }

  100% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }
}

.scale-up-center {
  -webkit-animation: scale-up-center 0.7s cubic-bezier(0.39, 0.575, 0.565, 1)
    both;
  animation: scale-up-center 0.7s cubic-bezier(0.39, 0.575, 0.565, 1) both;
}
</style>
