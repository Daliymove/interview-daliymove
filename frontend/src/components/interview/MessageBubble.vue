<template>
  <div class="flex items-start gap-3" :class="message?.type === 'user' ? 'justify-end' : ''">
    <template v-if="message?.type === 'interviewer'">
      <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center flex-shrink-0">
        <span class="i-carbon-user text-primary"></span>
      </div>
      <div class="flex-1">
        <div class="flex items-center gap-2 mb-1">
          <span class="text-sm font-semibold text-slate-700">面试官</span>
          <template v-if="message?.category">
            <n-tag size="small" round type="primary">{{ message.category }}</n-tag>
          </template>
        </div>
        <div class="bg-slate-100 rounded-2xl rounded-tl-none p-4 text-slate-800 leading-relaxed">
          {{ message?.content }}
        </div>
      </div>
    </template>
    <template v-else>
      <div class="flex-1 max-w-[80%]">
        <div class="bg-primary text-white rounded-2xl rounded-tr-none p-4 leading-relaxed">
          {{ message?.content }}
        </div>
      </div>
      <div class="w-8 h-8 bg-slate-200 rounded-full flex items-center justify-center flex-shrink-0">
        <span class="i-carbon-user-avatar text-slate-600"></span>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
/**
 * 消息气泡组件
 * - 面试官消息（左侧）
 * - 用户消息（右侧）
 */

interface Message {
  type: 'interviewer' | 'user'
  content: string
  category?: string
  questionIndex?: number
}

defineProps<{
  message?: Message
}>()
</script>