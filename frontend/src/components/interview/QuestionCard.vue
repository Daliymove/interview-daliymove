<template>
  <div class="rounded-2xl shadow-sm overflow-hidden">
    <div
      class="px-5 py-4 flex items-center justify-between cursor-pointer hover:bg-slate-50 transition-colors"
      @click="$emit('toggle')"
    >
      <n-space align="center">
        <span class="w-8 h-8 bg-slate-100 text-slate-600 rounded-lg flex items-center justify-center text-sm font-semibold">
          {{ answer.questionIndex + 1 }}
        </span>
        <n-tag size="small" round type="primary">{{ answer.category || '综合' }}</n-tag>
        <span :class="scoreColorClass" class="font-semibold">得分: {{ answer.score }}</span>
      </n-space>
      <span
        class="i-carbon-chevron-down text-slate-400 transition-transform duration-200"
        :class="{ 'rotate-180': expanded }"
      ></span>
    </div>

    <div class="px-5 pb-2">
      <p class="text-slate-800 font-medium leading-relaxed">{{ answer.question }}</p>
    </div>

    <n-collapse-transition :show="expanded">
      <div class="px-5 pb-5 space-y-4">
        <n-card embedded class="bg-slate-50">
          <p class="text-sm text-slate-500 mb-2 flex items-center gap-1">
            <span class="i-carbon-chat text-slate-400"></span>
            你的回答
          </p>
          <p
            class="leading-relaxed"
            :class="!answer.userAnswer || answer.userAnswer === '不知道' ? 'text-red-500 font-medium' : 'text-slate-700'"
          >
            "{{ answer.userAnswer || '(未回答)' }}"
          </p>
        </n-card>

        <template v-if="answer.feedback">
          <div>
            <p class="text-sm text-slate-600 mb-2 flex items-center gap-2 font-medium">
              <span class="i-carbon-analytics text-primary"></span>
              AI 深度评价
            </p>
            <p class="text-slate-700 leading-relaxed pl-6">{{ answer.feedback }}</p>
          </div>
        </template>

        <template v-if="answer.referenceAnswer">
          <n-card embedded class="bg-slate-50 border border-slate-100">
            <p class="text-sm text-slate-600 mb-3 flex items-center gap-2 font-medium">
              <span class="i-carbon-document text-primary"></span>
              参考答案
            </p>
            <div class="text-slate-700 leading-relaxed whitespace-pre-line">{{ answer.referenceAnswer }}</div>
          </n-card>
        </template>
      </div>
    </n-collapse-transition>
  </div>
</template>

<script setup lang="ts">
/**
 * 问题卡片组件
 * - 显示问题、答案、评分
 * - 可展开查看详情
 */
import { computed } from 'vue'
import type { AnswerItem } from '@/types/resume'
import { getScoreColor } from '@/utils/score'

const props = defineProps<{
  answer: AnswerItem
  index: number
  expanded: boolean
}>()

defineEmits<{
  toggle: []
}>()

const scoreColorClass = computed(() => getScoreColor(props.answer.score, [80, 60]))
</script>