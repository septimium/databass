<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter, useRoute } from 'vue-router'
import api from '../api/axios'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const isSidebarOpen = ref(true)

const credits = ref(0) 
const creditDiff = ref(0)
const showCreditAnim = ref(false)

const popupMessage = ref('')
const popupType = ref('success')
let popupTimeout = null

const showPopup = (msg, type = 'success') => {
  popupMessage.value = msg
  popupType.value = type
  if (popupTimeout) clearTimeout(popupTimeout)
  popupTimeout = setTimeout(() => popupMessage.value = '', 4000)
}

onMounted(async () => {
  try {
    const response = await api.get('/users/me')
    authStore.user = response.data.username 
    credits.value = response.data.credits 
  } catch (error) {
    console.error("Failed to sync user profile. Token might be expired.")
    handleLogout() 
  }
})

const handleClaimDaily = async () => {
  try {
    const response = await api.post('/credits/claim')
    
    creditDiff.value = response.data.creditsRewarded
    credits.value = response.data.totalCredits 
    
    showCreditAnim.value = false 
    setTimeout(() => showCreditAnim.value = true, 10) 

    const streakBonus = response.data.creditsRewarded - 50;
    
    if (streakBonus > 0) {
      showPopup(`+50 BASE & +${streakBonus} STREAK BONUS! (DAY ${response.data.currentStreak}) 🔥`, 'success')
    } else {
      showPopup(`+50 CREDITS! STREAK INITIATED (DAY 1) `, 'success')
    }

  } catch (error) {
    if (error.response && error.response.data && error.response.data.error) {
      showPopup(error.response.data.error.toUpperCase(), 'error')
    } 
    else if (error.response && error.response.status === 400) {
      showPopup('ALREADY CLAIMED TODAY. COME BACK TOMORROW.', 'error')
    } 
    else {
      showPopup('SYSTEM ERROR. COULD NOT CLAIM.', 'error')
    }
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const navItems = [
  { name: 'Studio', path: '/dashboard', icon: 'M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z' },
  { name: 'Library', path: '/dashboard', icon: 'M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3' },
  { name: 'Discover', path: '/dashboard', icon: 'M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9' },
  { name: 'Settings', path: '/dashboard', icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z' }
]
</script>

<template>
  <div class="h-screen w-full bg-slate-950 flex overflow-hidden font-sans text-slate-300">
    
    <aside 
      :class="isSidebarOpen ? 'w-64 translate-x-0' : 'w-0 -translate-x-full'"
      class="fixed inset-y-0 left-0 z-50 bg-slate-900/90 backdrop-blur-2xl border-r border-slate-800 transition-all duration-300 ease-in-out flex flex-col md:relative overflow-hidden whitespace-nowrap"
    >
      <div class="h-16 flex items-center px-6 border-b border-slate-800 shrink-0">
        <div class="text-2xl font-black tracking-[0.2em] flex" style="font-family: 'Orbitron', sans-serif;">
          <span class="flicker-1 text-yellow-400 drop-shadow-[0_0_8px_rgba(250,204,21,0.6)]">D</span>
          <span class="flicker-2 text-yellow-400 drop-shadow-[0_0_8px_rgba(250,204,21,0.6)]">A</span>
          <span class="flicker-3 text-yellow-400 drop-shadow-[0_0_8px_rgba(250,204,21,0.6)]">T</span>
          <span class="flicker-4 text-yellow-400 drop-shadow-[0_0_8px_rgba(250,204,21,0.6)]">A</span>
          
          <span class="flicker-2 text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.6)]">B</span>
          <span class="flicker-1 text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.6)]">A</span>
          <span class="flicker-4 text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.6)]">S</span>
          <span class="flicker-3 text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.6)]">S</span>
        </div>
      </div>

      <nav class="flex-1 overflow-y-auto py-6 px-3 space-y-2">
        <router-link 
          v-for="item in navItems" 
          :key="item.name"
          :to="item.path"
          :class="[
            route.path === item.path && item.name === 'Studio'
              ? 'bg-gradient-to-r from-lime-500/20 to-transparent border-l-2 border-lime-400 text-lime-400' 
              : 'hover:bg-slate-800/50 text-slate-400 hover:text-slate-200 border-l-2 border-transparent',
            'w-full flex items-center gap-3 px-4 py-3 rounded-r-lg transition-all text-sm font-bold tracking-wider uppercase'
          ]"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="item.icon" />
            <path v-if="item.name === 'Settings'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          {{ item.name }}
        </router-link>
      </nav>

      <div class="p-4 border-t border-slate-800">
        <button @click="handleLogout" class="flex items-center gap-3 w-full px-4 py-2 text-sm font-bold text-slate-500 hover:text-red-400 transition-colors uppercase tracking-wider">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Disconnect
        </button>
      </div>
    </aside>

    <main class="flex-1 flex flex-col min-w-0 transition-all duration-300 ease-in-out relative z-10">
      
      <header class="h-16 shrink-0 bg-slate-900/60 backdrop-blur-xl border-b border-slate-800 flex items-center justify-between px-4 sm:px-6 relative z-20">
        <button 
          @click="isSidebarOpen = !isSidebarOpen"
          class="p-2 text-slate-400 hover:text-lime-400 transition-colors rounded-lg hover:bg-slate-800 focus:outline-none"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <div class="flex items-center gap-6">
          
          <button @click="handleClaimDaily" class="hidden sm:block text-xs font-black uppercase tracking-widest text-lime-400 hover:text-lime-300 border border-lime-500/30 hover:border-lime-400 bg-lime-500/10 px-3 py-1.5 rounded-lg transition-all">
            Claim Daily
          </button>

          <div class="relative flex items-center gap-2 bg-slate-950/80 border border-slate-700 px-4 py-1.5 rounded-full shadow-[0_0_15px_rgba(132,204,22,0.1)]">
            <span class="text-lime-400 text-lg drop-shadow-[0_0_8px_rgba(132,204,22,0.8)]">🎵</span>
            <span class="font-black text-slate-200 tracking-wider">{{ credits }}</span>
            <span 
              v-if="showCreditAnim" 
              class="absolute right-0 top-0 font-black text-sm drop-shadow-md animate-credit-float pointer-events-none text-lime-400"
            >
              +{{ creditDiff }}
            </span>
          </div>

          <router-link :to="`/dashboard/profile/${authStore.user || 'DJ_Guest'}`" class="flex items-center gap-3 pl-4 border-l border-slate-700 hover:opacity-80 transition-opacity cursor-pointer group">
            <div class="text-right hidden sm:block">
              <div class="text-xs font-black text-slate-200 uppercase tracking-widest group-hover:text-lime-400 transition-colors">{{ authStore.user || 'DJ_Guest' }}</div>
            </div>
            <div class="h-9 w-9 rounded-full bg-gradient-to-br from-yellow-400 to-lime-500 p-0.5 shadow-lg shadow-lime-900/50 group-hover:shadow-lime-400/50 transition-shadow">
              <div class="h-full w-full bg-slate-900 rounded-full flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
            </div>
          </router-link>
        </div>
      </header>

      <div class="flex-1 overflow-y-auto relative">
        <div class="absolute top-1/4 left-1/4 w-[500px] h-[500px] bg-lime-600/10 rounded-full blur-[100px] pointer-events-none"></div>
        
        <router-view :key="route.fullPath"></router-view>
      </div>
    </main>

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
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
          
          <span class="font-black uppercase tracking-widest text-xs" :class="popupType === 'error' ? 'text-red-400' : 'text-lime-400'">
            {{ popupMessage }}
          </span>
        </div>
      </div>
    </transition>
  </div>
</template>