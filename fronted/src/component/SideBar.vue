<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import { getItems, getBoardItems } from '../libs/fetchUtils.js'

const route = useRoute()
const router = useRouter()
const BoardsList = ref()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
function getToken() {
  return localStorage.getItem('access_token')
}


onMounted(async () => {
  const itemsBoards = await getBoardItems(baseUrlBoard)
  BoardsList.value = itemsBoards
  console.log('Side Bar', BoardsList.value)
  const token = getToken()
  // const response = await fetch(`${import.meta.env.VITE_BASE_URL_MAIN}/boards`, {
  //   headers: {
  //     Authorization: `Bearer ${token}`
  //   }
  // })

  // if (response.status === 404) {
  //   router.push({ name: 'ErrorPage' })
  // } else if (response.status === 401) {
  //   router.push({ name: 'Login' })
  // }
})

const sidebarOpen = ref(false)

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const isExpanded = ref(false)

function toggleDropdown() {
  isExpanded.value = !isExpanded.value
}

// Username
const userStore = useUsers()
const userName = userStore.getUser().username

// Log out
const clearToken = () => {
  localStorage.clear();
  localStorage.removeItem('access_token')
  router.push({ name: 'Login' })
}

const toBoardsList = (boardId) => {
  if (boardId !== null) {
    router.push({ name: 'TaskList', params: { id: boardId } }).then(() => {
      router.go()
    })
    console.log(boardId)
    userStore.setBoard(boardId)
  }
}

// const imageSrc = ref(null)
// const fileInput = ref(null)

// const triggerFileInput = () => {
//   fileInput.value.click()
// }

// const onFileChange = (event) => {
//   const file = event.target.files[0]
//   if (file) {
//     const reader = new FileReader()
//     reader.onload = (e) => {
//       imageSrc.value = e.target.result
//     }
//     reader.readAsDataURL(file)
//   }
// }
</script>

