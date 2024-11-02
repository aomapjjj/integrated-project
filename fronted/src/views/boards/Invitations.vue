<script setup>
import { ref, onMounted, watch } from "vue"
import {
    getBoardById,
    getItems,
    addCollaborator,
    deleteCollaborator,
    editAccessRight
} from "./../../libs/fetchUtils.js"
import { useRoute, useRouter } from "vue-router"
import { useUsers } from "@/stores/storeUser"
import SideBar from "@/component/bar/SideBar.vue"
import Navbar from "@/component/bar/Navbar.vue"
import Alert from "@/component/alert/Alert.vue"
import ModalAcess from "@/component/modal/Modal.vue"

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------


const userStore = useUsers()

// ----------------------- Params -----------------------

const boardName = ref("")
const boardId = ref()
watch(
    () => route.params.id,
    (newId) => {
        boardId.value = newId
    },
    { immediate: true }
)

const token = localStorage.getItem("access_token")
const collaboratorEmail = ref("")
const collaboratorAccess = ref("READ")
const oidCollaboratorToRemove = ref(null)
const userName = userStore.getUser().username
const userEmail = userStore.getEmail()

// ----------------------- List Items -----------------------

const collaboratorInfo = ref([])

// ----------------------- Enable & Disable -----------------------


const disabledButtonWhileOpenPublic = ref(false)
const showConfirmModal = ref(false)



// ----------------------- Alerts -----------------------

const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref("")

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`


onMounted(async () => {
    userStore.setToken(token)
    const collaborator = await getItems(baseUrlCollaborator)
    collaboratorInfo.value = collaborator.collaborators
    console.log("Get Items", collaboratorInfo.value)

    const Board = await getBoardById(boardId.value)
    console.log("Board data", Board.item.owner.name)

    if (Board && Board.item && Board.item.name) {
        boardName.value = Board.item.name
    }
    if (Board.item.owner.name !== userName) {
        disabledButtonWhileOpenPublic.value = true
        console.log("ไม่ตรงกันนะจ๊า")
    } else {
        console.log("ตรงกันนะจ๊า")
    }
})

console.log(collaboratorInfo.value)
const showRemoveModal = (oid) => {
    console.log(oid)
    oidCollaboratorToRemove.value = oid
    console.log(oidCollaboratorToRemove.value)
    showConfirmModal.value = true
}

const pendingItem = ref(null)

const formatStatus = () => {
    if (collaboratorInfo.value.length > 0) {
        return collaboratorInfo.value[0].status.charAt(0) + collaboratorInfo.value[0].status.slice(1).toLowerCase();
    }
    return "";
};

</script>

<template>
    <div class="flex flex-col h-screen overflow-hidden">
        <div class="flex flex-1 overflow-hidden">
            <Alert :isAlertFailure="isAlertFailure" :isAlertSuccess="isAlertSuccess">
                {{ alertMessage }}
            </Alert>
            <SideBar />

            <div class="flex flex-col flex-1">
                <Navbar> IT Bangmod Kradan Kanbun </Navbar>

                <div class="mt-9 px-10 flex justify-between items-center">
                    <div class="text-sm breadcrumbs">
                        <ul>
                            <li>
                                <router-link :to="{ name: 'TaskList', params: { id: boardId } }">
                                    <svg xmlns="http://www.w3.org/3000/svg" viewBox="0 0 256 256"
                                        class="w-4 h-4 stroke-current mr-2">
                                        <path fill="currentColor"
                                            d="m219.31 108.68l-80-80a16 16 0 0 0-22.62 0l-80 80A15.87 15.87 0 0 0 32 120v96a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-56h32v56a8 8 0 0 0 8 8h64a8 8 0 0 0 8-8v-96a15.87 15.87 0 0 0-4.69-11.32M208 208h-48v-56a8 8 0 0 0-8-8h-48a8 8 0 0 0-8 8v56H48v-88l80-80l80 80Z" />
                                    </svg>
                                    {{ boardName.toLowerCase() }}
                                </router-link>
                            </li>
                            <li>
                                <a>
                                    <svg xmlns="http://www.w3.org/3000/svg" fill="none" viewBox="0 0 24 24"
                                        class="w-4 h-4 stroke-current mr-2">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z">
                                        </path>
                                    </svg>
                                    <span class="font-extrabold">Invitations</span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="flex flex-col items-center mt-1">
                    <!-- Table content -->
                    <div class="overflow-x-auto">
                        <div class="min-w-full">
                            <table class="table-auto1 mt-5 rounded-xl overflow-hidden mb-10"
                                style="table-layout: fixed">
                                <thead>
                                    <tr class="bg-base-200">
                                        <th class="hidden md:table-cell px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                                            style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;
                        color: #fff;
                      ">
                                            No.
                                        </th>

                                        <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                                            style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                                            Name
                                        </th>

                                        <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                                            style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                                            E-mail
                                        </th>
                                        <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                                            style="
                        background-color: #9391e4;
                        border-bottom: 2px solid #9391e4;

                        color: #fff;
                      ">
                                            Status
                                        </th>

                                        <th class="px-4 py-2 text-center md:text-left text-md font-semibold text-gray-700"
                                            style="
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
                                    <tr v-for="(item, index) in collaboratorInfo" :key="item.id" class="itbkk-item">
                                        <td
                                            class="hidden md:table-cell px-4 py-2 text-center md:text-left text-sm text-gray-700">
                                            {{ index + 1 }}
                                        </td>
                                        <td class="">
                                            <label class="itbkk-status-name" for="my_modal_6">
                                                {{ item.name }}
                                            </label>
                                        </td>

                                        <td
                                            class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700">
                                            {{ item.email }}
                                        </td>

                                        <td
                                            class="itbkk-status-description px-4 py-2 text-center md:text-left text-sm text-gray-700">
                                            {{ formatStatus() }}
                                        </td>

                                        <td
                                            class="px-4 py-2 text-center md:text-left text-sm text-gray-700 space-x-2 flex justify-center">
                                            <button :disabled="disabledButtonWhileOpenPublic"
                                                class="inline-flex bg-green-500 hover:bg-green-600 text-white rounded-full w-8 h-8 items-center justify-center"
                                                @click="showRemoveModal(item.id)">
                                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                                    stroke-width="2" stroke="currentColor" class="w-4 h-4">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        d="M5 13l4 4L19 7" />
                                                </svg>
                                            </button>
                                            <button :disabled="disabledButtonWhileOpenPublic"
                                                class="inline-flex bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 items-center justify-center"
                                                @click="showRemoveModal(item.id)">
                                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                                    stroke-width="2" stroke="currentColor" class="w-4 h-4">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        d="M6 18L18 6M6 6l12 12" />
                                                </svg>
                                            </button>
                                        </td>


                                    </tr>
                                    <tr class="bg-base-100 mt-4 md:mt-0" v-if="collaboratorInfo?.length === 0">
                                        <td colspan="5" class="text-center py-4 text-gray-400">
                                            Sorry, we couldn't find the invitation to this board
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
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
