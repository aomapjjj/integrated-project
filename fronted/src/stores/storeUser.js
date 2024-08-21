import { defineStore, acceptHMRUpdate } from "pinia"
import { ref } from "vue"

const useUsers = defineStore("users", () => {
  const users = ref([])
  const user = ref({ username: "" })
  const name = ref({ name: "" })

  const setName = (name) => {
    name.value = { name : name }
    localStorage.setItem("name", JSON.stringify(name.value))
  }



  
  if (localStorage.getItem("user")) {
    user.value = JSON.parse(localStorage.getItem("user"))
  }


  const getUser = () => {
    return user.value
  }

  const setUser = (userName) => {
    user.value = { username: userName }
    localStorage.setItem("user", JSON.stringify(user.value))
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
    getUser
  }
})

export { useUsers }

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot))
}
