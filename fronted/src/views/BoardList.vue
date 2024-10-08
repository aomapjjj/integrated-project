<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from '@/stores/storeUser'
import {
  getItemById,
  getItems,
  deleteItemById,
  editLimit
} from '../libs/fetchUtils.js'
const BoardsList = ref([])
const openModalName = ref(false)
const collaboratorInfo = ref()
const sidebarTasks = ref(true)
const toggleSidebar = () => {
  sidebarTasks.value = !sidebarTasks.value
}
const userStore = useUsers()
const userName = userStore.getUser().username
const router = useRouter()

const baseUrlBoard = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

onMounted(async () => {
  const itemsBoards = await getItems(baseUrlBoard)
  BoardsList.value = itemsBoards
  console.log(BoardsList)

  const collaborator = await getItems(baseUrlCollaborator)
  collaboratorInfo.value = collaborator
})

const toTasksList = () => {
  router.push({ name: 'TaskList' })
}
</script>

<template>
  <div class="min-h-full max-h-fit">
    <div class="min-h-screen flex">
      <!-- Main Content -->
      <div class="flex-1 flex flex-col">
        <!-- Navbar -->
        <nav
          class="bg-white shadow py-4 flex justify-between items-center w-full"
          style="background-color: #d8f1f1"
        >
          <div class="ml-10">
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
          </div>
          <div
            class="text-3xl font-bold tracking-tight"
            style="color: #9391e4; text-shadow: 0 0 5px #ffffff"
          >
            {{ userName }} Personal Board
          </div>
          <div class="flex items-center space-x-2 mr-3">
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

            <span
              class="itbkk-fullname flex font-bold"
              style="color: #9391e4; text-shadow: 0 0 5px #ffffff"
              >{{ userName }}</span
            >
          </div>
        </nav>
        <!-- <div>
          <button class="btn1 fourth text-black flex flex-col items-center">
            <svg class="mb-2"
              xmlns="http://www.w3.org/2000/svg"
              width="32"
              height="32"
              viewBox="0 0 24 24"
            >
              <g fill="none" stroke="#000000">
                <path
                  d="M3.5 11c0-1.9.001-3.274.142-4.322c.139-1.034.406-1.675.883-2.153c.478-.477 1.119-.744 2.153-.883C7.726 3.502 9.1 3.5 11 3.5h2c1.9 0 3.274.001 4.323.142c1.033.139 1.674.406 2.152.883c.477.478.744 1.119.883 2.153c.14 1.048.142 2.422.142 4.322v2c0 1.9-.001 3.274-.142 4.323c-.139 1.033-.406 1.674-.883 2.152c-.478.477-1.119.744-2.152.883c-1.049.14-2.423.142-4.323.142h-2c-1.9 0-3.274-.001-4.322-.142c-1.034-.139-1.675-.406-2.153-.883c-.477-.478-.744-1.119-.883-2.152C3.502 16.274 3.5 14.9 3.5 13z"
                />
                <path stroke-linejoin="round" d="M12 8v8m4-4H8" />
              </g>
            </svg>
            Add Board
          </button>
        </div> -->

        <!------------------------- Create Board ------------------------->
        <button @click="openModalName = !openModalName">
          <div
            class="w-full p-6 bg-white border border-gray-200 rounded-md shadow-md max-w-[13rem] fourth ml-4 mt-2"
          >
            <div class="flex flex-col items-center relative">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="32"
                height="32"
                viewBox="0 0 24 24"
              >
                <defs>
                  <mask id="letsIconsAddSquareDuotoneLine0">
                    <g fill="none">
                      <path
                        stroke="silver"
                        stroke-opacity=".25"
                        d="M3.5 11c0-1.9.001-3.274.142-4.322c.139-1.034.406-1.675.883-2.153c.478-.477 1.119-.744 2.153-.883C7.726 3.502 9.1 3.5 11 3.5h2c1.9 0 3.274.001 4.323.142c1.033.139 1.674.406 2.152.883c.477.478.744 1.119.883 2.153c.14 1.048.142 2.422.142 4.322v2c0 1.9-.001 3.274-.142 4.323c-.139 1.033-.406 1.674-.883 2.152c-.478.477-1.119.744-2.152.883c-1.049.14-2.423.142-4.323.142h-2c-1.9 0-3.274-.001-4.322-.142c-1.034-.139-1.675-.406-2.153-.883c-.477-.478-.744-1.119-.883-2.152C3.502 16.274 3.5 14.9 3.5 13z"
                      />
                      <path
                        stroke="#fff"
                        stroke-linejoin="round"
                        d="M12 8v8m4-4H8"
                      />
                    </g>
                  </mask>
                </defs>
                <path
                  fill="#000000"
                  d="M0 0h24v24H0z"
                  mask="url(#letsIconsAddSquareDuotoneLine0)"
                />
              </svg>
              <span class="itbkk-button-create mt-2">Create Board</span>
            </div>
          </div>
        </button>
        <!------------------------- Board ------------------------->

        <div class="flex flex-col items-center mt-6">
          <div class="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
            <div
              class="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8"
            >
              <div
                class="overflow-hidden border border-gray-200 dark:border-gray-700 md:rounded-lg"
              >
                <table
                  class="min-w-full divide-y divide-gray-200 dark:divide-gray-700"
                >
                  <thead class="bg-gray-50 dark:bg-gray-800">
                    <tr>
                      <th
                        scope="col"
                        class="py-3.5 px-4 text-sm font-normal text-center text-gray-500 dark:text-gray-400"
                      >
                        <button
                          class="flex items-center justify-center gap-x-3 focus:outline-none"
                        >
                          <span>Title</span>
                        </button>
                      </th>
                      <th
                        scope="col"
                        class="px-12 py-3.5 text-sm font-normal text-center text-gray-500 dark:text-gray-400"
                      >
                        Name
                      </th>
                      <th
                        scope="col"
                        class="px-4 py-3.5 text-sm font-normal text-center text-gray-500 dark:text-gray-400"
                      >
                        Action
                      </th>
                    </tr>
                  </thead>
                  <tbody
                    class="bg-white divide-y divide-gray-200 dark:divide-gray-700 dark:bg-gray-900"
                  >
                    <tr class="itbkk-item">
                      <td
                        class="px-4 py-4 text-sm font-medium text-center whitespace-nowrap"
                      >
                        <div>
                          <h2 class="font-medium text-gray-800 dark:text-white">
                            123
                          </h2>
                        </div>
                      </td>
                      <td
                        class="px-4 py-4 text-sm text-center whitespace-nowrap"
                      >
                        <div>
                          <h4 class="text-gray-700 dark:text-gray-200">Eiei</h4>
                        </div>
                      </td>
                      <td
                        class="px-12 py-4 text-sm font-medium text-center whitespace-nowrap"
                      >
                        <div
                          class="inline px-3 py-1 text-sm font-normal rounded-full text-emerald-500 gap-x-2 bg-emerald-100/60 dark:bg-gray-800"
                        >
                          E Pond
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!------------------------- Modal ------------------------->
        <div
          v-show="openModalName"
          class="fixed left-0 top-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50 py-10"
        >
          <div
            class="max-h-full w-full max-w-xl overflow-y-auto sm:rounded-2xl bg-white"
          >
            <div class="w-full">
              <div class="m-8 my-20 max-w-[400px] mx-auto">
                <div class="mb-8">
                  <h1 class="mb-4 text-3xl font-extrabold">New Board</h1>
                  <input
                    type="text"
                    placeholder="Name"
                    class="itbkk-board-name input input-bordered w-full max-w-s"
                  />
                </div>
                <div class="space-y-4">
                  <button
                    class="itbkk-button-ok p-3 bg-black rounded-full text-white w-full font-semibold"
                  >
                    Save
                  </button>

                  <button
                    @click="openModalName = false"
                    class="itbkk-button-cancel p-3 bg-white border rounded-full w-full font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fourth {
  border-color: #b7b7b7;
  background-image: -webkit-linear-gradient(
    45deg,
    #9fc3e9 50%,
    transparent 50%
  );
  background-image: linear-gradient(45deg, #9fc3e9 50%, transparent 50%);
  background-position: 100%;
  background-size: 400%;
  -webkit-transition: 300ms ease-in-out;
  transition: 300ms ease-in-out;
}

.fourth:hover {
  background-position: 0;
}
</style>
