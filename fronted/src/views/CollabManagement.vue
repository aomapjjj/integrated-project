<script setup>
import { ref, onMounted, watch } from 'vue'
import { getBoardById, getItems, addCollaborator, deleteCollaborator, editAccessRight } from '../libs/fetchUtils.js'
import { useStatuses } from '../stores/storeStatus'
import { useRoute, useRouter } from 'vue-router'
import { useUsers } from '@/stores/storeUser'
import SideBar from '@/component/SideBar.vue'
import Navbar from '@/component/Navbar.vue'

const userStore = useUsers()
const route = useRoute()
const router = useRouter()

const myStatuses = useStatuses()
const token = localStorage.getItem("access_token")
const userName = userStore.getUser().username
const disabledButtonWhileOpenPublic = ref(false)
const boardId = ref()
const boardName = ref('')
const openModalAddCollab = ref(false)
const collaboratorEmail = ref('')
const collaboratorAccess = ref('READ');
const statusList = ref([])
const collaboratorInfo = ref([])
const showConfirmModal = ref(false)
const oidCollaboratorToRemove = ref(null)

watch(
  () => route.params.id,
  (newId) => {
    boardId.value = newId
  },
  { immediate: true }
)



const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

onMounted(async () => {
  userStore.setToken(token)
  const collaborator = await getItems(baseUrlCollaborator)
  collaboratorInfo.value = collaborator
  console.log('Get Items',collaboratorInfo.value)

  const Board = await getBoardById(boardId.value)
  console.log('Board data', Board.item.owner.name)

  if (Board && Board.item && Board.item.name) {
    boardName.value = Board.item.name
  }
})

const openAdd = () => {
  openModalAddCollab.value = true
}

const cancelAction = () => {
  openModalAddCollab.value = false
}

const submitForm = async () => {
  if (collaboratorAccess.value && collaboratorEmail.value) {
    try {
      const result = await addCollaborator(boardId.value, {
        email: collaboratorEmail.value,
        accessRight: collaboratorAccess.value
      });
  

      console.log("Collaborator Added:", result.data);
     collaboratorInfo.value.push(result.data)
      openModalAddCollab.value = false;
    } catch (error) {
      console.error("Error adding collaborator:", error.message);
    }
  } else {
    console.error("Collaborator email and access right are required.");
  }
}

const showRemoveModal = (oid) => {
  oidCollaboratorToRemove.value = oid
  showConfirmModal.value = true
}

const confirmRemove = async () => {
  if (oidCollaboratorToRemove.value) {
    try {
      const status = await deleteCollaborator(boardId.value, oidCollaboratorToRemove.value)
      if (status === 200) {
        collaboratorInfo.value = collaboratorInfo.value.filter((collab) => collab.oid !== oidCollaboratorToRemove.value)
        console.log("Collaborator removed successfully")
      } else {
        console.error("Failed to remove collaborator")
      }
    } catch (error) {
      console.error("Error removing collaborator:", error)
    } finally {
      showConfirmModal.value = false
      oidCollaboratorToRemove.value = null
    }
  }
}

const updateAccessRight = async (item) => {
  try {
    const result = await editAccessRight(boardId.value, item.accessRight, item.oid);
    console.log("Access right updated:", result);
  } catch (error) {
    console.error("Failed to update access right:", error);
  }
}
</script>


