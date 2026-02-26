<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const username = route.params.username

//mock data
const generatedTracks = ref([
  { id: 1, title: 'Neon Nightrider', date: 'Feb 26, 2026', duration: '3:45', plays: 1204 },
  { id: 2, title: 'Cybernetic Pulse', date: 'Feb 24, 2026', duration: '2:30', plays: 850 },
  { id: 3, title: 'Acid Rain Drop', date: 'Feb 20, 2026', duration: '4:12', plays: 3042 },
])
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-8 shadow-2xl mb-8">
      <div class="flex items-center gap-6 border-b border-slate-800/80 pb-8">
        <div class="h-24 w-24 rounded-full bg-gradient-to-br from-yellow-400 to-lime-500 p-1 shadow-[0_0_30px_rgba(132,204,22,0.3)]">
          <div class="h-full w-full bg-slate-900 rounded-full flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>
        
        <div>
          <h1 class="text-4xl font-black uppercase tracking-widest text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">
            {{ username }}
          </h1>
          <p class="text-slate-500 font-bold uppercase tracking-widest mt-1">Lead Producer</p>
        </div>
      </div>

      <div class="flex gap-12 pt-6">
        <div>
          <div class="text-3xl font-black text-slate-200">{{ generatedTracks.length }}</div>
          <div class="text-xs font-bold text-slate-500 uppercase tracking-widest">Tracks Generated</div>
        </div>
        <div>
          <div class="text-3xl font-black text-slate-200">5,096</div>
          <div class="text-xs font-bold text-slate-500 uppercase tracking-widest">Total Streams</div>
        </div>
      </div>
    </div>

    <div>
      <h3 class="text-sm font-black text-slate-400 uppercase tracking-widest mb-4 ml-2">Generated Masterpieces</h3>
      
      <div class="space-y-3">
        <div v-for="track in generatedTracks" :key="track.id" class="flex items-center justify-between bg-slate-950/50 hover:bg-slate-800/80 border border-slate-800 hover:border-lime-500/50 transition-all p-4 rounded-xl group cursor-pointer">
          
          <div class="flex items-center gap-4">
            <button class="text-lime-400 opacity-70 group-hover:opacity-100 transition-opacity bg-lime-500/10 p-2 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" />
              </svg>
            </button>
            
            <div>
              <div class="font-bold text-slate-200 tracking-wider">{{ track.title }}</div>
              <div class="text-xs text-slate-500 uppercase font-bold mt-0.5">{{ track.date }}</div>
            </div>
          </div>

          <div class="flex items-center gap-6 text-sm font-bold text-slate-400">
            <span class="flex items-center gap-1">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              {{ track.plays }}
            </span>
            <span>{{ track.duration }}</span>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>