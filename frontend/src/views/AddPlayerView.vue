<template>
  <div class="max-w-2xl mx-auto py-12 px-4">
    <div class="bg-slate-900 border border-slate-800 rounded-2xl p-8 shadow-2xl shadow-slate-950/50">
      <div class="flex items-center gap-4 mb-10">
        <button 
          @click="$router.push({ name: 'guild-players', params: { id: $route.params.guildId } })"
          class="bg-slate-950 border border-slate-800 p-2 rounded-xl text-slate-400 hover:text-white hover:border-slate-700 transition-all shadow-lg"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-3xl font-bold text-white tracking-tight">Add Player</h1>
          <p class="text-slate-400 mt-1">Add a new member to your guild</p>
        </div>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Player Name</label>
          <input 
            v-model="form.name"
            type="text" 
            required
            placeholder="e.g. Sir Analytics"
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all"
          />
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Class</label>
            <select 
              v-model="form.playerClass"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all"
            >
              <option value="WARRIOR">Warrior</option>
              <option value="MAGE">Mage</option>
              <option value="SCOUT">Scout</option>
              <option value="ASSASSIN">Assassin</option>
              <option value="BATTLE_MAGE">Battle Mage</option>
              <option value="BERSERKER">Berserker</option>
              <option value="DEMON_HUNTER">Demon Hunter</option>
              <option value="DRUID">Druid</option>
              <option value="BARD">Bard</option>
              <option value="NECROMANCER">Necromancer</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-bold text-slate-300 uppercase tracking-wider mb-2">Join Date</label>
            <input 
              v-model="form.joinDate"
              type="date" 
              required
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
          <span v-else>Add Player</span>
          <Plus v-if="!loading" class="w-5 h-5" />
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/api/axios'
import { ArrowLeft, Plus, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const error = ref('')

const form = reactive({
  name: '',
  playerClass: 'WARRIOR',
  joinDate: new Date().toISOString().split('T')[0]
})

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const payload = {
      ...form,
      active: true,
      guildId: route.params.guildId
    }
    
    await api.post('/players', payload)
    router.push({ name: 'guild-players', params: { id: route.params.guildId } })
  } catch (err) {
    console.error('Add player failed:', err)
    error.value = err.response?.data?.message || 'Could not add player. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