<template>
  <div class="flex flex-col h-screen overflow-hidden">
    <div class="flex flex-1 overflow-hidden">
      <SideBar />

      <div class="flex flex-col flex-1">
        <Navbar> IT Bangmod Kradan Kanbun </Navbar>

        <div class="mt-9 px-10 flex justify-between items-center">
          <div class="text-sm breadcrumbs">
            <ul>
              <li>
                <a @click="$router.go(-1)">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" class="w-4 h-4 stroke-current mr-2">
                    <path fill="currentColor"
                      d="m219.31 108.68l-80-80a16 16 0 0 0-22.62 0l-80 80A15.87 15.87 0 0 0 32 120v96a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-56h32v56a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-96a15.87 15.87 0 0 0-4.69-11.32M208 208h-48v-56a8 8 0 0 0-8-8h-48a8 8 0 0 0-8 8v56H48v-88l80-80l80 80Z" />
                  </svg>
                  {{ boardName.toLowerCase() }}
                </a>
              </li>
              <li>
                <a>
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                    class="w-4 h-4 stroke-current mr-2">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"></path>
                  </svg>
                  <span class="font-extrabold">Collaborator</span>
                </a>
              </li>
            </ul>
          </div>

          <button :disabled="disabledButtonWhileOpenPublic" :style="{
            backgroundColor: disabledButtonWhileOpenPublic
              ? '#d3d3d3'
              : '#F785B1',
            color: disabledButtonWhileOpenPublic ? '#a9a9a9' : 'white',
            borderRadius: '30px',
            cursor: disabledButtonWhileOpenPublic ? 'not-allowed' : 'pointer',
            opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
          }" @click="openAdd" class="itbkk-button-add btn ml-4">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="currentColor"
                d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z" />
            </svg>
            Add Collaborator
          </button>
        </div>

        <div class="flex flex-col items-center mt-1">
          <!-- Table content -->
          <div class="overflow-x-auto">
            <div class="min-w-full">

              <table class="table-auto1 mt-5 rounded-xl overflow-hidden mb-10" style="table-layout: fixed">
                <thead>
                  <tr class="bg-base-200">
                    <th
                      class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                      style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;
                        color: #fff;
                      ">
                      No.
                    </th>

                    <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                      Name
                    </th>

                    <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                      E-mail
                    </th>
                    <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                      Acess Right
                    </th>

                    <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700" style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                        text-align: center;
                        vertical-align: middle;
                      ">
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in collaboratorInfo" :key="item.oid" class="itbkk-item">
                    <td class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700">
                      {{ index + 1 }}
                    </td>
                    <td class="">
                      <label class="itbkk-status-name" for="my_modal_6">
                        {{ item.name }}
                      </label>
                    </td>

                    <td class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700">
                      {{ item.email }}
                    </td>

                    <td class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700">
                      <select v-model="item.accessRight" @change="updateAccessRight(item)">
                        <option value="READ">READ</option>
                        <option value="WRITE">WRITE</option>
                      </select>
                    </td>

                    <td class="px-4 py-2 text-center md:text-left text-sm text-gray-700">
                      <button @click="showRemoveModal(item.oid)" class="btn bg-red-500 text-white">
                        Remove
                      </button>
                    </td>
                    .
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <!-- Modal AddCollab -->
          <div v-if="openModalAddCollab"
            class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50">
            <div class="max-h-full w-full max-w-md overflow-y-auto sm:rounded-2xl bg-white">
              <div class="w-full p-8">
                <h2 class="text-2xl font-bold text-center mb-4">Add Collaborator</h2>

                <label class="block text-sm font-bold mb-2">Email</label>
                <input type="email" v-model="collaboratorEmail" class="w-full p-2 border rounded mb-4"
                  placeholder="Enter email" />

                <label class="block text-sm font-bold mb-2">Access Right</label>
                <select v-model="collaboratorAccess" class="w-full p-2 border rounded mb-4">
                  <option value="READ">READ</option>
                  <option value="WRITE">WRITE</option>
                </select>

                <div class="flex justify-end">
                  <button class="btn bg-gray-500 text-white mr-4" @click="cancelAction">Cancel</button>
                  <button class="btn bg-blue-500 text-white" @click="submitForm">Save</button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="showConfirmModal"
            class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50">
            <div class="bg-white p-6 rounded-md max-w-md w-full">
              <h3 class="text-lg font-semibold text-center mb-4">Are you sure you want to remove this collaborator?</h3>
              <div class="flex justify-end">
                <button @click="showConfirmModal = false" class="btn bg-gray-500 text-white mr-4">Cancel</button>
                <button @click="confirmRemove" class="btn bg-red-500 text-white">Confirm</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@media (max-width: 576px) {
  .table-auto1 {
    width: 100%;
    overflow-x: auto;
  }
}

td {
  border-bottom: 1px solid #ababab;
}

tr:nth-child(even) {
  background-color: #f2f2f2;
}

tr:nth-child(odd) {
  background-color: #ffffff;
}

thead th {
  height: 3rem;
}

.table-auto1 {
  table-layout: fixed;
}
</style>
