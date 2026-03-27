<template>
  <div :class="['flex gap-3', message?.role === 'user' ? 'flex-row-reverse' : '']">
    <div
      :class="[
        'w-8 h-8 rounded-lg flex-shrink-0 flex-center',
        message?.role === 'user' ? 'bg-primary' : 'bg-gray-100'
      ]"
    >
      <span
        :class="[
          'text-lg',
          message?.role === 'user' ? 'i-carbon-user text-white' : 'i-carbon-chat-bot text-gray-500'
        ]"
      ></span>
    </div>

    <div
      :class="[
        'max-w-[80%] rounded-xl px-4 py-3',
        message?.role === 'user'
          ? 'bg-primary text-white'
          : 'bg-gray-50 text-gray-800'
      ]"
    >
      <div v-if="message?.role === 'assistant'" class="prose prose-sm max-w-none" v-html="renderedContent"></div>
      <div v-else class="whitespace-pre-wrap">{{ message?.content || '' }}</div>
      
      <div v-if="isStreaming && !message?.content" class="flex gap-1 items-center">
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
      </div>
    </div>

    <div v-if="message?.role === 'assistant' && !isStreaming" class="flex items-end gap-1 opacity-0 group-hover:opacity-100">
      <n-button quaternary size="tiny" @click="copyContent">
        <template #icon>
          <span class="i-carbon-copy text-sm"></span>
        </template>
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Message } from '@/types'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const props = defineProps<{
  message: Message
  isStreaming?: boolean
}>()

const escapeHtml = (s: string): string => {
  if (!s) return ''
  return s.replace(/[&<>"']/g, (c) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c] || c))
}

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
  highlight: function(str: string, lang: string): string {
    if (!str) return '<pre class="hljs"><code></code></pre>'
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch {
        // ignore
      }
    }
    return `<pre class="hljs"><code>${escapeHtml(str)}</code></pre>`
  }
})

const renderedContent = computed(() => {
  const content = props.message?.content
  if (!content) return ''
  try {
    return md.render(content)
  } catch (e) {
    console.error('Markdown render error:', e)
    return escapeHtml(content)
  }
})

const copyContent = async () => {
  try {
    await navigator.clipboard.writeText(props.message?.content || '')
    window.$message.success('已复制到剪贴板')
  } catch {
    window.$message.error('复制失败')
  }
}
</script>

<style scoped>
.prose :deep(pre) {
  margin: 0.5rem 0;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
}

.prose :deep(code) {
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace;
  font-size: 0.875rem;
}

.prose :deep(p) {
  margin: 0.5rem 0;
}

.prose :deep(ul),
.prose :deep(ol) {
  margin: 0.5rem 0;
  padding-left: 1.5rem;
}

.prose :deep(blockquote) {
  margin: 0.5rem 0;
  padding-left: 1rem;
  border-left: 3px solid #e5e7eb;
  color: #6b7280;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

.animate-bounce {
  animation: bounce 0.6s infinite;
}
</style>