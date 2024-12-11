<script setup>
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { getBoardItems } from '../../libs/fetchUtils.js'
import { useBoard } from '@/stores/storeBoard.js'
import authConfig from '@/libs/authConfig.js'

const route = useRoute()
const router = useRouter()

// Sidebar state
const sidebarOpen = ref(false)

// Dropdown state
const isExpanded = ref(false)

// Username state
const userStore = useUsers()
const userName = userStore.getUser().username

// Board state
const userBoard = useBoard()
const BoardsList = ref()
const boardsCollab = ref()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`

function getToken() {
  return localStorage.getItem('access_token')
}

onMounted(async () => {
  const token = getToken()

  const itemsBoards = await getBoardItems(baseUrlBoard)
  BoardsList.value = itemsBoards?.boards
  boardsCollab.value = itemsBoards?.collabs
})

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

function toggleDropdown() {
  isExpanded.value = !isExpanded.value
}

const clearToken = () => {
  if (userStore.getLoginMicrosoftSuccess() === true) {
    authConfig.logoff()
    localStorage.clear()
    localStorage.removeItem('access_token')
    router.push({ name: 'Login' })
  } else {
    localStorage.clear()
    localStorage.removeItem('access_token')
    router.push({ name: 'Login' })
  }
}

const toBoardsList = (boardId) => {
  if (boardId !== null) {
    router.push({ name: 'TaskList', params: { id: boardId } }).then(() => {
      router.go()
    })
    userStore.setBoard(boardId)
  }
}

const totalBoards = computed(() => {
  const personalCount = BoardsList.value?.length || 0
  const collabCount = boardsCollab.value?.length || 0
  return personalCount + collabCount
})
</script>

<template>
  <!-- Sidebar -->
  <div
    id="sidebar"
    :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
    class="hs-overlay fixed top-0 left-0 bottom-0 z-20 w-64 bg-white border-e border-gray-200 pt-7 overflow-y-auto transition-transform duration-300 transform -translate-x-full lg:translate-x-0 lg:relative lg:flex lg:flex-col lg:h-screen"
    role="dialog"
    tabindex="-1"
    aria-label="Sidebar"
  >
    <!-- Sidebar content here -->
    <div class="px-6">
      <img src="/image/sj3.png" alt="LOGO" class="w-24 h-24 mx-auto" />
    </div>
    <!-- Sidebar Nav Links -->
    <nav
      class="hs-accordion-group p-6 w-full flex-grow overflow-y-auto"
      data-hs-accordion-always-open
    >
      <ul class="space-y-1.5">
        <!-- Sidebar Items -->
        <li>
          <RouterLink to="/board">
            <span
              class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:bg-neutral-700 dark:text-white"
            >
              <svg
                class="size-4"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
              >
                <path
                  fill="#9391E4"
                  d="m21.743 12.331l-9-10c-.379-.422-1.107-.422-1.486 0l-9 10a1 1 0 0 0-.17 1.076c.16.361.518.593.913.593h2v7a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-4h4v4a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-7h2a.998.998 0 0 0 .743-1.669"
                />
              </svg>

              <span class="itbkk-home">Home</span>
            </span>
          </RouterLink>
        </li>

        <li class="hs-accordion" id="projects-accordion">
          <button
            @click="toggleDropdown"
            :aria-expanded="isExpanded"
            class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300"
          >
            <svg
              class="size-4"
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
            >
              <path
                fill="#9391E4"
                d="M20 4H4c-1.103 0-2 .897-2 2v10c0 1.103.897 2 2 2h4l-1.8 2.4l1.6 1.2l2.7-3.6h3l2.7 3.6l1.6-1.2L16 18h4c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2M5 13h4v2H5z"
              />
            </svg>
            All boards
            <!-- Count boards -->
            <div class="badge badge-sm text-xxs">
              {{ totalBoards }}
            </div>
            <!-- Icon when expanded -->
            <svg
              v-if="isExpanded"
              class="hs-accordion-active:block ms-auto size-4 text-gray-600 group-hover:text-gray-500 dark:text-neutral-400"
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
              <path d="m18 15-6-6-6 6" />
            </svg>
            <!-- Icon when collapsed -->
            <svg
              v-else
              class="ms-auto size-4 text-gray-600 group-hover:text-gray-500 dark:text-neutral-400"
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
              <path d="m6 9 6 6 6-6" />
            </svg>
          </button>

          <!-- Submenu -->
          <div
            v-show="isExpanded && BoardsList.length"
            class="hs-collapse w-full overflow-hidden transition-[height] duration-300"
          >
            <ul
              class="hs-accordion-group ps-3 pt-2"
              data-hs-accordion-always-open
            >
              <li
                v-for="(board, index) in BoardsList"
                :key="index"
                class="hs-accordion"
                :id="'users-accordion-sub-' + (index + 1)"
              >
                <button
                  @click="toBoardsList(board.id)"
                  type="button"
                  class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300"
                  :aria-expanded="board.isExpanded"
                  :aria-controls="'users-accordion-sub-' + (index + 1)"
                >
                  {{ board.name }}
                </button>
              </li>
              <li
                v-for="(board, index) in boardsCollab"
                :key="index"
                class="hs-accordion"
                :id="'users-accordion-sub-' + (index + 1)"
              >
                <button
                  @click="toBoardsList(board.id)"
                  type="button"
                  class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300"
                  :aria-expanded="board.isExpanded"
                  :aria-controls="'users-accordion-sub-' + (index + 1)"
                >
                  {{ board.name }}
                </button>
              </li>
            </ul>
          </div>
        </li>
      </ul>

      <!-- Tutorials & Help -->
      <ul
        class="pt-4 mt-4 space-y-2 font-medium border-t border-gray-200 dark:border-gray-700"
      >
        <li>
          <RouterLink to="/tutorials">
            <span
              class="flex items-center py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-300"
            >
              <svg
                class="size-4"
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
              >
                <path
                  fill="#374151"
                  fill-rule="evenodd"
                  d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10s-4.477 10-10 10m0-1.2a8.8 8.8 0 1 0 0-17.6a8.8 8.8 0 0 0 0 17.6M9 9.707C9.077 8.2 10.081 7 12.042 7C13.8 7 15 8.088 15 9.526c0 1.095-.565 1.87-1.458 2.407c-.886.523-1.137.914-1.137 1.646v.454h-1.221v-.642c-.007-.921.44-1.55 1.395-2.121c.81-.496 1.123-.935 1.123-1.682c0-.865-.67-1.5-1.709-1.5c-1.053 0-1.723.621-1.8 1.619zm2.798 7.507a.786.786 0 0 1-.796-.795c0-.454.35-.796.796-.796c.453 0 .795.342.795.796a.78.78 0 0 1-.795.795"
                />
              </svg>

              <span class="ms-3">Tutorials & Help</span>
            </span>
          </RouterLink>
        </li>
      </ul>
    </nav>
    <!-- Footer with User Info -->
    <div
      class="mt-auto p-2 fixed bottom-0 left-0 w-full lg:relative lg:mt-auto"
    >
      <div
        class="flex items-center justify-between border border-gray-100 p-2 rounded-full shadow-sm max-w-xs mx-auto"
      >
        <div class="flex items-center gap-2">
          <!-- User Icon -->
          <div class="avatar placeholder relative group">
            <div
              class="bg-neutral text-neutral-content w-10 h-10 overflow-hidden rounded-full group-hover:bg-gray-200 relative"
            >
              <!-- Add profile -->
              <span
                class="absolute inset-0 flex justify-center items-center text-2xl text-black opacity-0 group-hover:opacity-100"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                >
                  <path
                    fill="#ffffff"
                    fill-rule="evenodd"
                    d="M11.25 11.25V3.5h1.5v7.75h7.75v1.5h-7.75v7.75h-1.5v-7.75H3.5v-1.5z"
                  />
                </svg>
              </span>
              <img
                src="/image/profile.png"
                alt="profile"
                class="w-24 h-24 mx-auto rounded-full border-1 border-black"
              />
              <!-- <img
                    :src="imageSrc || '/image/profile.png'"
                    alt="profile"
                    class="w-24 h-24 mx-auto rounded-full border-1 border-black"
                  /> -->
            </div>
            <!-- Hidden File Input -->
            <!-- <input
                  type="file"
                  ref="fileInput"
                  accept="image/*"
                  @change="onFileChange"
                  class="hidden"
                /> -->
          </div>
          <!-- User Info -->
          <div>
            <!-- <p class="text-xs text-gray-500 dark:text-neutral-400">Welcome,</p> -->
            <p
              class="itbkk-fullname text-sm font-medium text-gray-800 dark:text-white"
            >
              {{ !userName ? 'Guest' : userName }}
            </p>
            <p
              class="itbkk-fullname text-xs font-medium text-gray-800 dark:text-white"
            >
              {{ userStore.getUserInfo()?.role }}
            </p>
          </div>
        </div>
        <!-- Log out -->
        <div class="mt-3">
          <button class="itbkk-sign-out group" @click="clearToken()">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="size-5"
              viewBox="0 0 512 512"
            >
              <path
                fill="none"
                stroke="#eb4343"
                class="group-hover:stroke-red-600"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="32"
                d="M304 336v40a40 40 0 0 1-40 40H104a40 40 0 0 1-40-40V136a40 40 0 0 1 40-40h152c22.09 0 48 17.91 48 40v40m64 160l80-80l-80-80m-192 80h256"
              />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Sidebar Toggle Button for Mobile -->
  <button
    @click="toggleSidebar"
    class="fixed top-4 left-4 z-50 text-gray-700 focus:outline-none lg:hidden"
  >
    <svg
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
      <path d="M3 6h18M3 12h18M3 18h18" />
    </svg>
  </button>

  <!-- End Sidebar -->
</template>

<style scoped>
.customPink {
  color: #f785b1;
}

.customPurple {
  color: #9391e4;
}

.customBlue {
  color: #9fc3e9;
}

.customRed {
  color: #eb4343;
}
</style>
