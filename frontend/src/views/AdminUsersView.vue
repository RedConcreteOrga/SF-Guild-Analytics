<template>
  <div class="space-y-8">
    <!-- Header -->
    <div>
      <h2 class="text-3xl font-bold text-white tracking-tight">Benutzerverwaltung</h2>
      <p class="text-slate-400 mt-1">Accounts erstellen und verwalten.</p>
    </div>

    <!-- Create user form -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl shadow-slate-950/50">
      <h3 class="text-lg font-bold text-white mb-5">Neuen Account erstellen</h3>
      <form @submit.prevent="handleCreate" class="grid grid-cols-1 sm:grid-cols-4 gap-4 items-end">
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Benutzername</label>
          <input
            v-model="form.username"
            type="text"
            required
            placeholder="z. B. spieler1"
            class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2.5 text-white focus:border-indigo-500 focus:outline-none transition-all placeholder-slate-600"
          />
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Passwort</label>
          <input
            v-model="form.password"
            type="password"
            required
            placeholder="••••••••"
            class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2.5 text-white focus:border-indigo-500 focus:outline-none transition-all"
          />
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Rolle</label>
          <select
            v-model="form.role"
            class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2.5 text-white focus:border-indigo-500 focus:outline-none transition-all"
          >
            <option value="GUILD_LEADER">GildenLeader</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>
        <div>
          <button
            type="submit"
            :disabled="creating"
            class="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-semibold py-2.5 rounded-xl transition-all flex items-center justify-center gap-2"
          >
            <Loader2 v-if="creating" class="w-4 h-4 animate-spin" />
            <UserPlus v-else class="w-4 h-4" />
            Erstellen
          </button>
        </div>
      </form>
      <p v-if="createError" class="mt-3 text-red-400 text-sm bg-red-400/10 border border-red-400/20 rounded-lg px-3 py-2">
        {{ createError }}
      </p>
      <p v-if="createSuccess" class="mt-3 text-green-400 text-sm bg-green-400/10 border border-green-400/20 rounded-lg px-3 py-2 flex items-center gap-2">
        <CheckCircle class="w-4 h-4" /> Account erfolgreich erstellt.
      </p>
    </div>

    <!-- User list -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl shadow-slate-950/50">
      <div class="px-6 py-4 bg-slate-800/50 border-b border-slate-800 flex items-center justify-between">
        <h3 class="font-bold text-white">Alle Accounts</h3>
        <span class="text-slate-500 text-sm">{{ users.length }} Benutzer</span>
      </div>

      <div v-if="loading" class="flex justify-center py-10">
        <Loader2 class="w-6 h-6 text-indigo-500 animate-spin" />
      </div>

      <div v-else class="divide-y divide-slate-800">
        <div
          v-for="user in users"
          :key="user.id"
          class="px-6 py-4 flex items-center justify-between gap-4"
        >
          <div class="flex items-center gap-4">
            <div class="w-9 h-9 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center flex-shrink-0">
              <UserIcon class="w-4 h-4 text-slate-400" />
            </div>
            <div>
              <div class="font-semibold text-white">{{ user.username }}</div>
              <div class="text-xs text-slate-500 mt-0.5">ID: {{ user.id }}</div>
            </div>
          </div>

          <div class="flex items-center gap-3">
            <span
              class="px-2.5 py-1 rounded-full text-xs font-bold border"
              :class="roleBadge(user.role)"
            >
              {{ roleLabel(user.role) }}
            </span>
            <button
              v-if="user.id !== authStore.user?.id"
              @click="handleDelete(user)"
              class="text-slate-600 hover:text-red-400 transition-colors p-1.5 rounded-lg hover:bg-red-400/10"
              title="Account löschen"
            >
              <Trash2 class="w-4 h-4" />
            </button>
            <span v-else class="text-xs text-slate-600 italic">Du</span>
          </div>
        </div>

        <div v-if="users.length === 0" class="px-6 py-10 text-center text-slate-500">
          Keine Benutzer gefunden.
        </div>
      </div>
    </div>

    <!-- Delete confirmation modal -->
    <div v-if="userToDelete" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div class="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-sm p-6 shadow-2xl">
        <h3 class="text-lg font-bold text-white mb-2">Account löschen?</h3>
        <p class="text-slate-400 text-sm mb-6">
          Soll der Account <span class="text-white font-semibold">{{ userToDelete.username }}</span> wirklich gelöscht werden? Diese Aktion kann nicht rückgängig gemacht werden.
        </p>
        <div class="flex gap-3">
          <button @click="userToDelete = null" class="flex-1 px-4 py-2 text-slate-400 hover:text-white transition-colors rounded-xl border border-slate-700 hover:border-slate-600">
            Abbrechen
          </button>
          <button @click="confirmDelete" :disabled="deleting" class="flex-1 bg-red-600 hover:bg-red-500 disabled:opacity-50 text-white font-semibold py-2 rounded-xl transition-all flex items-center justify-center gap-2">
            <Loader2 v-if="deleting" class="w-4 h-4 animate-spin" />
            Löschen
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/axios'
import { Loader2, UserPlus, Trash2, CheckCircle, User as UserIcon } from 'lucide-vue-next'

const authStore = useAuthStore()

const users = ref([])
const loading = ref(true)
const creating = ref(false)
const deleting = ref(false)
const createError = ref('')
const createSuccess = ref(false)
const userToDelete = ref(null)

const form = reactive({ username: '', password: '', role: 'GUILD_LEADER' })

const roleLabel = (role) => {
  const map = { ADMIN: 'Admin', GUILD_LEADER: 'GildenLeader' }
  return map[role] ?? role
}

const roleBadge = (role) => {
  const map = {
    ADMIN:        'bg-red-500/10 text-red-400 border-red-500/20',
    GUILD_LEADER: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/20',
  }
  return map[role] ?? 'bg-slate-700/50 text-slate-400 border-slate-600'
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await api.get('/users')
    users.value = res.data
  } finally {
    loading.value = false
  }
}

const handleCreate = async () => {
  creating.value = true
  createError.value = ''
  createSuccess.value = false
  try {
    const res = await api.post('/users', { ...form })
    users.value.push(res.data)
    form.username = ''
    form.password = ''
    form.role = 'MEMBER'
    createSuccess.value = true
    setTimeout(() => { createSuccess.value = false }, 3000)
  } catch (err) {
    createError.value = err.response?.status === 409 || err.response?.data?.message?.includes('exists')
      ? 'Benutzername bereits vergeben.'
      : 'Fehler beim Erstellen des Accounts.'
  } finally {
    creating.value = false
  }
}

const handleDelete = (user) => {
  userToDelete.value = user
}

const confirmDelete = async () => {
  deleting.value = true
  try {
    await api.delete(`/users/${userToDelete.value.id}`)
    users.value = users.value.filter(u => u.id !== userToDelete.value.id)
    userToDelete.value = null
  } finally {
    deleting.value = false
  }
}

onMounted(fetchUsers)
</script>
