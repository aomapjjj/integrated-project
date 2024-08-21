<script setup>
import { jwtDecode } from "jwt-decode";
import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
import { useUsers } from "../stores/storeUser"

const baseUrlUsers = `${import.meta.env.VITE_BASE_URL_MAIN}/login`
const baseUrlUsersvalidate = `${import.meta.env.VITE_BASE_URL_MAIN}/validate-token`


const router = useRouter()
const alertLogin = ref(false)
const userInput = ref('')
const passwordInput = ref('')

const userStore = useUsers()

const isValidUsername = computed(() => {
  return userInput.value && userInput.value.length > 0 && userInput.value.length <= 50
})

const isValidPassword = computed(() => {
  return passwordInput.value && passwordInput.value.length > 0 && passwordInput.value.length <= 14
})

const isFormValid = computed(() => {
  return isValidPassword.value && isValidUsername.value 
  
})

const openHomePage = () => {
  userStore.setUser(userInput.value); 
  console.log(userStore.getUser());
  router.push({ name: 'TaskList' });
};

const showAlert = () => {
  alertLogin.value = true;
  setTimeout(() => {
    alertLogin.value = false;
  }, 2000); 
};

const submitForm = async () => {
  try {
    const response = await fetch(baseUrlUsers, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userName: userInput.value,
        password: passwordInput.value,
      }),
    });

    if (response.status === 200) {
      const data = await response.json(); 
      console.log(data.access_token);
      const decoded = jwtDecode(data.access_token, {header: true});
      console.log(decoded)
      const validateResponse = await fetch(baseUrlUsersvalidate, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${data.access_token}`, 
        },
      });

      if (validateResponse.status === 200) {
        openHomePage(); // ไปที่หน้าหลัก
      } else {
        
        showAlert()
      }
    } else if (response.status === 401) {
      showAlert()
    } else {
      showAlert()
    }
  } catch (error) {
    showAlert()
  }
};

const closeAlert = () => {
  alertLogin.value = false
}

</script>

<template>
  <div class="w-full h-screen bg-customPurple">
    <div
      class="fixed top-4 left-1/2 transform -translate-x-1/2 w-full max-w-md bg-red-100 border border-red-400 text-red-600 px-6 py-4 rounded-md shadow-lg"
      role="alert" v-show="alertLogin">
      <strong class="font-bold text-lg">Error!</strong>
      <span class="itbkk-message block">Username or Password is incorrect.</span>
      <button class="absolute top-2 right-2 p-1 text-red-500 hover:text-red-700" @click="closeAlert">
        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
          <path fill="none" stroke="currentColor" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <div class="flex flex-col md:flex-row min-h-screen">
      <div class="flex-1 flex items-center justify-center p-4 lg:p-16">
        <div class="text-center lg:text-left max-w-lg">
          <div class="flex justify-center lg:justify-start">
            <img src="../image/logo2.png" class="h-40 w-40 lg:h-48 lg:w-48" alt="Kradan Kanban Logo" />
          </div>
          <h1 class="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-white text-shadowCustom">
            Hi, Welcome to <br />
            Kradan Kanban
          </h1>
          <p class="md:text-lg text-white">
            Visualize your work, maximize efficiency, and manage work-in-progress limits with a beautiful board view
            that works for you.
          </p>
        </div>
      </div>

      <!-- Form -->
      <div class="flex-1 flex items-center justify-center p-4 lg:p-16">
        <!-- Main Content Area -->
        <div class="flex-1 flex items-center justify-center">
          <div class="w-full max-w-md rounded-lg shadow-md p-20 bg-white">
            <h2 class="text-2xl font-semibold text-customPink text-center">
              Login
            </h2>
            <p class="mt-2 text-center text-customPink">Access your account</p>
            <form @submit.prevent="submitForm" class="mt-4">
              <!-- User -->
              <div class=" flex items-center mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 256 256">
                  <g fill="#b0b0b0">
                    <path d="M192 96a64 64 0 1 1-64-64a64 64 0 0 1 64 64" opacity=".2" />
                    <path
                      d="M230.92 212c-15.23-26.33-38.7-45.21-66.09-54.16a72 72 0 1 0-73.66 0c-27.39 8.94-50.86 27.82-66.09 54.16a8 8 0 1 0 13.85 8c18.84-32.56 52.14-52 89.07-52s70.23 19.44 89.07 52a8 8 0 1 0 13.85-8M72 96a56 56 0 1 1 56 56a56.06 56.06 0 0 1-56-56" />
                  </g>
                </svg>
                <div class="flex flex-col -mb-6">
                  <input v-model="userInput" id="login" type="text"
                    class="itbkk-username ml-3 px-6 py-2 border border-gray-400 rounded-full focus:outline-none focus:ring-2 focus:ring-customPink"
                    placeholder="Username" maxlength="50" required />
                  <p class="text-xs text-gray-400 ml-auto mr-2 mt-1">
                    {{ userInput.length }}/50
                  </p>
                </div>
              </div>
              <!-- Password -->
              <div class="flex items-center mb-6">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <path fill="#b0b0b0"
                    d="M8 11a1 1 0 0 1-1-1V7a5.002 5.002 0 0 1 8.532-3.542a5.09 5.09 0 0 1 1.306 2.293a1 1 0 0 1-1.934.505l-.002-.007a3.072 3.072 0 0 0-.786-1.379A3.002 3.002 0 0 0 9 7v3a1 1 0 0 1-1 1m4 7a1 1 0 0 1-1-1v-3a1 1 0 1 1 2 0v3a1 1 0 0 1-1 1"
                    opacity=".5" />
                  <path fill="#b0b0b0"
                    d="M17 9H7a3 3 0 0 0-3 3v7a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-7a3 3 0 0 0-3-3m-4 8a1 1 0 0 1-2 0v-3a1 1 0 1 1 2 0z" />
                </svg>
                <div class="flex flex-col mt-6">
                  <input v-model="passwordInput" id="password" type="password" 
                    class="itbkk-password  ml-3 px-6 py-2 border border-gray-400 rounded-full focus:outline-none focus:ring-2 focus:ring-customPink"
                    placeholder="Password" maxlength="14" required />

                  <p class="text-xs text-gray-400 ml-auto mr-2 mt-1">
                    {{ passwordInput.length }}/14
                  </p>
                </div>
              </div>
              <!-- Sign in Btn -->
              <button type="submit" :disabled="!isFormValid" :class="{'disabled' : !isFormValid, '' :isFormValid}"
                class="itbkk-button-signin w-full py-2 px-4 bg-customPink text-white rounded-md disabled:bg-gray-300 disabled:cursor-not-allowed">
                Sign in
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
