<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import {
    getBoardById,
    getItems,
    addCollaborator,
    editStatusCollab,
    deleteCollaborator
} from './../../libs/fetchUtils.js'
import { useRoute, useRouter } from 'vue-router'
import { useUsers } from '@/stores/storeUser'
import SideBar from '@/component/bar/SideBar.vue'
import Navbar from '@/component/bar/Navbar.vue'
import Alert from '@/component/alert/Alert.vue'

// ----------------------- Router -----------------------

const route = useRoute()
const router = useRouter()

// ----------------------- Stores -----------------------

const userStore = useUsers()

// ----------------------- Params -----------------------

const boardOwnerName = ref('')
const boardName = ref('')
const boardId = ref()

watch(
    () => route.params.id,
    (newId) => {
        boardId.value = newId
    },
    { immediate: true }
)

const token = localStorage.getItem('access_token')
const collaboratorEmail = ref('')
const collaboratorAccess = ref('READ')
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
const alertMessage = ref('')

// ----------------------- BaseUrl -----------------------

const baseUrlboards = `${import.meta.env.VITE_BASE_URL_MAIN}/boards`
const baseUrlBoardId = `${baseUrlboards}/${boardId.value}`
const baseUrlCollaborator = `${baseUrlboards}/${boardId.value}/collabs`

console.log(userEmail.email)

onMounted(async () => {
    userStore.setToken(token)
    const collaborator = await getItems(baseUrlCollaborator)
    collaboratorInfo.value = collaborator.collaborators
    console.log('Get Items', collaboratorInfo.value)
    console.log(baseUrlboards)

    const Board = await getBoardById(boardId.value)
    boardName.value = Board.item.name
    console.log(Board.item.name)
    console.log('Board data', Board.item.owner.name)
    boardOwnerName.value = Board.item.owner.name
    console.log(boardOwnerName.value)

    if (Board.item.owner.name !== userName) {
        disabledButtonWhileOpenPublic.value = true
    }
})

const filteredCollaboratorInfo = computed(() =>
    collaboratorInfo.value.filter(
        (item) => item.status === 'PENDING' && item.email === userEmail.email
    )
)

const acceptCollaborator = async (collaborator) => {
    console.log(collaborator)

    try {
        if (collaborator) {
            collaborator.status = 'ACCEPTED'
            await editStatusCollab(
                boardId.value,
                collaborator.status,
                collaborator.id,
                collaborator.accessRight
            )
            router.push({ name: 'TaskList', params: { id: boardId.value } })
        }
    } catch (error) {
        console.error('Error updating collaborator status:', error)
    }
}

const declineCollaborator = async (collaborator) => {
    try {
        const status = await deleteCollaborator(boardId.value, collaborator.id)
        if (status === 200) {
            router.push({ name: 'Board'})
            console.log('Collaborator deleted successfully')
            collaboratorInfo.value = collaboratorInfo.value.filter(
                (c) => c.id !== collaborator.id
            )
        } else {
            console.log('Failed to delete collaborator')
        }
    } catch (error) {
        console.error('Error deleting collaborator:', error)
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
                <Navbar> IT Bangmod Kradan Kanban </Navbar>
                <div class="flex flex-col justify-center items-center h-[100vh]">
                    <div v-for="(item, index) in filteredCollaboratorInfo" :key="item.id"
                        class="relative flex flex-col items-center rounded-[10px] border-[1px] border-gray-200 w-[400px] mx-auto p-4 bg-white bg-clip-border shadow-md shadow-[#F3F3F3] dark:border-[#ffffff33] dark:!bg-navy-800 dark:text-white dark:shadow-none">
                        <div class="relative flex h-32 w-full justify-center rounded-xl bg-cover">
                            <img src="/image/invite.png"
                                class="absolute flex h-32 w-full justify-center rounded-xl bg-cover" />
                            <div
                                class="absolute -bottom-12 flex h-[87px] w-[87px] items-center justify-center rounded-full border-[4px] border-white bg-pink-400 dark:!border-navy-700">
                                <img class="h-full w-full rounded-full" src="/image/profile.png" alt="profile" />
                            </div>
                        </div>
                        <div class="mt-16 flex flex-col items-center">
                            <div v-if="filteredCollaboratorInfo.length > 0" class="itbkk-invite-message">
                                <p>
                                    {{ boardOwnerName }} has invited you to collaborate with
                                    {{ item.accessRight }} access right on {{ boardName }} board
                                </p>
                                <div class="flex justify-center gap-3 mt-4">
                                    <button class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg"
                                        @click="acceptCollaborator(item)">Accept</button>
                                    <button class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg"
                                        @click="declineCollaborator(item)">Decline</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style></style>