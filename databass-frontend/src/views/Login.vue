<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api/axios'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const handleLogin = async () => {
  errorMessage.value = ''
  isLoading.value = true
  
  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    })

    const actualToken = response.data.token || response.data.jwt || response.data.accessToken || response.data

    if (!actualToken || actualToken === 'undefined') {
        throw new Error("Backend did not return a valid token.")
    }

    authStore.setAuth(actualToken, username.value)
    
    router.push('/dashboard')
  } catch (error) {
    console.error("Login Error:", error)
    
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403 || error.response.status === 400) {
        errorMessage.value = 'Invalid username or password.'
      } else {
        errorMessage.value = `Server error: ${error.response.status}`
      }
    } else {
      errorMessage.value = 'Failed to connect to the server.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-slate-950 px-4 relative overflow-hidden">
    <div class="absolute top-[-10%] left-[-10%] w-96 h-96 bg-purple-600/20 rounded-full blur-3xl"></div>
    <div class="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-cyan-600/20 rounded-full blur-3xl"></div>

    <div class="w-full max-w-md bg-slate-900/60 backdrop-blur-xl border border-slate-800 p-8 rounded-2xl shadow-2xl relative z-10">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-cyan-400 tracking-tight">
          Databass
        </h1>
        <p class="text-slate-400 mt-2 text-sm">Enter the AI Music Studio</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-1">Username</label>
          <input 
            v-model="username" 
            type="text" 
            required
            class="w-full bg-slate-950/50 border border-slate-700 text-white rounded-lg px-4 py-3 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all placeholder-slate-500"
            placeholder="DJ_Databass"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-300 mb-1">Password</label>
          <input 
            v-model="password" 
            type="password" 
            required
            class="w-full bg-slate-950/50 border border-slate-700 text-white rounded-lg px-4 py-3 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all placeholder-slate-500"
            placeholder="••••••••"
          />
        </div>

        <div v-if="errorMessage" class="text-red-400 text-sm bg-red-900/20 border border-red-900/50 p-3 rounded-lg text-center">
          {{ errorMessage }}
        </div>

        <button 
          type="submit" 
          :disabled="isLoading"
          class="w-full bg-gradient-to-r from-purple-600 to-purple-500 hover:from-purple-500 hover:to-purple-400 text-white font-bold py-3 px-4 rounded-lg shadow-lg shadow-purple-900/50 transform hover:-translate-y-0.5 transition-all focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isLoading ? 'Connecting...' : 'Login' }}
        </button>
      </form>

      <div class="mt-6 text-center text-sm text-slate-400">
        Don't have an account? 
        <router-link to="/register" class="text-cyan-400 hover:text-cyan-300 font-medium transition-colors">
          Create one
        </router-link>
      </div>
    </div>
  </div>
</template>