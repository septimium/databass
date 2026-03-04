<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

const tracks = ref([])
const isLoading = ref(true)
const currentSort = ref('recent') 

const currentAudio = ref(null)
const playingTrackId = ref(null)

onMounted(() => {
  fetchTracks()
})

onUnmounted(() => {
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
  }
})

watch(currentSort, () => {
  fetchTracks()
})

const fetchTracks = async () => {
  isLoading.value = true
  try {
    const response = await api.get(`/music/feed?sort=${currentSort.value}`)
    tracks.value = response.data
  } catch (error) {
    console.error("Failed to load discover feed.", error)
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

const goToProfile = (username) => {
  router.push(`/profile/${username}`)
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString(undefined, { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div class="mb-10 flex flex-col md:flex-row justify-between items-end gap-6">
      <div>
        <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200">
          Global <span class="text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">Discover</span>
        </h1>
        <p class="text-slate-500 font-bold uppercase tracking-widest mt-2">Explore the Databass Network</p>
      </div>

      <div class="flex bg-slate-900 p-1 rounded-lg border border-slate-800">
        <button 
          @click="currentSort = 'recent'"
          :class="currentSort === 'recent' ? 'bg-slate-800 text-lime-400 shadow-lg' : 'text-slate-500 hover:text-slate-300'"
          class="px-6 py-2 rounded-md font-black uppercase tracking-widest text-xs transition-all"
        >
          Fresh Drops
        </button>
        <button 
          @click="currentSort = 'popular'"
          :class="currentSort === 'popular' ? 'bg-slate-800 text-yellow-400 shadow-lg' : 'text-slate-500 hover:text-slate-300'"
          class="px-6 py-2 rounded-md font-black uppercase tracking-widest text-xs transition-all"
        >
          Top Rated
        </button>
      </div>
    </div>

    <div v-if="isLoading" class="flex flex-col items-center justify-center mt-20">
      <div class="w-12 h-12 border-4 border-lime-500/30 border-t-lime-400 rounded-full animate-spin shadow-[0_0_15px_rgba(132,204,22,0.5)]"></div>
      <p class="mt-4 text-lime-400 font-bold uppercase tracking-widest text-sm animate-pulse">Scanning Network...</p>
    </div>

    <div v-else-if="tracks.length === 0" class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-12 text-center shadow-2xl">
      <p class="text-slate-500 font-bold text-sm tracking-wider uppercase">No public tracks found yet.</p>
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
              playingTrackId === track.id ? 'bg-lime-500 text-slate-950 shadow-[0_0_15px_rgba(132,204,22,0.6)]' : 'bg-lime-500/10 text-lime-400 group-hover:bg-lime-500/20'
            ]"
          >
            <svg v-if="playingTrackId === track.id" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zM7 8a1 1 0 012 0v4a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" /></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" /></svg>
          </button>

          <div class="min-w-0">
            <h3 class="text-sm font-black text-slate-200 truncate pr-4">{{ track.title || 'Untitled Track' }}</h3>
            
            <div class="flex items-center gap-2 mt-1">
              <button 
                @click.stop="goToProfile(track.producer)"
                class="text-[10px] font-black uppercase tracking-widest text-lime-400 hover:text-white hover:underline transition-all flex items-center gap-1"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
                {{ track.producer }}
              </button>

              <span class="text-slate-600 text-[10px]">•</span>
              
              <span class="text-[10px] font-bold text-slate-500 uppercase tracking-widest">
                {{ formatDateTime(track.createdAt) }}
              </span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-6">
          
          <div class="flex items-center gap-2 text-slate-400">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
            <span class="text-xs font-black tracking-widest">{{ track.streamCount || 0 }}</span>
          </div>

          <div class="text-xs font-bold text-slate-500 uppercase tracking-widest">
            {{ formatDuration(track.duration) }}
          </div>
        </div>

      </div>
    </div>

  </div>
</template>