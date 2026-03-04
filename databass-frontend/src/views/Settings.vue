<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const profileForm = ref({ bio: '', avatarUrl: '' })

const emailForm = ref({ current: '', new: '' })
const passwordForm = ref({ current: '', new: '', confirm: '' })

const message = ref('')
const messageType = ref('')

const showMessage = (msg, type = 'success') => {
  message.value = msg
  messageType.value = type
  setTimeout(() => message.value = '', 4000)
}

onMounted(async () => {
  try {
    const response = await api.get('/users/settings')
    emailForm.value.current = response.data.email
    profileForm.value.bio = response.data.bio
    profileForm.value.avatarUrl = response.data.avatarUrl
  } catch (error) {
    console.error("Failed to load settings.")
  }
})

const handleUpdateProfile = async () => {
  try {
    await api.put('/users/profile', profileForm.value)
    showMessage("PROFILE UPDATED SUCCESSFULLY.")
  } catch (error) {
    showMessage("FAILED TO UPDATE PROFILE.", "error")
  }
}

const handleUpdateEmail = async () => {
  if (!emailForm.value.new) return
  try {
    await api.put('/users/email', { email: emailForm.value.new })
    emailForm.value.current = emailForm.value.new
    emailForm.value.new = ''
    showMessage("EMAIL UPDATED SUCCESSFULLY.")
  } catch (error) {
    showMessage("FAILED TO UPDATE EMAIL.", "error")
  }
}

const handleUpdatePassword = async () => {
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    showMessage("NEW PASSWORDS DO NOT MATCH.", "error")
    return
  }
  try {
    await api.put('/users/password', { 
      current: passwordForm.value.current, 
      new: passwordForm.value.new 
    })
    passwordForm.value = { current: '', new: '', confirm: '' }
    showMessage("PASSWORD UPDATED SUCCESSFULLY.")
  } catch (error) {
    showMessage(error.response?.data?.error || "FAILED TO UPDATE PASSWORD.", "error")
  }
}
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div class="mb-10">
      <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200">
        System <span class="text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">Settings</span>
      </h1>
      <p class="text-slate-500 font-bold uppercase tracking-widest mt-2">Manage Your Databass Account</p>
    </div>

    <div v-if="message" class="mb-6 p-4 rounded-xl border flex items-center gap-3 transition-all" :class="messageType === 'error' ? 'bg-red-500/10 border-red-500/30 text-red-400' : 'bg-lime-500/10 border-lime-500/30 text-lime-400'">
      <svg v-if="messageType === 'error'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
      <span class="text-xs font-black uppercase tracking-widest">{{ message }}</span>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      
      <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-6 shadow-2xl h-fit">
        <h2 class="text-sm font-black text-slate-300 uppercase tracking-widest border-b border-slate-800 pb-4 mb-6 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-lime-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
          Public Profile
        </h2>

        <div class="space-y-6">
          
          <div class="flex items-start gap-6">
            <div class="h-20 w-20 rounded-full bg-slate-800 border-2 border-slate-700 overflow-hidden shrink-0 flex items-center justify-center">
              <img v-if="profileForm.avatarUrl" :src="profileForm.avatarUrl" alt="Avatar" class="h-full w-full object-cover" />
              <span v-else class="text-2xl font-black text-slate-500 uppercase">{{ authStore.user?.charAt(0) || 'D' }}</span>
            </div>
            <div class="flex-1 space-y-2">
              <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">Avatar Image URL</label>
              <input v-model="profileForm.avatarUrl" type="url" placeholder="https://example.com/image.png" class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-sm text-slate-200 focus:border-lime-500 focus:outline-none font-bold" />
            </div>
          </div>

          <div>
            <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Producer Bio</label>
            <textarea 
              v-model="profileForm.bio" 
              rows="4" 
              placeholder="Tell the world about your sound..." 
              class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-4 text-sm text-slate-200 focus:border-lime-500 focus:outline-none font-medium resize-none"
            ></textarea>
          </div>

          <button @click="handleUpdateProfile" class="w-full bg-lime-500 hover:bg-lime-400 text-slate-950 shadow-[0_0_15px_rgba(132,204,22,0.4)] transition-all font-black uppercase tracking-widest text-xs py-3 rounded-lg">
            Save Profile
          </button>
        </div>
      </div>

      <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-6 shadow-2xl h-fit">
        <h2 class="text-sm font-black text-slate-300 uppercase tracking-widest border-b border-slate-800 pb-4 mb-6 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
          Security & Access
        </h2>

        <div class="space-y-4 mb-8">
          <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">Update Email</h3>
          <div class="space-y-3">
            <input v-model="emailForm.current" type="email" disabled placeholder="No email set" class="w-full bg-slate-950/30 border border-slate-800 rounded-lg p-3 text-sm text-slate-500 cursor-not-allowed font-bold" />
            <input v-model="emailForm.new" type="email" placeholder="New Email Address" class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-sm text-slate-200 focus:border-yellow-500 focus:outline-none font-bold" />
            <button @click="handleUpdateEmail" class="w-full bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 hover:border-slate-500 transition-all font-black uppercase tracking-widest text-xs py-3 rounded-lg">
              Save Email
            </button>
          </div>
        </div>

        <div class="space-y-4 pt-6 border-t border-slate-800/50">
          <h3 class="text-xs font-black text-slate-500 uppercase tracking-widest">Update Password</h3>
          <div class="space-y-3">
            <input v-model="passwordForm.current" type="password" placeholder="Current Password" class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-sm text-slate-200 focus:border-yellow-500 focus:outline-none font-bold" />
            <input v-model="passwordForm.new" type="password" placeholder="New Password" class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-sm text-slate-200 focus:border-yellow-500 focus:outline-none font-bold" />
            <input v-model="passwordForm.confirm" type="password" placeholder="Confirm New Password" class="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-sm text-slate-200 focus:border-yellow-500 focus:outline-none font-bold" />
            <button @click="handleUpdatePassword" class="w-full bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 hover:border-slate-500 transition-all font-black uppercase tracking-widest text-xs py-3 rounded-lg">
              Save Password
            </button>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>