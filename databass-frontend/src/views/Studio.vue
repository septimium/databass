<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()
const emit = defineEmits(['credit-spent'])

const prompt = ref('')
const duration = ref(30) 
const enhancePrompt = ref(false)
const useAdvancedParams = ref(false)

const temperature = ref(0.85)
const topK = ref(250)
const guidanceScale = ref(3.2)

const isGenerating = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const totalCost = computed(() => {
  let cost = duration.value
  if (useAdvancedParams.value) cost += 20
  if (enhancePrompt.value) cost += 15
  return cost
})

const handleGenerate = async () => {
  if (!prompt.value.trim()) {
    errorMessage.value = "PROMPT CANNOT BE EMPTY."
    return
  }

  isGenerating.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const payload = {
      prompt: prompt.value,
      duration: duration.value,
      enhancePrompt: enhancePrompt.value,
      useAdvancedParams: useAdvancedParams.value,
      temperature: temperature.value,
      topK: topK.value,
      guidanceScale: guidanceScale.value
    }

    const response = await api.post('/music/generate', payload)
    
    emit('credit-spent', totalCost.value)
    
    successMessage.value = "TRACK QUEUED SUCCESSFULLY! REDIRECTING TO LIBRARY..."
    
    setTimeout(() => {
      router.push('/library')
    }, 2000)

  } catch (error) {
    if (error.response && error.response.status === 402) {
      errorMessage.value = "INSUFFICIENT CREDITS. CLAIM YOUR DAILY REWARD!"
    } else {
      errorMessage.value = "GENERATION FAILED. SYSTEM ERROR."
    }
  } finally {
    isGenerating.value = false
  }
}
</script>

