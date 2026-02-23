<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

const username = ref('')
const email = ref('') 
const password = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const isLoading = ref(false)

const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  isLoading.value = true
  
  try {
    await api.post('/auth/register', {
      username: username.value,
      email: email.value, 
      password: password.value
    })

    successMessage.value = 'Account created! Redirecting...'
    
    setTimeout(() => {
      router.push('/login')
    }, 2000)

  } catch (error) {
    console.error("Register Error:", error)
    if (error.response && error.response.status === 400) {
      errorMessage.value = 'Registration failed. Check inputs or username.'
    } else {
      errorMessage.value = 'Network error. Is Spring Boot running?'
    }
  } finally {
    isLoading.value = false 
  }
}
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-slate-950 px-4 relative overflow-hidden">
    <div class="absolute top-[-10%] right-[-10%] w-96 h-96 bg-cyan-600/20 rounded-full blur-3xl"></div>
    <div class="absolute bottom-[-10%] left-[-10%] w-96 h-96 bg-purple-600/20 rounded-full blur-3xl"></div>

    <div class="w-full max-w-md bg-slate-900/60 backdrop-blur-xl border border-slate-800 p-8 rounded-2xl shadow-2xl relative z-10">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-purple-400 tracking-tight">
          Join Databass
        </h1>
        <p class="text-slate-400 mt-2 text-sm">Start generating AI music today</p>
      </div>

      <form @submit.prevent="handleRegister" class="space-y-5">
        
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-1">Email</label>
          <input 
            v-model="email" 
            type="email" 
            required
            class="w-full bg-slate-950/50 border border-slate-700 text-white rounded-lg px-4 py-3 focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-all placeholder-slate-500"
            placeholder="dj@example.com"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-300 mb-1">Username</label>
          <input 
            v-model="username" 
            type="text" 
            required
            class="w-full bg-slate-950/50 border border-slate-700 text-white rounded-lg px-4 py-3 focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-all placeholder-slate-500"
            placeholder="DJ_Databass"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-slate-300 mb-1">Password</label>
          <input 
            v-model="password" 
            type="password" 
            required
            class="w-full bg-slate-950/50 border border-slate-700 text-white rounded-lg px-4 py-3 focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-all placeholder-slate-500"
            placeholder="••••••••"
          />
        </div>

        <div v-if="errorMessage" class="text-red-400 text-sm bg-red-900/20 border border-red-900/50 p-3 rounded-lg text-center">
          {{ errorMessage }}
        </div>
        
        <div v-if="successMessage" class="text-emerald-400 text-sm bg-emerald-900/20 border border-emerald-900/50 p-3 rounded-lg text-center">
          {{ successMessage }}
        </div>

        <button 
          type="submit" 
          :disabled="isLoading || !!successMessage"
          class="w-full bg-gradient-to-r from-cyan-600 to-cyan-500 hover:from-cyan-500 hover:to-cyan-400 text-white font-bold py-3 px-4 rounded-lg shadow-lg shadow-cyan-900/50 transform hover:-translate-y-0.5 transition-all focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed mt-2"
        >
          {{ isLoading ? 'Creating account...' : 'Register' }}
        </button>
      </form>

      <div class="mt-6 text-center text-sm text-slate-400">
        Already have an account? 
        <router-link to="/login" class="text-purple-400 hover:text-purple-300 font-medium transition-colors">
          Login here
        </router-link>
      </div>
    </div>
  </div>
</template>