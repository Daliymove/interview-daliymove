<template>
  <div class="flex flex-col h-[calc(100vh-200px)] max-w-4xl mx-auto">
    <n-card class="mb-4">
      <div class="flex items-center justify-between mb-3">
        <span class="text-sm font-semibold text-slate-700">
          题目 {{ currentQuestion ? currentQuestion.questionIndex + 1 : 0 }} / {{ session?.totalQuestions || 0 }}
        </span>
        <span class="text-sm text-slate-500">{{ Math.round(progress) }}%</span>
      </div>
      <n-progress
        type="line"
        :percentage="progress"
        :show-indicator="false"
        :height="8"
        status="success"
      />
    </n-card>

    <n-card class="flex-1 overflow-hidden flex flex-col min-h-0">
      <div class="flex-1 overflow-y-auto px-6 py-4 space-y-4">
        <MessageBubble
          v-for="(msg, i) in messages"
          :key="i"
          :message="msg"
        />
      </div>

      <div class="border-t border-slate-200 p-4 bg-slate-50">
        <div class="flex gap-3">
          <n-input
            v-model:value="answer"
            type="textarea"
            placeholder="输入你的回答... (Ctrl/Cmd + Enter 提交)"
            :rows="3"
            :disabled="isSubmitting"
            @keydown="handleKeyPress"
          />
          <div class="flex flex-col gap-2">
            <n-button
              type="primary"
              :disabled="!answer.trim() || isSubmitting"
              :loading="isSubmitting"
              @click="handleSubmit"
            >
              <template #icon>
                <span class="i-carbon-send"></span>
              </template>
              提交
            </n-button>
            <n-button
              quaternary
              :disabled="isSubmitting"
              @click="handleShowCompleteConfirm"
            >
              提前交卷
            </n-button>
          </div>
        </div>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
/**
 * 面试聊天面板组件
 * - 显示对话消息
 * - 输入回答
 * - 进度显示
 */
import { computed, ref, watch } from 'vue'
import type { InterviewSession, InterviewQuestion } from '@/types/interview'
import MessageBubble from './MessageBubble.vue'

interface Message {
  type: 'interviewer' | 'user'
  content: string
  category?: string
  questionIndex?: number
}

const props = withDefaults(defineProps<{
  session?: InterviewSession
  currentQuestion?: InterviewQuestion | null
  messages?: Message[]
  isSubmitting?: boolean
  onShowCompleteConfirm?: (show: boolean) => void
}>(), {
  messages: () => [],
  isSubmitting: false
})

const emit = defineEmits<{
  answerChange: [answer: string]
  submit: []
}>()

const answer = ref('')

const progress = computed(() => {
  if (!props.session || !props.currentQuestion) return 0
  return ((props.currentQuestion.questionIndex + 1) / props.session.totalQuestions) * 100
})

const handleKeyPress = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) {
    handleSubmit()
  }
}

const handleSubmit = () => {
  emit('submit')
}

const handleShowCompleteConfirm = () => {
  props.onShowCompleteConfirm?.(true)
}

watch(answer, (val) => {
  emit('answerChange', val)
})
</script>