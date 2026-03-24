<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-chat-bot text-primary text-xl"></span>
          <span class="text-lg font-semibold">面试助手</span>
        </div>
        <n-space>
          <n-button @click="showQuestionBank = true">
            <template #icon>
              <span class="i-carbon-catalog"></span>
            </template>
            题库管理
          </n-button>
          <n-button type="primary" @click="startInterview">
            <template #icon>
              <span class="i-carbon-play-filled"></span>
            </template>
            开始模拟面试
          </n-button>
        </n-space>
      </div>

      <n-tabs v-model:value="activeTab" type="line" animated>
        <n-tab-pane name="questions" tab="面试题库">
          <div class="flex flex-wrap gap-3 mb-4">
            <n-select
              v-model:value="questionFilter.category"
              :options="categoryOptions"
              placeholder="题目分类"
              class="w-40"
              clearable
            />
            <n-select
              v-model:value="questionFilter.difficulty"
              :options="difficultyOptions"
              placeholder="难度等级"
              class="w-32"
              clearable
            />
            <n-button type="primary" @click="loadQuestions">
              <template #icon>
                <span class="i-carbon-search"></span>
              </template>
              筛选
            </n-button>
          </div>

          <n-list hoverable>
            <n-list-item v-for="question in questionList" :key="question.id">
              <n-thing :title="question.title">
                <template #header-extra>
                  <n-tag :type="getDifficultyType(question.difficulty)" size="small">
                    {{ getDifficultyLabel(question.difficulty) }}
                  </n-tag>
                </template>
                <template #description>
                  <n-space>
                    <n-tag v-for="tag in question.tags" :key="tag" size="small" round>
                      {{ tag }}
                    </n-tag>
                  </n-space>
                </template>
              </n-thing>
              <template #suffix>
                <n-space>
                  <n-button size="small" quaternary type="primary" @click="viewAnswer(question)">
                    查看答案
                  </n-button>
                  <n-button size="small" quaternary type="info" @click="practice(question)">
                    练习
                  </n-button>
                </n-space>
              </template>
            </n-list-item>
          </n-list>
        </n-tab-pane>

        <n-tab-pane name="history" tab="面试记录">
          <n-data-table
            :columns="historyColumns"
            :data="historyData"
            :bordered="false"
          />
        </n-tab-pane>

        <n-tab-pane name="analysis" tab="数据分析">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <n-card>
              <n-statistic label="已练习题目" :value="statistics.totalQuestions">
                <template #suffix>道</template>
              </n-statistic>
            </n-card>
            <n-card>
              <n-statistic label="平均得分" :value="statistics.avgScore">
                <template #suffix>分</template>
              </n-statistic>
            </n-card>
            <n-card>
              <n-statistic label="模拟面试次数" :value="statistics.interviewCount">
                <template #suffix>次</template>
              </n-statistic>
            </n-card>
          </div>
        </n-tab-pane>
      </n-tabs>
    </n-card>

    <n-modal v-model:show="answerVisible" preset="card" title="题目答案" class="w-[600px]" :bordered="false">
      <n-descriptions label-placement="top" :column="1">
        <n-descriptions-item label="题目">
          {{ currentQuestion?.title }}
        </n-descriptions-item>
        <n-descriptions-item label="参考答案">
          <n-card embedded class="bg-gray-50">
            {{ currentQuestion?.answer }}
          </n-card>
        </n-descriptions-item>
      </n-descriptions>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NRate } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'

interface Question {
  id: number
  title: string
  category: string
  difficulty: number
  tags: string[]
  answer: string
}

interface HistoryRecord {
  id: number
  date: string
  questionCount: number
  score: number
  duration: string
}

const activeTab = ref('questions')
const showQuestionBank = ref(false)
const answerVisible = ref(false)
const currentQuestion = ref<Question | null>(null)

const questionFilter = reactive({
  category: null as string | null,
  difficulty: null as number | null
})

const categoryOptions = [
  { label: 'Java', value: 'java' },
  { label: '前端', value: 'frontend' },
  { label: '数据库', value: 'database' },
  { label: '系统设计', value: 'system' }
]

const difficultyOptions = [
  { label: '简单', value: 1 },
  { label: '中等', value: 2 },
  { label: '困难', value: 3 }
]

const statistics = reactive({
  totalQuestions: 128,
  avgScore: 85.5,
  interviewCount: 12
})

const questionList = ref<Question[]>([])

const historyColumns: DataTableColumns<HistoryRecord> = [
  { title: '日期', key: 'date', width: 180 },
  { title: '题目数量', key: 'questionCount', width: 100 },
  { 
    title: '得分', 
    key: 'score', 
    width: 150,
    render(row) {
      return h(NRate, { value: row.score / 20, readonly: true, size: 'small' })
    }
  },
  { title: '用时', key: 'duration', width: 100 },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render() {
      return h(NButton, { size: 'small', quaternary: true, type: 'primary' }, { default: () => '查看详情' })
    }
  }
]

const historyData = ref<HistoryRecord[]>([])

const getDifficultyType = (difficulty: number) => {
  const map: Record<number, 'success' | 'warning' | 'error'> = {
    1: 'success',
    2: 'warning',
    3: 'error'
  }
  return map[difficulty] || 'info'
}

const getDifficultyLabel = (difficulty: number) => {
  const map: Record<number, string> = {
    1: '简单',
    2: '中等',
    3: '困难'
  }
  return map[difficulty] || '未知'
}

const loadQuestions = async () => {
  questionList.value = [
    { id: 1, title: '请解释Java中的多态是什么？', category: 'java', difficulty: 2, tags: ['Java', 'OOP'], answer: '多态是指同一个方法调用，由于对象不同可能会有不同的行为...' },
    { id: 2, title: 'Vue3中的响应式原理是什么？', category: 'frontend', difficulty: 2, tags: ['Vue', '前端'], answer: 'Vue3使用Proxy来实现响应式...' },
    { id: 3, title: '请解释MySQL的索引结构', category: 'database', difficulty: 3, tags: ['MySQL', '索引'], answer: 'MySQL使用B+树作为索引结构...' }
  ]
}

const viewAnswer = (question: Question) => {
  currentQuestion.value = question
  answerVisible.value = true
}

const practice = (question: Question) => {
  window.$message.info(`开始练习: ${question.title}`)
}

const startInterview = () => {
  window.$message.info('模拟面试功能开发中...')
}

const loadHistory = () => {
  historyData.value = [
    { id: 1, date: '2024-01-15 14:30', questionCount: 10, score: 85, duration: '25分钟' },
    { id: 2, date: '2024-01-14 10:00', questionCount: 15, score: 92, duration: '35分钟' },
    { id: 3, date: '2024-01-13 16:00', questionCount: 8, score: 78, duration: '20分钟' }
  ]
}

onMounted(() => {
  loadQuestions()
  loadHistory()
})
</script>