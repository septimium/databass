<script setup>
import { ref } from 'vue'

const faqs = [
  {
    category: "Economy & Credits",
    questions: [
      {
        q: "How do Credits work and what do things cost?",
        a: "Credits are the lifeblood of Databass. The base cost of generating a track equals its duration (e.g., 30 seconds = 30 Credits). Activating Gemini Enhance adds a flat 15 CR fee, and using Advanced Parameters adds a 20 CR fee."
      },
      {
        q: "How do I get more credits?",
        a: "Click the 'Claim Daily' button at the top of your dashboard. You get a base of 50 credits every 24 hours. If you log in consecutively, you build a streak that adds bonus credits on top of your base claim!"
      }
    ]
  },
  {
    category: "AI Generation Parameters",
    questions: [
      {
        q: "What is 'Gemini Enhance'?",
        a: "Sometimes a simple prompt like 'cool synthwave' isn't enough for the audio engine. Gemini Enhance uses a text-based AI to automatically rewrite and expand your prompt into a highly detailed, professional audio-generation script before sending it to the music engine."
      },
      {
        q: "What does 'Temperature' do?",
        a: "Temperature controls the 'creativity' or randomness of the AI. A lower value (e.g., 0.3) makes the AI play it safe, resulting in more generic but structurally sound music. A higher value (e.g., 1.5) makes the AI experiment wildly, which can lead to genius tracks or complete chaos."
      },
      {
        q: "What does 'Guidance Scale' do?",
        a: "Guidance Scale dictates how strictly the AI must follow your text prompt. A high scale forces the AI to stick exactly to your words, while a lower scale gives the AI room to interpret the vibe on its own."
      },
      {
        q: "What does 'Top K' mean?",
        a: "Top K limits the AI's 'vocabulary' of sound tokens. Lowering this number forces the AI to only pick from the absolute most likely next sounds, resulting in a cleaner but potentially more repetitive track."
      }
    ]
  },
  {
    category: "Platform & Privacy",
    questions: [
      {
        q: "Are my generated tracks public?",
        a: "Yes, by default, successfully generated tracks are pushed to your public Producer Profile and the Global Discover feed. You can hide a track at any time by clicking the 'Edit' button in your Library and toggling off the Public visibility."
      },
      {
        q: "How do I customize my Producer Profile?",
        a: "Head over to the Settings tab in the sidebar. There, you can write a custom Producer Bio and paste an image URL to set your avatar. These changes will instantly update on your public profile and in the Discover feed."
      },
      {
        q: "Why is my track stuck on 'RENDERING...'?",
        a: "High-fidelity audio generation takes time. When you initialize a track, it is placed in a secure queue. Our background workers process these one by one. Usually, a 30-second track takes about 1-2 minutes to render. You can safely leave the page; it will update in your Library when finished!"
      }
    ]
  }
]
</script>

<template>
  <div class="p-8 max-w-4xl mx-auto relative z-10 animate-fade-in-up pb-20">
    <div class="mb-12 text-center">
      <h1 class="text-4xl font-black uppercase tracking-widest text-slate-200 drop-shadow-[0_0_15px_rgba(255,255,255,0.1)]">
        Data<span class="text-lime-400 drop-shadow-[0_0_15px_rgba(132,204,22,0.4)]">bass</span> Manual
      </h1>
      <p class="text-slate-500 font-bold uppercase tracking-widest mt-2">Knowledge Base & System Documentation</p>
    </div>

    <div class="space-y-12">
      <div v-for="(section, index) in faqs" :key="index" class="space-y-6">
        
        <h2 class="text-lg font-black text-transparent bg-clip-text bg-gradient-to-r from-lime-400 to-yellow-400 uppercase tracking-widest border-b border-slate-800 pb-2">
          {{ section.category }}
        </h2>

        <div class="grid grid-cols-1 gap-4">
          <div 
            v-for="(faq, fIndex) in section.questions" 
            :key="fIndex" 
            class="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-xl p-6 shadow-lg hover:border-slate-700 transition-colors"
          >
            <h3 class="text-sm font-black text-slate-200 tracking-wider mb-3 flex items-start gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-lime-400 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              {{ faq.q }}
            </h3>
            <p class="text-slate-400 text-sm leading-relaxed pl-8">
              {{ faq.a }}
            </p>
          </div>
        </div>

      </div>
    </div>

  
  </div>
</template>