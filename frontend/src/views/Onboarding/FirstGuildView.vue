<template>
  <div class="max-w-2xl mx-auto py-12 px-4">
    <div class="bg-slate-900 border border-slate-800 rounded-2xl p-8 shadow-2xl shadow-slate-950/50">
      <div class="text-center mb-10">
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-indigo-600/20 mb-6">
          <Shield class="w-8 h-8 text-indigo-400" />
        </div>
        <h1 class="text-3xl font-bold text-white tracking-tight">Create Your First Guild</h1>
        <p class="text-slate-400 mt-3 text-lg">
          To start tracking analytics, you first need to define your guild.
        </p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Guild Name</label>
          <input 
            v-model="form.name"
            type="text" 
            required
            placeholder="e.g. Warriors of Light"
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all"
          />
        </div>

        <div>
          <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Image URL (Optional)</label>
          <input 
            v-model="form.imageUrl"
            type="url" 
            placeholder="https://example.com/image.png"
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all"
          />
        </div>

        <div class="grid grid-cols-1 gap-6">
          <div>
            <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Server</label>
            <input 
              v-model="form.server"
              type="text" 
              required
              placeholder="e.g. S1 DE"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all"
            />
          </div>
        </div>

        <div v-if="error" class="bg-red-500/10 border border-red-500/20 rounded-xl p-4 text-red-400 text-sm">
          {{ error }}
        </div>

        <button 
          type="submit" 
          :disabled="loading"
          class="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-4 px-6 rounded-xl shadow-lg shadow-indigo-600/20 transition-all flex items-center justify-center gap-2 mt-4"
        >
          <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
          <span v-else>Create Guild & Continue</span>
          <ArrowRight v-if="!loading" class="w-5 h-5" />
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/axios'
import { Shield, ArrowRight, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const error = ref('')

const form = reactive({
  name: '',
  server: '',
  faction: 'GOOD',
  imageUrl: ''
})

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await api.post('/guilds', form)
    // Update store state after success
    await authStore.checkOnboardingStatus()
    router.push({ name: 'first-player' })
  } catch (err) {
    error.value = err.response?.data?.message || 'Could not create guild. Please try again.'
    console.error('Create guild failed', err)
  } finally {
    loading.value = false
  }
}
</script>
