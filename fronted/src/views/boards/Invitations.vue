<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import {
    getBoardById,
    getItems,
    addCollaborator,
    editStatusCollab,
    deleteCollaborator
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

const boardOwnerName = ref("")
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
const openModalAddCollab = ref(false)
const showConfirmModal = ref(false)

// ----------------------- Alerts -----------------------

const isAlertFailure = ref(false)
const isAlertSuccess = ref(false)
const alertMessage = ref("")

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlBoardId = `${baseUrlboards}/${boardId.value}`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

console.log(userEmail.email);

onMounted(async () => {
    userStore.setToken(token)
    const collaborator = await getItems(baseUrlCollaborator)
    collaboratorInfo.value = collaborator.collaborators
    console.log("Get Items", collaboratorInfo.value)
    console.log(baseUrlboards);

    const Board = await getBoardById(boardId.value)
    boardName.value = Board.item.name
    console.log(Board.item.name)
    console.log("Board data", Board.item.owner.name)
    boardOwnerName.value = Board.item.owner.name
    console.log(boardOwnerName.value)

    if (Board.item.owner.name !== userName) {
        disabledButtonWhileOpenPublic.value = true
    }
})

const submitFormSendEmail = async () => {
    const email = collaboratorEmail?.value;
    const accessRight = collaboratorAccess.value;
    const boardIdValue = boardId.value;
    const inviterName = boardOwnerName.value;
    const boardNames = boardName.value;
    const boardUrl = baseUrlBoardId;

    if (!email || !accessRight || !inviterName || !boardNames || !boardUrl) {
        console.error("One or more required fields are missing.");
        return;
    }

    try {
        const collaboratorWithEmailDTO = {
            collaborator: {
                email,
                accessRight,
                status: "PENDING",
            },
            email: {
                inviterName,
                boardName: boardNames,
                accessRight,
                boardUrl,
            },
        };

        const result = await addCollaborator(boardIdValue, collaboratorWithEmailDTO);

        if (result.statusCode === 201) {
            console.log("Collaborator added and email sent successfully.");
        } else {
            console.error("Failed to add collaborator.");
        }
    } catch (error) {
        console.error("An error occurred:", error);
    }
}

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

const openAdd = () => {
    openModalAddCollab.value = true
}

const filteredCollaboratorInfo = computed(() =>
    collaboratorInfo.value.filter(item => item.status === 'PENDING' && item.email === userEmail.email)
);

const cancelAction = () => {
    openModalAddCollab.value = false
    clearForm()
}

const acceptCollaborator = async (collaborator) => {
    console.log(collaborator);
    
    try {
        if (collaborator) {
            collaborator.status = 'ACCEPTED';
            await editStatusCollab(boardId.value, collaborator.status, collaborator.id, collaborator.accessRight);
            console.log('Collaborator status updated to ACCEPT');
        }
    } catch (error) {
        console.error('Error updating collaborator status:', error);
    }
};

const declineCollaborator = async (collaborator) => {
    try {
        const status = await deleteCollaborator(boardId.value, collaborator.id);
        if (status === 200) {
            console.log('Collaborator deleted successfully');
            collaboratorInfo.value = collaboratorInfo.value.filter(c => c.id !== collaborator.id);
        } else {
            console.log('Failed to delete collaborator');
        }
    } catch (error) {
        console.error('Error deleting collaborator:', error);
    }
}
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
                                    {{ boardOwnerName.toLowerCase() }}
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
                    <button :disabled="disabledButtonWhileOpenPublic" :style="{
                        backgroundColor: disabledButtonWhileOpenPublic
                            ? '#d3d3d3'
                            : '#F785B1',
                        color: disabledButtonWhileOpenPublic ? '#a9a9a9' : 'white',
                        borderRadius: '30px',
                        cursor: disabledButtonWhileOpenPublic ? 'not-allowed' : 'pointer',
                        opacity: disabledButtonWhileOpenPublic ? 0.6 : 1
                    }" @click="openAdd" class="itbkk-button-add btn ml-4">
                        <svg xmlns="http://www.w3.org/3000/svg" width="24" height="24" viewBox="0 0 24 24">
                            <path fill="currentColor"
                                d="M11 13H6q-.425 0-.712-.288T5 12t.288-.712T6 11h5V6q0-.425.288-.712T12 5t.713.288T13 6v5h5q.425 0 .713.288T19 12t-.288.713T18 13h-5v5q0 .425-.288.713T12 19t-.712-.288T11 18z" />
                        </svg>
                        Invitations
                    </button>
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
                                    <tr v-for="(item, index) in filteredCollaboratorInfo" :key="item.id"
                                        class="itbkk-item">
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
                                            <button
                                                class="inline-flex bg-green-500 hover:bg-green-600 text-white rounded-full w-8 h-8 items-center justify-center"
                                                @click="acceptCollaborator(item)">
                                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                                    stroke-width="2" stroke="currentColor" class="w-4 h-4">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        d="M5 13l4 4L19 7" />
                                                </svg>
                                            </button>
                                            <button
                                                class="inline-flex bg-red-500 hover:bg-red-600 text-white rounded-full w-8 h-8 items-center justify-center"
                                                @click="declineCollaborator(item)">
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
                    <div v-if="openModalAddCollab"
                        class="fixed top-0 left-0 right-0 flex h-full w-full items-center justify-center bg-black bg-opacity-50">
                        <div class="max-h-full w-full max-w-md overflow-y-auto sm:rounded-2xl bg-white">
                            <div class="w-full p-8">
                                <h2 class="text-2xl font-bold text-center mb-4">
                                    Add Collaborator
                                </h2>

                                <div class="flex items-center space-x-4 mb-4">
                                    <!-- Email Input -->
                                    <div class="flex-1">
                                        <label class="block text-sm font-bold mb-2">Email</label>
                                        <input type="email" v-model="collaboratorEmail"
                                            class="w-full p-2 border rounded-lg" placeholder="you@ad.sit.kmutt.ac.th" />
                                    </div>

                                </div>

                                <!-- Buttons -->
                                <div class="flex justify-end mt-4">
                                    <button class="btn bg-gray-300 mr-4" @click="cancelAction">
                                        Cancel
                                    </button>
                                    <button class="btn bg-customPink hover:bg-customPinkDark disabled:opacity-50"
                                        @click="submitFormSendEmail">
                                        Add
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
