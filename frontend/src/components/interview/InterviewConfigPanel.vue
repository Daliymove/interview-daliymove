<template>
  <div class="max-w-2xl mx-auto">
    <n-card>
      <h2 class="text-2xl font-bold text-slate-900 mb-6 flex items-center gap-3">
        <div class="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center">
          <span class="i-carbon-settings text-primary text-xl"></span>
        </div>
        面试配置
      </h2>

      <template v-if="checkingUnfinished">
        <n-alert type="info" class="mb-6" :bordered="false">
          <template #icon>
            <n-spin size="small" />
          </template>
          正在检查是否有未完成的面试...
        </n-alert>
      </template>

      <template v-if="unfinishedSession && !checkingUnfinished">
        <n-alert type="warning" class="mb-6" :bordered="false">
          <template #header>
            检测到未完成的模拟面试
          </template>
          <p class="mb-2">已完成 {{ unfinishedSession.currentQuestionIndex }} / {{ unfinishedSession.totalQuestions }} 题</p>
          <n-space>
            <n-button type="warning" @click="$emit('continueUnfinished')">继续完成</n-button>
            <n-button quaternary @click="$emit('startNew')">开始新的</n-button>
          </n-space>
        </n-alert>
      </template>

      <n-form class="space-y-6">
        <n-form-item label="题目数量">
          <n-space>
            <n-button
              v-for="count in questionCounts"
              :key="count"
              :type="questionCount === count ? 'primary' : 'default'"
              round
              @click="$emit('questionCountChange', count)"
            >
              {{ count }}
            </n-button>
          </n-space>
        </n-form-item>

        <n-form-item label="简历预览（前500字）">
          <n-input
            :value="resumePreview"
            type="textarea"
            :rows="4"
            readonly
          />
        </n-form-item>

        <p class="text-sm text-slate-500 mb-6">
          题目分布：项目经历(20%) + MySQL(20%) + Redis(20%) + Java基础/集合/并发(30%) + Spring(10%)
        </p>

        <template v-if="error">
          <n-alert type="error" class="mb-6" :bordered="false">
            {{ error }}
          </n-alert>
        </template>

        <n-space justify="center">
          <n-button size="large" @click="$emit('back')">← 返回</n-button>
          <n-button
            type="primary"
            size="large"
            :loading="isCreating"
            :disabled="isCreating"
            @click="$emit('start')"
          >
            {{ isCreating ? '正在生成题目...' : '开始面试 →' }}
          </n-button>
        </n-space>
      </n-form>
    </n-card>
  </div>
</template>

<script setup lang="ts">
/**
 * 面试配置面板组件
 * - 选择题目数量
 * - 简历预览
 * - 处理未完成面试
 */
import { computed } from 'vue'
import type { InterviewSession } from '@/types/interview'

const props = defineProps<{
  questionCount: number
  isCreating: boolean
  checkingUnfinished: boolean
  unfinishedSession?: InterviewSession | null
  resumeText: string
  error?: string
}>()

defineEmits<{
  questionCountChange: [count: number]
  start: []
  continueUnfinished: []
  startNew: []
  back: []
}>()

const questionCounts = [6, 8, 10, 12, 15]

const resumePreview = computed(() => {
  const text = props.resumeText.substring(0, 500)
  return text + (props.resumeText.length > 500 ? '...' : '')
})
</script>