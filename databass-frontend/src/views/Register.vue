<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const popupMessage = ref('')
const popupType = ref('error')
const isLoading = ref(false)
let popupTimeout = null

const showPopup = (msg, type = 'error') => {
  popupMessage.value = msg
  popupType.value = type
  if (popupTimeout) clearTimeout(popupTimeout)
  
  if (type === 'error') {
    popupTimeout = setTimeout(() => {
      popupMessage.value = ''
    }, 4000)
  }
}

const handleRegister = async () => {
  popupMessage.value = ''
  isLoading.value = true
  
  if (!email.value.trim()) {
    showPopup('EMAIL ADDRESS IS REQUIRED.', 'error')
    isLoading.value = false
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    showPopup('INVALID EMAIL FORMAT. MISSING DOMAIN.', 'error')
    isLoading.value = false
    return
  }

  if (!username.value.trim()) {
    showPopup('DESIRED USERNAME IS REQUIRED.', 'error')
    isLoading.value = false
    return
  }

  if (!password.value) {
    showPopup('ACCESS CODE IS REQUIRED.', 'error')
    isLoading.value = false
    return
  }

  try {
    await api.post('/auth/register', {
      username: username.value,
      email: email.value, 
      password: password.value
    })

    showPopup('ACCOUNT CREATED. REDIRECTING...', 'success')
    
    setTimeout(() => {
      router.push('/login')
    }, 2000)

  } catch (error) {
    if (error.response && error.response.status === 400) {
      showPopup('REGISTRATION FAILED. USERNAME MAY EXIST.', 'error')
    } else {
      showPopup('NETWORK ERROR. SERVER DOWN?', 'error')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-slate-950 px-4 relative overflow-hidden">
    <div class="absolute top-[-10%] right-[-10%] w-96 h-96 animate-float-3 flex items-center justify-center pointer-events-none z-0">
      <div class="absolute inset-0 bg-lime-500/20 rounded-full blur-3xl"></div>
      <span class="relative text-3xl tracking-[0.3em] text-slate-950 z-10" style="font-family: 'Orbitron', sans-serif; font-weight: 900;">
        DATABASS
      </span>
    </div>

    <div class="absolute bottom-[-10%] left-[-10%] w-96 h-96 animate-float-4 flex items-center justify-center pointer-events-none z-0">
      <div class="absolute inset-0 bg-yellow-500/20 rounded-full blur-3xl"></div>
      <span class="relative text-3xl tracking-[0.3em] text-slate-950 z-10" style="font-family: 'Orbitron', sans-serif; font-weight: 900;">
        DATABASS
      </span>
    </div>
    <div class="animate-fade-in-up w-full max-w-md bg-slate-900/70 backdrop-blur-xl border border-slate-800 p-8 rounded-2xl shadow-[0_0_50px_-12px_rgba(132,204,22,0.25)] relative z-10">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400 tracking-tight uppercase">
          Ready to Produce?
        </h1>
        <p class="text-yellow-400/70 mt-2 text-sm font-medium tracking-widest uppercase">Request Access</p>
      </div>

      <form @submit.prevent="handleRegister" novalidate class="space-y-5">
        <div>
          <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-wider">Email Address</label>
          <input 
            v-model="email" 
            type="email" 
            class="w-full bg-slate-950/80 border border-slate-800 text-lime-50 rounded-lg px-4 py-3 focus:outline-none focus:border-lime-400 focus:ring-1 focus:ring-lime-400 transition-all placeholder-slate-600"
            placeholder="dj@example.com"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-wider">Desired Username</label>
          <input 
            v-model="username" 
            type="text" 
            class="w-full bg-slate-950/80 border border-slate-800 text-lime-50 rounded-lg px-4 py-3 focus:outline-none focus:border-lime-400 focus:ring-1 focus:ring-lime-400 transition-all placeholder-slate-600"
            placeholder="DJ_Databass"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-400 mb-1 uppercase tracking-wider">Access Code (Password)</label>
          <input 
            v-model="password" 
            type="password" 
            class="w-full bg-slate-950/80 border border-slate-800 text-lime-50 rounded-lg px-4 py-3 focus:outline-none focus:border-lime-400 focus:ring-1 focus:ring-lime-400 transition-all placeholder-slate-600"
            placeholder="••••••••"
          />
        </div>

        <button 
          type="submit" 
          :disabled="isLoading || popupType === 'success'"
          class="w-full mt-4 bg-gradient-to-r from-lime-500 to-lime-400 hover:from-lime-400 hover:to-lime-300 text-slate-950 font-black uppercase tracking-widest py-3 px-4 rounded-lg shadow-[0_0_20px_rgba(132,204,22,0.3)] transform hover:-translate-y-0.5 transition-all focus:outline-none focus:ring-2 focus:ring-lime-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ isLoading ? 'Processing...' : 'Create Account' }}
        </button>
      </form>

      <div class="mt-6 text-center text-sm text-slate-500">
        ALREADY A PRODUCER?
        <router-link to="/login" class="text-yellow-400 hover:text-yellow-300 font-bold transition-colors ml-1 underline decoration-yellow-400/30 underline-offset-4">
          LOGIN HERE
        </router-link>
      </div>
    </div>

    <transition name="toast" mode="out-in">
      <div v-if="popupMessage" :key="popupMessage" class="fixed bottom-6 left-6 z-50">
        <div 
          class="bg-slate-950/90 backdrop-blur-md border-l-4 pl-4 pr-6 py-4 rounded-r-lg flex items-center gap-3"
          :class="popupType === 'error' ? 'border-red-500 shadow-[0_0_30px_rgba(239,68,68,0.2)]' : 'border-lime-500 shadow-[0_0_30px_rgba(132,204,22,0.2)]'"
        >
          <svg v-if="popupType === 'error'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-lime-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          
          <span class="font-black uppercase tracking-widest text-xs" :class="popupType === 'error' ? 'text-red-400' : 'text-lime-400'">
            {{ popupMessage }}
          </span>
        </div>
      </div>
    </transition>
  </div>
</template>