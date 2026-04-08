<template>
  <div class="relative my-3">
    <div class="flex items-center justify-between px-4 py-2 bg-slate-700 rounded-t-xl border-b border-slate-600">
      <span class="text-xs text-slate-400 font-mono">
        {{ language || 'code' }}
      </span>
      <n-button
        quaternary
        size="tiny"
        class="text-slate-400 hover:text-white"
        @click="handleCopy"
      >
        <template #icon>
          <span :class="copied ? 'i-carbon-checkmark text-green-400' : 'i-carbon-copy'"></span>
        </template>
        <span :class="copied ? 'text-green-400' : ''">{{ copied ? '已复制' : '复制' }}</span>
      </n-button>
    </div>

    <div class="bg-[#282c34] rounded-b-xl text-sm leading-6 overflow-auto">
      <pre class="p-4"><code ref="codeRef" :class="`language-${language || 'text'}`">{{ code }}</code></pre>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 代码块组件
 * - 使用 highlight.js 进行语法高亮
 * - 支持复制功能
 */
import { ref, onMounted, watch } from 'vue'
import hljs from 'highlight.js'

interface Props {
  language?: string
  code: string
}

const props = defineProps<Props>()

const copied = ref(false)
const codeRef = ref<HTMLElement | null>(null)

const code = computed(() => props.code?.trim() || '')

const highlightCode = () => {
  if (codeRef.value) {
    hljs.highlightElement(codeRef.value)
  }
}

const handleCopy = async () => {
  try {
    await navigator.clipboard.writeText(props.code)
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  } catch (err) {
    console.error('复制失败:', err)
  }
}

onMounted(() => {
  highlightCode()
})

watch(() => props.code, () => {
  highlightCode()
})

import { computed } from 'vue'
</script>

<style scoped>
pre {
  margin: 0;
  background: transparent;
}

code {
  font-family: 'Fira Code', 'Consolas', monospace;
  font-size: 0.875rem;
  line-height: 1.5;
  background: transparent !important;
}
</style>