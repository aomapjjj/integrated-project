import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";

const useUsers = defineStore("users", () => {
  // state
  const users = ref([]);
  const user = ref({ username: '' });

  // Load user data from localStorage if available
  if (localStorage.getItem('user')) {
    user.value = JSON.parse(localStorage.getItem('user'));
  }

  // view
  const getUsers = () => {
    return users.value;
  };

  const getUser = () => {
    return user.value;
  };

  // actions
  const setUser = (userName) => {
    user.value = { username: userName };
    localStorage.setItem('user', JSON.stringify(user.value)); 
  };

  const addUsers = (newUsers) => {
    if (Array.isArray(newUsers)) {
      newUsers.forEach((newUser) => {
        addUser(
          newUser.oid,
          newUser.name,
          newUser.username,
          newUser.email,
          newUser.password,
          newUser.role
        );
      });
    } else {
      console.error("addUsers expects an array");
    }
  };

  const addUser = (oid, name, username, email, password, role) => {
    users.value.push({
      oid: oid,
      name: name,
      username: username,
      email: email,
      password: password,
      role: role,
    });
  };

  const updateStatus = (id, name, description, createdOn, updateOn) => {
    users.value = users.value.map((user) => {
      return user.id === id
        ? {
            ...user,
            name: name,
            description: description,
            createdOn: createdOn,
            updateOn: updateOn,
          }
        : user;
    });
  };

  const removeUser = (removeId) => {
    users.value.splice(
      users.value.findIndex((user) => user.id === removeId),
      1
    );
  };

  return {
    getUsers,
    addUsers,
    addUser,
    updateStatus,
    removeUser,
    setUser,
    getUser
  };
});

export { useUsers };

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot));
}