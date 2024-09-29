import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useUsers = defineStore('users', () => {
  const users = ref([])
  const user = ref({ username: '' })
  const token = ref('')
  const LoginSuccess = ref(false)
  const refreshToken = ref('')
  const boardId = ref('')

  const setToken = (newToken) => {
    token.value = newToken
    sessionStorage.setItem('token', token.value)
  }

  const getToken = () => {
    return token.value || sessionStorage.getItem('token')
  }

  const setLoginSuccess = (statusLogin) => {
    LoginSuccess.value = statusLogin
  }

  const getLoginSuccess = () => {
    return LoginSuccess.value
  }

  const setRefreshToken = (newRefreshToken) => {
    refreshToken.value = newRefreshToken
    sessionStorage.setItem('refresh_token', refreshToken.value)
  }

  const getRefreshToken = () => {
    return refreshToken.value || sessionStorage.getItem('refresh_token')
  }

  if (sessionStorage.getItem('user')) {
    user.value = JSON.parse(sessionStorage.getItem('user'))
  }
  if (sessionStorage.getItem('token')) {
    token.value = sessionStorage.getItem('token')
  }
  if (sessionStorage.getItem('refresh_token')) {
    refreshToken.value = sessionStorage.getItem('refresh_token')
  }
  if (sessionStorage.getItem('boardId')) {
    boardId.value = sessionStorage.getItem('boardId')
  }

  const setUser = (userName) => {
    user.value = { username: userName }
    sessionStorage.setItem('user', JSON.stringify(user.value))
  }

  const getUser = () => {
    return user.value
  }

  const setBoard = (newBoard) => {
    boardId.value = newBoard
    sessionStorage.setItem('boardId', boardId.value)
  }

  const getBoard = () => {
    return boardId.value
  }

  const removeUser = (removeId) => {
    users.value.splice(
      users.value.findIndex((user) => user.id === removeId),
      1
    )
  }

  return {
    removeUser,
    setUser,
    getUser,
    setToken,
    getToken,
    setRefreshToken,
    getRefreshToken,
    setBoard,
    getBoard,
    setLoginSuccess,
    getLoginSuccess
  }
})

export { useUsers }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot))
}
