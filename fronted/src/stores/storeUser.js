import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

const useUsers = defineStore('users', () => {
    //state
    const users = ref([])
    //view
    const getUsers = () => {
      return users.value
    }
    //actions
    const addUsers = (newUsers) => {
      newUsers?.forEach((newUser) =>
        addStatus(  newUser.oid,
            newUser.name, 
            newUser.username, 
            newUser.email,
            newUser.password,
            newUser.role,
              )
      )
    }
    const addUser = (oid, name, username, email, password, role) => {
      statuses.value.push({
        oid: oid,
        name: name,
        username: username,
        email: email,
        password: password,
        role: role,
      })
    }
    const updateStatus = (id, name, description, createdOn, updateOn) => {
      statuses.value = statuses.value.map((status) => {
        return status.id === id
          ? { ...status, name: name,
            description: description,
            createdOn: createdOn,
            updateOn: updateOn, }
          : status
      })
    }
    const removeStatus = (removeId) => {
      statuses.value.splice(
        statuses.value.findIndex((status) => status.id === removeId),
        1
      )
    }
    return {
      getUsers,
      addUsers,
      addUser,
      
    }
  })

  export { useUsers }

  
if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot))
  }