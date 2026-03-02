<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import api from '../api/axios'

const tracks = ref([])
const isLoading = ref(true)

const currentAudio = ref(null)
const playingTrackId = ref(null)

const isEditModalOpen = ref(false)
const editingTrack = ref(null)
const editTitle = ref('')
const editPublic = ref(true)

onMounted(async () => {
  await fetchHistory()
})

onUnmounted(() => {
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
  }
})

const fetchHistory = async () => {
  try {
    const response = await api.get('/music/history')
    tracks.value = response.data
  } catch (error) {
    console.error("Failed to load library data.", error)
  } finally {
    isLoading.value = false
  }
}

const togglePlay = (track) => {
  if (track.status === 'PENDING' || !track.s3Url) return

  if (playingTrackId.value === track.id) {
    currentAudio.value.pause()
    playingTrackId.value = null
    return
  }

  if (currentAudio.value) currentAudio.value.pause()

  currentAudio.value = new Audio(track.s3Url)
  currentAudio.value.play()
  playingTrackId.value = track.id

  api.post(`/music/${track.id}/stream`).then(response => {
    track.streamCount = response.data.streamCount
  }).catch(e => console.error("Failed to count stream", e))

  currentAudio.value.onended = () => {
    playingTrackId.value = null
  }
}

const openEditModal = (track) => {
  editingTrack.value = track
  editTitle.value = track.title || 'Untitled Track'
  editPublic.value = track.public
  isEditModalOpen.value = true
}

const closeEditModal = () => {
  isEditModalOpen.value = false
  editingTrack.value = null
}

const saveEdit = async () => {
  if (!editingTrack.value) return
  try {
    const payload = { title: editTitle.value, isPublic: editPublic.value }
    await api.put(`/music/${editingTrack.value.id}`, payload)
    
    const index = tracks.value.findIndex(t => t.id === editingTrack.value.id)
    if (index !== -1) {
      tracks.value[index].title = editTitle.value
      tracks.value[index].public = editPublic.value
    }
    closeEditModal()
  } catch (error) {
    console.error("Failed to update track.", error)
  }
}

