<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-slate-950 flex flex-col items-center justify-center text-white">
    <h1 class="text-4xl font-bold text-purple-400 mb-4">Welcome to the Studio, {{ authStore.username }}!</h1>
    <p class="text-slate-400 mb-8">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
    
    <button @click="handleLogout" class="bg-red-600 hover:bg-red-500 px-6 py-2 rounded-lg font-bold transition-colors">
      Logout
    </button>
  </div>
</template>