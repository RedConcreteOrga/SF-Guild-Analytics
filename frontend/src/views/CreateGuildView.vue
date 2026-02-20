<template>
  <div class="max-w-xl mx-auto space-y-6">
    <div class="flex items-center gap-4">
      <button
        @click="$router.push({ name: 'guilds' })"
        class="bg-slate-900 border border-slate-800 p-2 rounded-xl text-slate-400 hover:text-white hover:border-slate-700 transition-all"
      >
        <ArrowLeft class="w-5 h-5" />
      </button>
      <div>
        <h2 class="text-2xl font-bold text-white tracking-tight">Neue Gilde erstellen</h2>
        <p class="text-slate-400 text-sm mt-0.5">Die Gilde wird dir als GildenLeader zugewiesen.</p>
      </div>
    </div>

    <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl shadow-slate-950/50">
      <form @submit.prevent="handleSubmit" class="space-y-5">
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Gildenname *</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="z. B. Warriors of Light"
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all placeholder-slate-600"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Server *</label>
          <input
            v-model="form.server"
            type="text"
            required
            placeholder="z. B. S1 DE"
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-indigo-500 focus:outline-none transition-all placeholder-slate-600"
          />
        </div>

        <!-- Image upload -->
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Gildenbild (optional)</label>

          <!-- Drop / paste zone -->
          <div
            class="relative border-2 border-dashed rounded-xl transition-all cursor-pointer"
            :class="dragging ? 'border-indigo-500 bg-indigo-500/5' : 'border-slate-700 hover:border-slate-600'"
            @click="fileInput.click()"
            @dragover.prevent="dragging = true"
            @dragleave.prevent="dragging = false"
            @drop.prevent="onDrop"
          >
            <!-- Preview -->
            <div v-if="previewUrl" class="p-3">
              <img :src="previewUrl" class="max-h-40 mx-auto rounded-lg object-contain" />
            </div>

            <!-- Placeholder -->
            <div v-else class="flex flex-col items-center justify-center py-8 px-4 text-center pointer-events-none">
              <ImagePlus class="w-8 h-8 text-slate-600 mb-2" />
              <p class="text-slate-400 text-sm font-medium">Klicken oder Bild hierher ziehen</p>
              <p class="text-slate-600 text-xs mt-1">Auch Strg+V zum Einfügen aus der Zwischenablage</p>
            </div>

            <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="onFileChange" />
          </div>

          <!-- Clear button -->
          <button
            v-if="previewUrl"
            type="button"
            @click="clearImage"
            class="mt-2 text-xs text-slate-500 hover:text-red-400 transition-colors flex items-center gap-1"
          >
            <X class="w-3 h-3" /> Bild entfernen
          </button>
        </div>

        <div v-if="error" class="bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3 text-red-400 text-sm">
          {{ error }}
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-3 rounded-xl shadow-lg shadow-indigo-600/20 transition-all flex items-center justify-center gap-2"
        >
          <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
          <Shield v-else class="w-5 h-5" />
          {{ loading ? 'Wird erstellt...' : 'Gilde erstellen' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/axios'
import { Shield, ArrowLeft, Loader2, ImagePlus, X } from 'lucide-vue-next'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const dragging = ref(false)
const previewUrl = ref('')
const selectedFile = ref(null)
const fileInput = ref(null)

const form = reactive({ name: '', server: '', faction: 'GOOD' })

const applyFile = (file) => {
  if (!file || !file.type.startsWith('image/')) return
  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
}

const onFileChange = (e) => applyFile(e.target.files[0])

const onDrop = (e) => {
  dragging.value = false
  applyFile(e.dataTransfer.files[0])
}

const onPaste = (e) => {
  const item = [...e.clipboardData.items].find(i => i.type.startsWith('image/'))
  if (item) applyFile(item.getAsFile())
}

const clearImage = () => {
  selectedFile.value = null
  previewUrl.value = ''
  if (fileInput.value) fileInput.value.value = ''
}

onMounted(() => window.addEventListener('paste', onPaste))
onUnmounted(() => window.removeEventListener('paste', onPaste))

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  try {
    let imageUrl = ''
    if (selectedFile.value) {
      const fd = new FormData()
      fd.append('file', selectedFile.value)
      const res = await api.post('/upload', fd)
      const backendBase = import.meta.env.VITE_API_BASE_URL.replace('/api', '')
      imageUrl = backendBase + res.data.path
    }
    await api.post('/guilds', { ...form, imageUrl })
    router.push({ name: 'guilds' })
  } catch (err) {
    error.value = err.response?.data?.message || 'Gilde konnte nicht erstellt werden.'
  } finally {
    loading.value = false
  }
}
</script>
