import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";

const useUsers = defineStore("users", () => {
  const users = ref([]);
  const user = ref({ username: "" });
  const token = ref("");
  const boardId = ref("");

  // บันทึก token ใน localStorage
  const setToken = (newToken) => {
    token.value = newToken;
    localStorage.setItem("token", token.value);
  };

  // ดึง token จาก localStorage
  const getToken = () => {
    return token.value || localStorage.getItem("token");
  };

  // ดึงข้อมูลจาก localStorage ทันทีที่ store ถูกสร้างขึ้น
  if (localStorage.getItem("user")) {
    user.value = JSON.parse(localStorage.getItem("user"));
  }
  if (localStorage.getItem("token")) {
    token.value = localStorage.getItem("token");
  }
  if (localStorage.getItem("boardId")) {
    boardId.value = localStorage.getItem("boardId");
  }

  // บันทึก user ใน localStorage
  const setUser = (userName) => {
    user.value = { username: userName };
    localStorage.setItem("user", JSON.stringify(user.value));
  };

  const getUser = () => {
    return user.value;
  };

  // บันทึก boardId ใน localStorage
  // const setID = (id) => {
  //   boardId.value = id;
  //   localStorage.setItem("boardId", boardId.value);
  // };

  // const getID = () => {
  //   return boardId.value || localStorage.getItem("boardId");
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
  };
});

export { useUsers };

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUsers, import.meta.hot));
}