<template>
  <!-- Sidebar -->
  <div id="sidebar" :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
    class="hs-overlay fixed top-0 left-0 bottom-0 z-40 w-64 bg-white border-e border-gray-200 pt-7 overflow-y-auto transition-transform duration-300 transform -translate-x-full lg:translate-x-0 lg:relative lg:flex lg:flex-col lg:h-screen"
    role="dialog" tabindex="-1" aria-label="Sidebar">
    <!-- Sidebar content here -->
    <div class="px-6">
      <img src="/image/sj3.png" alt="LOGO" class="w-24 h-24 mx-auto" />
    </div>
    <!-- Sidebar Nav Links -->
    <nav class="hs-accordion-group p-6 w-full flex-grow overflow-y-auto" data-hs-accordion-always-open>
      <ul class="space-y-1.5">
        <!-- Sidebar Items -->

        <li>
          <a class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:bg-neutral-700 dark:text-white"
            href="#">
            <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="#9391E4"
                d="m21.743 12.331l-9-10c-.379-.422-1.107-.422-1.486 0l-9 10a1 1 0 0 0-.17 1.076c.16.361.518.593.913.593h2v7a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-4h4v4a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-7h2a.998.998 0 0 0 .743-1.669" />
            </svg>
            <RouterLink to="/board">
              <span class="itbkk-home">Home</span>
            </RouterLink>
          </a>
        </li>

        <li class="hs-accordion" id="projects-accordion">
          <button @click="toggleDropdown" :aria-expanded="isExpanded"
            class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300">
            <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="#9391E4"
                d="M20 4H4c-1.103 0-2 .897-2 2v10c0 1.103.897 2 2 2h4l-1.8 2.4l1.6 1.2l2.7-3.6h3l2.7 3.6l1.6-1.2L16 18h4c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2M5 13h4v2H5z" />
            </svg>
            All boards
            <!-- Count boards -->
            <div class="badge badge-sm text-xxs">{{ BoardsList?.length }}</div>
            <!-- Icon when expanded -->
            <svg v-if="isExpanded"
              class="hs-accordion-active:block ms-auto size-4 text-gray-600 group-hover:text-gray-500 dark:text-neutral-400"
              xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m18 15-6-6-6 6" />
            </svg>
            <!-- Icon when collapsed -->
            <svg v-else class="ms-auto size-4 text-gray-600 group-hover:text-gray-500 dark:text-neutral-400"
              xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m6 9 6 6 6-6" />
            </svg>
          </button>

          <!-- Submenu -->
          <div v-show="isExpanded" class="hs-collapse w-full overflow-hidden transition-[height] duration-300">
            <ul class="hs-accordion-group ps-3 pt-2" data-hs-accordion-always-open>
              <li v-for="(board, index) in BoardsList" :key="index" class="hs-accordion"
                :id="'users-accordion-sub-' + (index + 1)">
                <button @click="toBoardsList(board.id)" type="button"
                  class="hs-accordion-toggle hs-accordion-active:text-blue-600 hs-accordion-active:hover:bg-transparent w-full text-start flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 focus:outline-none focus:bg-gray-100 dark:bg-neutral-800 dark:text-neutral-400 dark:hs-accordion-active:text-white dark:hover:bg-neutral-700 dark:hover:text-neutral-300 dark:focus:bg-neutral-700 dark:focus:text-neutral-300"
                  :aria-expanded="board.isExpanded" :aria-controls="'users-accordion-sub-' + (index + 1)">
                  {{ board.name }}
                </button>
              </li>
            </ul>
          </div>
        </li>

        <li>
          <a class="flex items-center gap-x-3.5 py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:bg-neutral-700 dark:text-white"
            href="#">
            <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="#9391E4"
                d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3s1.34 3 3 3m-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5S5 6.34 5 8s1.34 3 3 3m0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5m8 0c-.29 0-.62.02-.97.05c1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5" />
            </svg>
            <span class="">Members</span>
          </a>
        </li>
      </ul>

      <!-- Tutorials & Help -->
      <ul class="pt-4 mt-4 space-y-2 font-medium border-t border-gray-200 dark:border-gray-700">
        <li>
          <a href="#"
            class="flex items-center py-2 px-2.5 text-sm text-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-300">
            <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="#374151" fill-rule="evenodd"
                d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10s-4.477 10-10 10m0-1.2a8.8 8.8 0 1 0 0-17.6a8.8 8.8 0 0 0 0 17.6M9 9.707C9.077 8.2 10.081 7 12.042 7C13.8 7 15 8.088 15 9.526c0 1.095-.565 1.87-1.458 2.407c-.886.523-1.137.914-1.137 1.646v.454h-1.221v-.642c-.007-.921.44-1.55 1.395-2.121c.81-.496 1.123-.935 1.123-1.682c0-.865-.67-1.5-1.709-1.5c-1.053 0-1.723.621-1.8 1.619zm2.798 7.507a.786.786 0 0 1-.796-.795c0-.454.35-.796.796-.796c.453 0 .795.342.795.796a.78.78 0 0 1-.795.795" />
            </svg>

            <span class="ms-3">Tutorials & Help</span>
          </a>
        </li>
      </ul>
    </nav>
    <!-- Footer with User Info -->
    <div class="mt-auto p-4 fixed bottom-0 left-0 w-full lg:relative lg:mt-auto">
      <div class="flex items-center justify-between border border-gray-100 p-3 rounded-full shadow-lg max-w-xs mx-auto">
        <div class="flex items-center gap-2">
          <!-- User Icon -->
          <div class="avatar placeholder relative group">
            <div class="bg-neutral text-neutral-content w-10 h-10 rounded-full group-hover:bg-gray-200 relative">
              <!-- Add profile -->
              <span
                class="absolute inset-0 flex justify-center items-center text-2xl text-black opacity-0 group-hover:opacity-100">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
                  <path fill="#ffffff" fill-rule="evenodd"
                    d="M11.25 11.25V3.5h1.5v7.75h7.75v1.5h-7.75v7.75h-1.5v-7.75H3.5v-1.5z" />
                </svg>
              </span>
              <img src="/image/profile.png" alt="profile"
                class="w-24 h-24 mx-auto rounded-full border-1 border-black" />
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
            <p class="itbkk-fullname text-sm font-medium text-gray-800 dark:text-white">
              {{ userName }}
            </p>
            <!-- <p
              class="itbkk-fullname text-xs font-medium text-gray-800 dark:text-white"
            >
              {{ userRole }}
            </p> -->
          </div>
        </div>
        <!-- Log out -->
        <div class="mt-3">
          <button class="itbkk-sign-out group" @click="clearToken()">
            <svg class="size-5" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="none" stroke="#eb4343" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M15 17.625c-.074 1.852-1.617 3.424-3.684 3.374c-.481-.012-1.076-.18-2.265-.515c-2.861-.807-5.345-2.164-5.941-5.203C3 14.724 3 14.095 3 12.837v-1.674c0-1.257 0-1.886.11-2.445c.596-3.038 3.08-4.395 5.941-5.202c1.19-.335 1.784-.503 2.265-.515c2.067-.05 3.61 1.522 3.684 3.374M21 12H10m11 0c0-.7-1.994-2.008-2.5-2.5M21 12c0 .7-1.994 2.008-2.5 2.5"
                class="group-hover:stroke-red-400" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Sidebar Toggle Button for Mobile -->
  <button @click="toggleSidebar" class="fixed top-4 left-4 z-50 text-gray-700 focus:outline-none lg:hidden">
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor"
      stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