<template>
  <div class="p-8 max-w-5xl mx-auto relative z-10 animate-fade-in-up">
    
    <div class="mb-10">
      <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200">
        The <span class="text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400">Studio</span>
      </h1>
      <p class="text-slate-500 font-bold uppercase tracking-widest mt-2">Initialize Audio Generation Sequence</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      
      <div class="lg:col-span-2 space-y-6">
        
        <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-6 shadow-2xl relative group focus-within:border-lime-500/50 transition-colors">
          <label class="block text-xs font-black text-lime-400 uppercase tracking-widest mb-3">Master Prompt</label>
          <textarea 
            v-model="prompt"
            rows="4" 
            placeholder="Drum and bass song with catchy beats (Toggle Gemini Enhance for a clearer prompt!)"
            class="w-full bg-slate-950/50 border border-slate-800 rounded-xl p-4 text-slate-200 placeholder-slate-500 focus:outline-none focus:border-lime-500/50 focus:ring-1 focus:ring-lime-500/50 transition-all resize-none font-medium"
          ></textarea>
        </div>

        <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl p-6 shadow-2xl flex flex-col sm:flex-row gap-6 justify-between items-center">
          
          <div class="w-full sm:w-1/2">
            <div class="flex justify-between items-end mb-3">
              <label class="block text-xs font-black text-slate-400 uppercase tracking-widest">Duration</label>
              <span class="text-sm font-black text-lime-400">{{ duration }}s</span>
            </div>
            <input 
              type="range" 
              v-model.number="duration" 
              min="5" 
              max="30" 
              step="1" 
              class="w-full accent-lime-400 cursor-pointer"
            >
          </div>

          <div class="w-full sm:w-auto flex items-center justify-between sm:justify-start gap-4 bg-slate-950/50 px-5 py-3 rounded-lg border border-slate-800">
            <div>
              <div class="text-sm font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-purple-400 uppercase tracking-wider">Gemini Enhance</div>
              <div class="text-[10px] text-slate-500 font-bold uppercase tracking-widest">+15 Credits</div>
            </div>
            <button 
              @click="enhancePrompt = !enhancePrompt"
              :class="enhancePrompt ? 'bg-purple-500 border-purple-400 shadow-[0_0_15px_rgba(168,85,247,0.4)]' : 'bg-slate-800 border-slate-700'"
              class="w-12 h-6 rounded-full relative transition-all duration-300 border focus:outline-none shrink-0"
            >
              <div :class="enhancePrompt ? 'translate-x-6 bg-white' : 'translate-x-1 bg-slate-400'" class="w-4 h-4 rounded-full transition-all duration-300"></div>
            </button>
          </div>
        </div>

        <div class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-2xl overflow-hidden shadow-2xl transition-all duration-300">
          <div class="p-6 flex items-center justify-between border-b border-slate-800/50">
            <div>
              <h3 class="text-sm font-black text-slate-300 uppercase tracking-widest flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                Advanced Parameters
              </h3>
              <div class="text-[10px] text-slate-500 font-bold uppercase tracking-widest mt-1">+20 Credits</div>
            </div>
            
            <button 
              @click="useAdvancedParams = !useAdvancedParams"
              :class="useAdvancedParams ? 'bg-yellow-500 border-yellow-400 shadow-[0_0_15px_rgba(234,179,8,0.4)]' : 'bg-slate-800 border-slate-700'"
              class="w-12 h-6 rounded-full relative transition-all duration-300 border focus:outline-none"
            >
              <div :class="useAdvancedParams ? 'translate-x-6 bg-white' : 'translate-x-1 bg-slate-400'" class="w-4 h-4 rounded-full transition-all duration-300"></div>
            </button>
          </div>

          <div v-if="useAdvancedParams" class="p-6 space-y-6 bg-slate-950/30">
            <div>
              <div class="flex justify-between mb-2">
                <label class="text-xs font-black text-slate-400 uppercase tracking-widest">Temperature</label>
                <span class="text-xs font-bold text-yellow-400">{{ temperature }}</span>
              </div>
              <input type="range" v-model.number="temperature" min="0.1" max="2.0" step="0.05" class="w-full accent-yellow-400 cursor-pointer">
              <p class="text-[10px] text-slate-600 font-bold uppercase mt-1">Controls creativity vs consistency.</p>
            </div>
            <div>
              <div class="flex justify-between mb-2">
                <label class="text-xs font-black text-slate-400 uppercase tracking-widest">Top K</label>
                <span class="text-xs font-bold text-yellow-400">{{ topK }}</span>
              </div>
              <input type="range" v-model.number="topK" min="50" max="1000" step="10" class="w-full accent-yellow-400 cursor-pointer">
              <p class="text-[10px] text-slate-600 font-bold uppercase mt-1">Limits vocabulary choices for the AI.</p>
            </div>
            <div>
              <div class="flex justify-between mb-2">
                <label class="text-xs font-black text-slate-400 uppercase tracking-widest">Guidance Scale</label>
                <span class="text-xs font-bold text-yellow-400">{{ guidanceScale }}</span>
              </div>
              <input type="range" v-model.number="guidanceScale" min="1.0" max="10.0" step="0.1" class="w-full accent-yellow-400 cursor-pointer">
              <p class="text-[10px] text-slate-600 font-bold uppercase mt-1">How strictly to follow your prompt.</p>
            </div>
          </div>
        </div>

      </div>

      <div class="lg:col-span-1">
        <div class="bg-slate-900/80 backdrop-blur-xl border border-slate-800 rounded-2xl p-6 shadow-2xl sticky top-8">
          <h2 class="text-sm font-black text-slate-300 uppercase tracking-widest border-b border-slate-800 pb-4 mb-4">Generation Ticket</h2>
          
          <div class="space-y-3 mb-8">
            <div class="flex justify-between text-sm font-bold">
              <span class="text-slate-500 uppercase tracking-wider">Base ({{ duration }}s)</span>
              <span class="text-slate-300">{{ duration }} CR</span>
            </div>
            <div v-if="enhancePrompt" class="flex justify-between text-sm font-bold">
              <span class="text-purple-400 uppercase tracking-wider">Gemini Enhance</span>
              <span class="text-purple-400">+15 CR</span>
            </div>
            <div v-if="useAdvancedParams" class="flex justify-between text-sm font-bold">
              <span class="text-yellow-400 uppercase tracking-wider">Advanced Core</span>
              <span class="text-yellow-400">+20 CR</span>
            </div>
            
            <div class="pt-4 mt-4 border-t border-slate-800 flex justify-between items-center">
              <span class="text-xs font-black text-slate-400 uppercase tracking-widest">Total Cost</span>
              <span class="text-2xl font-black text-lime-400 drop-shadow-[0_0_8px_rgba(132,204,22,0.5)]">{{ totalCost }} <span class="text-sm">CR</span></span>
            </div>
          </div>

          <div v-if="errorMessage" class="mb-4 text-xs font-bold text-red-400 bg-red-500/10 border border-red-500/30 p-3 rounded-lg text-center tracking-widest uppercase">
            {{ errorMessage }}
          </div>
          <div v-if="successMessage" class="mb-4 text-xs font-bold text-lime-400 bg-lime-500/10 border border-lime-500/30 p-3 rounded-lg text-center tracking-widest uppercase">
            {{ successMessage }}
          </div>

          <button 
            @click="handleGenerate"
            :disabled="isGenerating"
            class="w-full relative group overflow-hidden rounded-xl disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <div class="absolute inset-0 bg-gradient-to-r from-lime-500 to-yellow-500 opacity-80 group-hover:opacity-100 transition-opacity"></div>
            <div class="relative px-6 py-4 flex items-center justify-center gap-3 bg-slate-900/20 group-hover:bg-transparent transition-colors">
              <svg v-if="isGenerating" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
              </svg>
              <span class="font-black text-white tracking-[0.2em] uppercase">{{ isGenerating ? 'Transmitting...' : 'Initialize' }}</span>
            </div>
          </button>

        </div>
      </div>

    </div>
  </div>
</template>