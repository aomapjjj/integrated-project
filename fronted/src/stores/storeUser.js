import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";

const useUsers = defineStore("users", () => {
  const users = ref([]);
  const user = ref({ username: "" });
  const token = ref("");
  const LoginSuccess = ref(false)
  const refreshToken = ref(""); 
  const boardId = ref("");

  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem("token", token.value);
  };

  const getToken = () => {
    return token.value || localStorage.getItem("token");
  };

  const setLoginSuccess = (statusLogin) => {
    LoginSuccess.value = statusLogin
  };

  const getLoginSuccess = () => {
    return LoginSuccess.value 
  }

  const setRefreshToken = (newRefreshToken) => {
    refreshToken.value = newRefreshToken;
    localStorage.setItem("refresh_token", refreshToken.value);
  };

  const getRefreshToken = () => {
    return refreshToken.value || localStorage.getItem("refresh_token");
  };

  if (localStorage.getItem("user")) {
    user.value = JSON.parse(localStorage.getItem("user"));
  }
  
  if (localStorage.getItem("token")) {
    token.value = localStorage.getItem("token");
  }
  if (localStorage.getItem("refresh_token")) {
    refreshToken.value = localStorage.getItem("refresh_token");
  }
  if (localStorage.getItem("boardId")) {
    boardId.value = localStorage.getItem("boardId");
  }

  const setUser = (userName) => {
    user.value = { username: userName };
    localStorage.setItem("user", JSON.stringify(user.value));
  };

  const getUser = () => {
    return user.value;
  };

  const setBoard = (newBoard) => {
    boardId.value = newBoard;
    localStorage.setItem("boardId", boardId.value);
  };

  const getBoard = () => {
    return boardId.value;
  };

  const removeUser = (removeId) => {
    users.value.splice(
      users.value.findIndex((user) => user.id === removeId),
      1
    );
  };

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
});

export { useUsers };

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot));
}