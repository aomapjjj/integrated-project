<script setup>
import { useRouter, useRoute } from "vue-router"
import { ref, computed } from "vue"
import { useUsers } from "@/stores/storeUser"
const sidebarTasks = ref(true)
const toggleSidebar = () => {
  sidebarTasks.value = !sidebarTasks.value
}
const userStore = useUsers()
const userName = userStore.getUser().username
const router = useRouter()

</script>

<template>
  <div class="min-h-full max-h-fit">
    <div class="min-h-screen flex">
      <!-- Sidebar -->
      <aside
        id="sidebar"
        v-if="sidebarTasks"
        class="w-64 bg-gray-100 shadow-lg transition-transform transform translate-x-0"
      >
        <div class="p-6">
          <img src="/src/image/sj3.png" alt="LOGO" class="w-24 h-24 mx-auto" />
          <h2 class="text-xl text-center font-bold mt-4">Kradan Kanban</h2>
          <nav class="mt-6">
            <ul>
              <li class="my-3">
                <a
                  href="#"
                  class="flex items-center text-gray-700 hover:bg-gray-200 hover:text-blue-500 transition duration-200 rounded-lg"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                  >
                    <g fill="none">
                      <path
                        fill="#d9d9d9"
                        fill-opacity=".25"
                        d="M16 16H8.415c-1.184 0-1.776 0-2.299.202q-.412.16-.76.43c-.442.344-.747.852-1.356 1.868V7c0-1.886 0-2.828.586-3.414S6.114 3 8 3h8c1.886 0 2.828 0 3.414.586S20 5.114 20 7v5c0 1.886 0 2.828-.586 3.414S17.886 16 16 16"
                      />
                      <path
                        stroke="#d9d9d9"
                        stroke-width="1.2"
                        d="M20 12v5c0 1.886 0 2.828-.586 3.414S17.886 21 16 21H6.5a2.5 2.5 0 0 1 0-5H16c1.886 0 2.828 0 3.414-.586S20 13.886 20 12V7c0-1.886 0-2.828-.586-3.414S17.886 3 16 3H8c-1.886 0-2.828 0-3.414.586S4 5.114 4 7v11.5"
                      />
                      <path
                        stroke="#d9d9d9"
                        stroke-linecap="round"
                        stroke-width="1.2"
                        d="m9 10l1.293 1.293a1 1 0 0 0 1.414 0L15 8"
                      />
                    </g>
                  </svg>

                  <span class="ml-3">All Boards</span>
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </aside>

      <!-- Main Content -->
      <div class="flex-1 flex flex-col">
        <!-- Navbar -->
        <nav
          class="bg-white shadow px-4 py-6 flex justify-between items-center"
          style="background-color: #d8f1f1"
        >
          <label class="btn btn-circle swap swap-rotate">
            <input id="sidebarToggle" type="checkbox" />

            <!-- hamburger icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              v-if="!sidebarTasks"
              @click="toggleSidebar"
            >
              <path
                fill="#F785B1"
                d="M20 10.75H4a.75.75 0 0 1 0-1.5h16a.75.75 0 0 1 0 1.5m0-4H4a.75.75 0 0 1 0-1.5h16a.75.75 0 0 1 0 1.5m0 8H4a.75.75 0 0 1 0-1.5h16a.75.75 0 0 1 0 1.5m0 4H4a.75.75 0 0 1 0-1.5h16a.75.75 0 0 1 0 1.5"
              />
            </svg>
            <!-- close icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              viewBox="0 0 24 24"
              v-if="sidebarTasks"
              @click="toggleSidebar"
            >
              <path
                fill="#F785B1"
                stroke="#d9d9d9"
                stroke-linecap="round"
                d="M12 21a9 9 0 1 1 0-18a9 9 0 0 1 0 18zM9 9l6 6m0-6l-6 6"
              />
            </svg>
          </label>
          <div
            class="text-2xl font-bold tracking-tight"
            style="color: #9391e4; text-shadow: 0 0 5px #ffffff"
          >
            IT-Bangmod Kradan Kanban
          </div>
          <div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              viewBox="0 0 256 256"
            >
              <path
                fill="#ffffff"
                d="M128 26a102 102 0 1 0 102 102A102.12 102.12 0 0 0 128 26M71.44 198a66 66 0 0 1 113.12 0a89.8 89.8 0 0 1-113.12 0M94 120a34 34 0 1 1 34 34a34 34 0 0 1-34-34m99.51 69.64a77.53 77.53 0 0 0-40-31.38a46 46 0 1 0-51 0a77.53 77.53 0 0 0-40 31.38a90 90 0 1 1 131 0"
              />
            </svg>
            <h2 class="text-sm tracking-tight text-gray-800">
              Welcome,

              <span class="itbkk-fullname flex font-bold">{{ userName }}</span>
            </h2>
          </div>
        </nav>
      </div>
    </div>

    <div>



    </div>
  </div>
</template>

<style scoped></style>
