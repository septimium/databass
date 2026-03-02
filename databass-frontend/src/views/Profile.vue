<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/axios'

const route = useRoute()
const router = useRouter()
const username = route.params.username

const generatedTracks = ref([])
const isLoading = ref(true)
const errorMessage = ref('')

const currentAudio = ref(null)
const playingTrackId = ref(null)

onMounted(async () => {
  try {
    const response = await api.get(`/music/profile/${username}`)
    generatedTracks.value = response.data
  } catch (error) {
    if (error.response && error.response.status === 404) {
      errorMessage.value = 'PRODUCER NOT FOUND IN DATABASS.'
    } else {
      errorMessage.value = 'SYSTEM ERROR. COULD NOT LOAD PROFILE.'
    }
  } finally {
    isLoading.value = false
  }
})

onUnmounted(() => {
  if (currentAudio.value) {
    currentAudio.value.pause()
    currentAudio.value = null
  }
})

const togglePlay = (track) => {
  if (track.status === 'PENDING' || !track.s3Url) return

  if (playingTrackId.value === track.id) {
    currentAudio.value.pause()
    playingTrackId.value = null
    return
  }

  if (currentAudio.value) {
    currentAudio.value.pause()
  }

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

const producerTitle = computed(() => {
  const count = generatedTracks.value.length
  if (count < 5) return 'Bedroom Producer'
  if (count < 15) return 'Local Opener'
  if (count < 50) return 'Underground Resident'
  if (count < 100) return 'Mainstage Headliner'
  if (count < 250) return 'Platinum Architect'
  if (count < 500) return 'Synthwave God'
  return 'Databass Legend'
})

const totalStreams = computed(() => {
  return generatedTracks.value.reduce((sum, track) => sum + (track.streamCount || 0), 0)
})

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div v-if="errorMessage" class="flex flex-col items-center justify-center mt-20 text-center">
      <div class="text-red-500 mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto opacity-80 drop-shadow-[0_0_15px_rgba(239,68,68,0.5)]" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
      </div>
      <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200">{{ errorMessage }}</h1>
      <button @click="router.push('/studio')" class="mt-8 text-lime-400 hover:text-lime-300 font-bold uppercase tracking-widest border border-lime-500/30 px-6 py-2 rounded-lg transition-all hover:border-lime-400 bg-lime-500/10">Return to Studio</button>
    </div>

    <div v-else-if="isLoading" class="flex flex-col items-center justify-center mt-20">
      <div class="w-12 h-12 border-4 border-lime-500/30 border-t-lime-400 rounded-full animate-spin shadow-[0_0_15px_rgba(132,204,22,0.5)]"></div>
      <p class="mt-4 text-lime-400 font-bold uppercase tracking-widest text-sm animate-pulse">Scanning Databass...</p>
    </div>

    <div v-else>
      <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-8 shadow-2xl mb-8">
        <div class="flex flex-col sm:flex-row items-center justify-between gap-6 border-b border-slate-800/80 pb-8 text-center sm:text-left">
          <div class="flex flex-col sm:flex-row items-center gap-6">
            <div class="h-24 w-24 rounded-full bg-gradient-to-br from-yellow-400 to-lime-500 p-1 shadow-[0_0_30px_rgba(132,204,22,0.3)] shrink-0">
              <div class="h-full w-full bg-slate-900 rounded-full flex items-center justify-center text-3xl font-black text-slate-300 uppercase">{{ username.charAt(0) }}</div>
            </div>
            <div>
              <h1 class="text-4xl font-black uppercase tracking-widest text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">{{ username }}</h1>
              <p class="text-slate-500 font-bold uppercase tracking-widest mt-1">{{ producerTitle }}</p>
            </div>
          </div>
        </div>

        <div class="flex gap-12 pt-6 justify-center sm:justify-start">
          <div>
            <div class="text-3xl font-black text-slate-200">{{ generatedTracks.length }}</div>
            <div class="text-xs font-bold text-slate-500 uppercase tracking-widest">Public Tracks</div>
          </div>
          <div>
            <div class="text-3xl font-black text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.4)]">{{ totalStreams }}</div>
            <div class="text-xs font-bold text-slate-500 uppercase tracking-widest">Total Streams</div>
          </div>
        </div>
      </div>

      <div>
        <h3 class="text-sm font-black text-slate-400 uppercase tracking-widest mb-4 ml-2">Generated Masterpieces</h3>
        
        <div v-if="generatedTracks.length === 0" class="text-center py-12 border border-dashed border-slate-800 rounded-xl">
          <p class="text-slate-500 font-bold uppercase tracking-widest">No public tracks found for this producer.</p>
        </div>

        <div v-else class="space-y-3">
          <div 
            v-for="track in generatedTracks" 
            :key="track.id" 
            class="flex items-center justify-between bg-slate-950/50 hover:bg-slate-800/80 border transition-all p-4 rounded-xl group cursor-pointer"
            :class="playingTrackId === track.id ? 'border-lime-500/50 shadow-[0_0_15px_rgba(132,204,22,0.1)]' : 'border-slate-800 hover:border-lime-500/50'"
            @click="togglePlay(track)"
          >
            
            <div class="flex items-center gap-4 flex-1 min-w-0">
              <button 
                :disabled="track.status === 'PENDING' || !track.s3Url" 
                class="shrink-0 flex items-center justify-center transition-all p-2 rounded-full disabled:opacity-30 disabled:cursor-not-allowed"
                :class="playingTrackId === track.id ? 'bg-lime-500 text-slate-950 shadow-[0_0_10px_rgba(132,204,22,0.5)]' : 'text-lime-400 bg-lime-500/10 group-hover:bg-lime-500/20'"
              >
                <svg v-if="track.status === 'PENDING'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" /></svg>
                <svg v-else-if="playingTrackId === track.id" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zM7 8a1 1 0 012 0v4a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" /></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" /></svg>
              </button>
              
              <div class="min-w-0">
                <div class="font-bold text-slate-200 tracking-wider truncate max-w-md">{{ track.title || 'Untitled Track' }}</div>
                <div class="text-[10px] text-slate-500 uppercase font-black tracking-widest mt-0.5 flex items-center gap-3">
                  {{ new Date(track.createdAt).toLocaleDateString() }} 
                  
                  <span class="flex items-center gap-1 text-[10px] font-black uppercase tracking-widest text-slate-400">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                    {{ track.streamCount || 0 }}
                  </span>

                  <span v-if="track.status === 'PENDING'" class="text-yellow-400 animate-pulse">RENDERING...</span>
                  <span v-else-if="track.status === 'COMPLETED' || track.status === 'READY'" class="text-lime-400">READY</span>
                  <span v-else-if="track.status === 'FAILED'" class="text-red-400">ERROR</span>
                  <span v-else-if="track.status" class="text-slate-400">{{ track.status }}</span>
                </div>
              </div>
            </div>

            <div class="flex items-center gap-6 text-sm font-bold text-slate-400 shrink-0">
              <span>{{ formatDuration(track.duration) }}</span>
            </div>

          </div>
        </div>
      </div>
    </div>

  </div>
</template>