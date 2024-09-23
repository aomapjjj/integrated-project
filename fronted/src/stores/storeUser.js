import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";

const useUsers = defineStore("users", () => {
  const users = ref([]);
  const user = ref({ username: "" });
  const token = ref("");
  const boardId = ref("");

  // บันทึก token ใน sessionStorage
  const setToken = (newToken) => {
    token.value = newToken;
    sessionStorage.setItem("token", token.value);
  };

  // ดึง token จาก sessionStorage
  const getToken = () => {
    return token.value || sessionStorage.getItem("token");
  };

  // ดึงข้อมูลจาก sessionStorage ทันทีที่ store ถูกสร้างขึ้น
  if (sessionStorage.getItem("user")) {
    user.value = JSON.parse(sessionStorage.getItem("user"));
  }
  if (sessionStorage.getItem("token")) {
    token.value = sessionStorage.getItem("token");
  }
  if (sessionStorage.getItem("boardId")) {
    boardId.value = sessionStorage.getItem("boardId");
  }

  // บันทึก user ใน sessionStorage
  const setUser = (userName) => {
    user.value = { username: userName };
    sessionStorage.setItem("user", JSON.stringify(user.value));
  };

  const getUser = () => {
    return user.value;
  };

  const setBoard = (newboard) => {
    boardId.value = newboard;
    sessionStorage.setItem("boardId", boardId.value);
  };

  const getBoard = () => {
    return boardId.value
  }

  // บันทึก boardId ใน sessionStorage
  // const setID = (id) => {
  //   boardId.value = id;
  //   sessionStorage.setItem("boardId", boardId.value);
  // };

  // const getID = () => {
  //   return boardId.value || sessionStorage.getItem("boardId");
  // };

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
    setBoard,
    getBoard
  };
});

export { useUsers };

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot));
}