const deleteTrack = async () => {
  if (!editingTrack.value) return
  if (!confirm("Are you sure you want to permanently delete this track?")) return

  try {
    await api.delete(`/music/${editingTrack.value.id}`)
    tracks.value = tracks.value.filter(t => t.id !== editingTrack.value.id)
    
    if (playingTrackId.value === editingTrack.value.id) {
      currentAudio.value.pause()
      playingTrackId.value = null
    }
    
    closeEditModal()
  } catch (error) {
    console.error("Failed to delete track.", error)
  }
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div class="mb-10 flex justify-between items-end">
      <div>
        <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200">
          Personal <span class="text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">Library</span>
        </h1>
        <p class="text-slate-500 font-bold uppercase tracking-widest mt-2">Manage Your Generated Masterpieces</p>
      </div>
      <div class="hidden md:block text-right">
        <div class="text-3xl font-black text-lime-400">{{ tracks.length }}</div>
        <div class="text-[10px] text-slate-500 font-bold uppercase tracking-widest">Total Tracks</div>
      </div>
    </div>

    <div v-if="isLoading" class="flex flex-col items-center justify-center mt-20">
      <div class="w-12 h-12 border-4 border-lime-500/30 border-t-lime-400 rounded-full animate-spin shadow-[0_0_15px_rgba(132,204,22,0.5)]"></div>
      <p class="mt-4 text-lime-400 font-bold uppercase tracking-widest text-sm animate-pulse">Decrypting Audio Files...</p>
    </div>

    <div v-else-if="tracks.length === 0" class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-12 text-center shadow-2xl">
      <div class="text-slate-600 mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-20 w-20 mx-auto opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3" />
        </svg>
      </div>
      <h2 class="text-xl font-black text-slate-300 uppercase tracking-widest mb-2">Vault is Empty</h2>
      <p class="text-slate-500 font-bold text-sm tracking-wider uppercase mb-6">You haven't generated any tracks yet.</p>
      <router-link to="/studio" class="inline-block bg-lime-500/10 text-lime-400 hover:text-lime-300 border border-lime-500/50 hover:border-lime-400 px-6 py-3 rounded-lg font-black uppercase tracking-widest transition-all">
        Enter Studio
      </router-link>
    </div>

    <div v-else class="space-y-4">
      <div 
        v-for="track in tracks" 
        :key="track.id" 
        class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-xl p-5 shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-6 transition-all hover:border-lime-500/30 group"
        :class="playingTrackId === track.id ? 'border-lime-500/50 shadow-[0_0_20px_rgba(132,204,22,0.1)]' : ''"
      >
        <div class="flex items-center gap-5 flex-1 min-w-0">
          
          <button 
            @click="togglePlay(track)"
            :disabled="track.status === 'PENDING' || !track.s3Url"
            class="shrink-0 w-12 h-12 rounded-full flex items-center justify-center transition-all focus:outline-none"
            :class="[
              track.status === 'PENDING' ? 'bg-slate-800 cursor-not-allowed' :
              playingTrackId === track.id ? 'bg-lime-500 text-slate-950 shadow-[0_0_15px_rgba(132,204,22,0.6)]' : 'bg-lime-500/10 text-lime-400 group-hover:bg-lime-500/20'
            ]"
          >
            <svg v-if="track.status === 'PENDING'" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-slate-500 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" /></svg>
            <svg v-else-if="playingTrackId === track.id" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zM7 8a1 1 0 012 0v4a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" /></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" /></svg>
          </button>

          <div class="min-w-0">
            <div class="flex items-center gap-2">
              <h3 class="text-sm font-black text-lime-400 uppercase tracking-widest truncate">{{ track.title || 'Untitled Track' }}</h3>
              <span v-if="!track.public" class="text-[10px] bg-slate-800 text-slate-400 px-1.5 py-0.5 rounded font-bold uppercase tracking-wider" title="Private Track">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 inline" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
              </span>
            </div>
            
            <p class="text-xs font-bold text-slate-300 truncate pr-4 mt-0.5">{{ track.prompt }}</p>
            
            <div class="flex items-center gap-3 mt-1.5">
              <span class="text-[10px] font-black uppercase tracking-widest text-slate-500">
                {{ new Date(track.createdAt).toLocaleDateString() }}
              </span>
              
              <span class="flex items-center gap-1 text-[10px] font-black uppercase tracking-widest text-slate-400">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                {{ track.streamCount || 0 }}
              </span>
              
              <span v-if="track.status === 'PENDING'" class="text-[10px] font-black uppercase tracking-widest text-yellow-500 bg-yellow-500/10 px-2 py-0.5 rounded border border-yellow-500/20 animate-pulse">Rendering...</span>
              <span v-else-if="track.status === 'COMPLETED' || track.status === 'READY'" class="text-[10px] font-black uppercase tracking-widest text-lime-400 bg-lime-500/10 px-2 py-0.5 rounded border border-lime-500/20">Ready</span>
              <span v-else-if="track.status === 'FAILED'" class="text-[10px] font-black uppercase tracking-widest text-red-400 bg-red-500/10 px-2 py-0.5 rounded border border-red-500/20">Error</span>
              <span v-else-if="track.status" class="text-[10px] font-black uppercase tracking-widest text-slate-400 bg-slate-800 px-2 py-0.5 rounded border border-slate-700">{{ track.status }}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-between w-full md:w-auto gap-8 pl-17 md:pl-0">
          <div class="hidden lg:flex items-center gap-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">
            <span title="Duration">{{ formatDuration(track.duration) }}</span>
            <span class="w-1 h-1 rounded-full bg-slate-700"></span>
            <span title="Temperature">T: {{ track.temperature || 0.85 }}</span>
            <span class="w-1 h-1 rounded-full bg-slate-700"></span>
            <span title="Guidance">G: {{ track.guidanceScale || 3.2 }}</span>
          </div>

          <button 
            @click="openEditModal(track)"
            :disabled="track.status === 'PENDING'"
            class="flex items-center gap-2 px-3 py-1.5 rounded-lg border bg-slate-800/50 border-slate-700 text-slate-300 hover:bg-slate-700 hover:text-white hover:border-slate-500 transition-all disabled:opacity-30 disabled:cursor-not-allowed group/btn"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
            <span class="text-[10px] font-black uppercase tracking-widest hidden sm:block">Edit</span>
          </button>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="isEditModalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-slate-950/80 backdrop-blur-sm" @click="closeEditModal"></div>
        
        <div class="bg-slate-900 border border-slate-700 rounded-2xl w-full max-w-md relative z-10 shadow-2xl overflow-hidden animate-fade-in-up">
          <div class="p-6 border-b border-slate-800 flex justify-between items-center">
            <h2 class="text-lg font-black uppercase tracking-widest text-slate-200">Track Settings</h2>
            <button @click="closeEditModal" class="text-slate-500 hover:text-white"><svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg></button>
          </div>
          
          <div class="p-6 space-y-6">
            
            <div>
              <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Track Title</label>
              <input v-model="editTitle" type="text" class="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-slate-200 focus:border-lime-500 focus:outline-none font-bold" />
            </div>

            <div>
              <label class="block text-xs font-black text-slate-400 uppercase tracking-widest mb-2">Master Prompt</label>
              <div class="w-full bg-slate-950/50 border border-slate-800/50 rounded-lg p-3 text-slate-400 text-sm italic h-24 overflow-y-auto leading-relaxed shadow-inner">
                {{ editingTrack?.prompt }}
              </div>
            </div>

            <div class="flex items-center justify-between bg-slate-950 p-4 rounded-lg border border-slate-800">
              <div>
                <div class="text-sm font-black text-slate-200 uppercase tracking-wider">Public Profile</div>
                <div class="text-[10px] text-slate-500 font-bold uppercase tracking-widest mt-0.5">Show on your producer page</div>
              </div>
              <button @click="editPublic = !editPublic" :class="editPublic ? 'bg-lime-500 shadow-[0_0_15px_rgba(132,204,22,0.3)]' : 'bg-slate-700'" class="w-12 h-6 rounded-full relative transition-all duration-300 focus:outline-none shrink-0">
                <div :class="editPublic ? 'translate-x-6 bg-slate-900' : 'translate-x-1 bg-slate-400'" class="w-4 h-4 rounded-full transition-all duration-300"></div>
              </button>
            </div>

            <button @click="deleteTrack" class="w-full flex items-center justify-center gap-2 py-3 rounded-lg border border-red-500/30 text-red-400 hover:bg-red-500/10 hover:border-red-400 font-black uppercase tracking-widest text-xs transition-all">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              Delete Permanently
            </button>

          </div>

          <div class="p-4 bg-slate-950 border-t border-slate-800 flex justify-end gap-3">
            <button @click="closeEditModal" class="px-5 py-2 rounded-lg text-slate-400 hover:text-white font-bold text-xs uppercase tracking-widest">Cancel</button>
            <button @click="saveEdit" class="px-5 py-2 bg-lime-500 hover:bg-lime-400 text-slate-950 rounded-lg font-black text-xs uppercase tracking-widest shadow-[0_0_15px_rgba(132,204,22,0.4)] transition-all">Save Changes</button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>