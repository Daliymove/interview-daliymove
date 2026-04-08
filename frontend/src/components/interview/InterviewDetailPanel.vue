<template>
  <div class="space-y-6">
    <n-card class="bg-gradient-to-br from-violet-600 via-purple-600 to-indigo-700">
      <div class="flex flex-col items-center text-center text-white">
        <div class="relative w-32 h-32 mb-6">
          <svg class="w-32 h-32 transform -rotate-90" viewBox="0 0 120 120">
            <circle
              cx="60"
              cy="60"
              r="54"
              stroke="rgba(255,255,255,0.2)"
              stroke-width="8"
              fill="none"
            />
            <circle
              cx="60"
              cy="60"
              r="54"
              stroke="white"
              stroke-width="8"
              fill="none"
              stroke-linecap="round"
              :stroke-dasharray="circumference"
              :stroke-dashoffset="animatedOffset"
              class="transition-all duration-1500"
            />
          </svg>
          <div class="absolute inset-0 flex flex-col items-center justify-center">
            <template v-if="isProcessing">
              <n-spin size="large" />
            </template>
            <template v-else>
              <span class="text-4xl font-bold">{{ interview?.overallScore ?? '-' }}</span>
              <span class="text-sm text-white/70">总分</span>
            </template>
          </div>
        </div>

        <h3 class="text-2xl font-bold mb-3">
          {{ isProcessing ? '正在评估中...' : isFailed ? '评估失败' : '面试评估' }}
        </h3>
        <p class="text-white/90 max-w-2xl leading-relaxed mb-4">
          {{ isFailed ? 'AI评估服务暂时不可用，请点击重新评估按钮重试' : interview?.overallFeedback || '表现良好，展示了扎实的技术基础。' }}
        </p>

        <template v-if="showReevaluateButton">
          <n-button
            quaternary
            :loading="reevaluating || isProcessing"
            :disabled="reevaluating || isProcessing"
            @click="handleReevaluate"
          >
            <template #icon>
              <span class="i-carbon-renew"></span>
            </template>
            {{ reevaluating || isProcessing ? '评估中...' : '重新评估' }}
          </n-button>
        </template>
      </div>
    </n-card>

    <template v-if="interview?.strengths && interview.strengths.length > 0">
      <n-card>
        <h4 class="font-semibold text-emerald-600 mb-4 flex items-center gap-2">
          <span class="i-carbon-checkmark text-lg"></span>
          表现优势
        </h4>
        <ul class="space-y-3">
          <li
            v-for="(s, i) in interview.strengths"
            :key="i"
            class="text-slate-700 flex items-start gap-3"
          >
            <span class="w-2 h-2 bg-primary rounded-full mt-2 flex-shrink-0"></span>
            <span>{{ s }}</span>
          </li>
        </ul>
      </n-card>
    </template>

    <template v-if="interview?.improvements && interview.improvements.length > 0">
      <n-card>
        <h4 class="font-semibold text-amber-600 mb-4 flex items-center gap-2">
          <span class="i-carbon-warning text-lg"></span>
          改进建议
        </h4>
        <ul class="space-y-3">
          <li
            v-for="(s, i) in interview.improvements"
            :key="i"
            class="text-slate-700 flex items-start gap-3"
          >
            <span class="w-2 h-2 bg-amber-500 rounded-full mt-2 flex-shrink-0"></span>
            <span>{{ s }}</span>
          </li>
        </ul>
      </n-card>
    </template>

    <n-card>
      <h4 class="font-semibold text-slate-800 mb-4 flex items-center gap-2">
        <span class="i-carbon-chat text-primary text-lg"></span>
        问答记录详情
      </h4>

      <n-space vertical size="large">
        <QuestionCard
          v-for="(answer, idx) in interview?.answers || []"
          :key="idx"
          :answer="answer"
          :index="idx"
          :expanded="expandedQuestions.has(idx)"
          @toggle="toggleQuestion(idx)"
        />
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
/**
 * 面试详情面板组件
 * - 评分圆环
 * - 表现优势和改进建议
 * - 问答记录详情
 */
import { ref, computed, onMounted } from 'vue'
import type { InterviewDetail } from '@/types/resume'
import QuestionCard from './QuestionCard.vue'

const props = defineProps<{
  interview?: InterviewDetail
  reevaluating?: boolean
}>()

const emit = defineEmits<{
  reevaluate: []
}>()

const expandedQuestions = ref<Set<number>>(new Set())
const animatedOffset = ref(2 * Math.PI * 54)

const evaluateStatus = computed(() => props.interview?.evaluateStatus)
const isProcessing = computed(() => 
  evaluateStatus.value === 'PENDING' || evaluateStatus.value === 'PROCESSING'
)
const isFailed = computed(() => evaluateStatus.value === 'FAILED')
const showReevaluateButton = computed(() =>
  isProcessing.value || isFailed.value
)

const circumference = 2 * Math.PI * 54

const scorePercent = computed(() => {
  return props.interview?.overallScore !== null && props.interview?.overallScore !== undefined
    ? (props.interview.overallScore / 100) * 100 
    : 0
})

const toggleQuestion = (index: number) => {
  if (expandedQuestions.value.has(index)) {
    expandedQuestions.value.delete(index)
  } else {
    expandedQuestions.value.add(index)
  }
}

const handleReevaluate = () => {
  emit('reevaluate')
}

onMounted(() => {
  if (props.interview?.answers) {
    props.interview.answers.forEach((_, idx) => expandedQuestions.value.add(idx))
  }
  
  setTimeout(() => {
    const offset = circumference - (scorePercent.value / 100) * circumference
    animatedOffset.value = offset
  }, 100)
})
</script>