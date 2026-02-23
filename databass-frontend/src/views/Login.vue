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
let popupTimeout = null

const showPopup = (msg) => {
  errorMessage.value = msg
  if (popupTimeout) clearTimeout(popupTimeout)
  popupTimeout = setTimeout(() => {
    errorMessage.value = ''
  }, 4000)
}

const handleLogin = async () => {
  errorMessage.value = ''
  isLoading.value = true
  
  // 🛡️ CUSTOM VALIDATION
  if (!username.value.trim()) {
    showPopup('USERNAME IS REQUIRED.')
    isLoading.value = false
    return
  }
  
  if (!password.value) {
    showPopup('PASSWORD IS REQUIRED.')
    isLoading.value = false
    return
  }
  
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
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403 || error.response.status === 400) {
        showPopup('INVALID CREDENTIALS DETECTED')
      } else {
        showPopup(`SERVER ERROR: ${error.response.status}`)
      }
    } else {
      showPopup('CONNECTION LOST. CHECK SERVER.')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-slate-950 px-4 relative overflow-hidden">
    <div class="absolute top-[-10%] left-[-10%] w-96 h-96 animate-float-1 flex items-center justify-center pointer-events-none z-0">
      <div class="absolute inset-0 bg-yellow-500/20 rounded-full blur-3xl"></div>
      <span class="relative text-3xl tracking-[0.3em] text-slate-950 z-10" style="font-family: 'Orbitron', sans-serif; font-weight: 900;">
        DATABASS
      </span>
    </div>

    <div class="absolute bottom-[-10%] right-[-10%] w-96 h-96 animate-float-2 flex items-center justify-center pointer-events-none z-0">
      <div class="absolute inset-0 bg-lime-500/20 rounded-full blur-3xl"></div>
      <span class="relative text-3xl tracking-[0.3em] text-slate-950 z-10" style="font-family: 'Orbitron', sans-serif; font-weight: 900;">
        DATABASS
      </span>
    </div>

    <div class="animate-fade-in-up w-full max-w-md bg-slate-900/70 backdrop-blur-xl border border-slate-800 p-8 rounded-2xl shadow-[0_0_50px_-12px_rgba(234,179,8,0.25)] relative z-10">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-lime-400 tracking-tight uppercase">
          Welcome Back!
        </h1>
        <p class="text-lime-400/70 mt-2 text-sm font-medium tracking-widest uppercase">Enter Your Credentials</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-wider">Username</label>
          <input 
            v-model="username" 
            type="text" 
            class="w-full bg-slate-950/80 border border-slate-800 text-yellow-50 rounded-lg px-4 py-3 focus:outline-none focus:border-yellow-400 focus:ring-1 focus:ring-yellow-400 transition-all placeholder-slate-600"
            placeholder="DJ_Databass"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-wider">Access Code (Password)</label>
          <input 
            v-model="password" 
            type="password" 
            class="w-full bg-slate-950/80 border border-slate-800 text-yellow-50 rounded-lg px-4 py-3 focus:outline-none focus:border-yellow-400 focus:ring-1 focus:ring-yellow-400 transition-all placeholder-slate-600"
            placeholder="••••••••"
          />
        </div>

        <button 
          type="submit" 
          :disabled="isLoading"
          class="w-full bg-gradient-to-r from-yellow-500 to-yellow-400 hover:from-yellow-400 hover:to-yellow-300 text-slate-950 font-black uppercase tracking-widest py-3 px-4 rounded-lg shadow-[0_0_20px_rgba(234,179,8,0.3)] transform hover:-translate-y-0.5 transition-all focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isLoading ? 'Connecting...' : 'Log In' }}
        </button>
      </form>

      <div class="mt-6 text-center text-sm text-slate-500">
        NO CLEARANCE? 
        <router-link to="/register" class="text-lime-400 hover:text-lime-300 font-bold transition-colors ml-1 underline decoration-lime-400/30 underline-offset-4">
          REGISTER HERE
        </router-link>
      </div>
    </div>

    <transition name="toast" mode="out-in">
      <div v-if="errorMessage" :key="errorMessage" class="fixed bottom-6 left-6 z-50">
        <div class="bg-slate-950/90 backdrop-blur-md border-l-4 border-red-500 pl-4 pr-6 py-4 rounded-r-lg shadow-[0_0_30px_rgba(239,68,68,0.2)] flex items-center gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span class="text-red-400 font-black uppercase tracking-widest text-xs">{{ errorMessage }}</span>
        </div>
      </div>
    </transition>
  </div>
</template>