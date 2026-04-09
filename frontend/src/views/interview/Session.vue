<template>
  <div class="flex flex-col pb-8">
    <div class="max-w-4xl mx-auto w-full">
      <div class="text-center mb-10">
        <div class="w-14 h-14 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center mx-auto mb-4">
          <span class="i-carbon-microphone text-white text-2xl"></span>
        </div>
        <h1 class="text-3xl font-bold text-slate-900 mb-3">模拟面试</h1>
        <p class="text-slate-600 text-lg">{{ stageSubtitles[stage] }}</p>
      </div>

      <div class="flex-1">
        <n-spin :show="checkingUnfinished" class="h-full">
        <template v-if="stage === 'config'">
          <InterviewConfigPanel
            :question-count="questionCount"
            :is-creating="isCreating"
            :checking-unfinished="checkingUnfinished"
            :unfinished-session="unfinishedSession"
            :resume-text="resumeText"
            :error="error"
            @question-count-change="handleQuestionCountChange"
            @start="startInterview"
            @continue-unfinished="handleContinueUnfinished"
            @start-new="handleStartNew"
            @back="handleBack"
          />
        </template>

        <template v-if="stage === 'interview' && session && currentQuestion">
            <InterviewChatPanel
              :session="session"
              :current-question="currentQuestion"
              :messages="messages"
              :is-submitting="isSubmitting"
              :on-show-complete-confirm="handleShowCompleteConfirm"
              @answer-change="handleAnswerChange"
              @submit="handleSubmitAnswer"
            />
          </template>
        </n-spin>
      </div>
    </div>

    <n-modal
      v-model:show="showCompleteConfirm"
      preset="dialog"
      title="提前交卷"
      content="确定要提前交卷吗？未回答的问题将按0分计算。"
      positive-text="确定交卷"
      negative-text="取消"
      type="warning"
      :loading="isSubmitting"
      @positive-click="handleCompleteEarly"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 面试会话页面
 * - 面试配置（题目数量选择）
 * - 检查未完成面试
 * - 面试对话界面
 * - 提交答案
 * - 提前交卷
 * - 跳转到面试记录页
 */
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { interviewApi } from '@/api/interview'
import { historyApi } from '@/api/history'
import InterviewConfigPanel from '@/components/interview/InterviewConfigPanel.vue'
import InterviewChatPanel from '@/components/interview/InterviewChatPanel.vue'
import type { InterviewSession, InterviewQuestion } from '@/types/interview'

type InterviewStage = 'config' | 'interview'

interface Message {
  type: 'interviewer' | 'user'
  content: string
  category?: string
  questionIndex?: number
}

const router = useRouter()
const route = useRoute()

const stage = ref<InterviewStage>('config')
const questionCount = ref(8)
const session = ref<InterviewSession | null>(null)
const currentQuestion = ref<InterviewQuestion | null>(null)
const messages = ref<Message[]>([])
const answer = ref('')
const isSubmitting = ref(false)
const error = ref('')
const isCreating = ref(false)
const checkingUnfinished = ref(false)
const unfinishedSession = ref<InterviewSession | null>(null)
const showCompleteConfirm = ref(false)
const forceCreateNew = ref(false)
const resumeText = ref('')
const resumeId = ref<number | undefined>(undefined)

const stageSubtitles: Record<InterviewStage, string> = {
  config: '配置您的面试参数',
  interview: '认真回答每个问题，展示您的实力'
}

const checkUnfinishedSession = async () => {
  if (!resumeId.value) return
  
  checkingUnfinished.value = true
  try {
    const foundSession = await interviewApi.findUnfinishedSession(resumeId.value)
    if (foundSession) {
      unfinishedSession.value = foundSession
    }
  } catch (err) {
    console.error('检查未完成面试失败', err)
  } finally {
    checkingUnfinished.value = false
  }
}

const handleContinueUnfinished = () => {
  if (!unfinishedSession.value) return
  forceCreateNew.value = false
  restoreSession(unfinishedSession.value)
  unfinishedSession.value = null
}

const handleStartNew = () => {
  unfinishedSession.value = null
  forceCreateNew.value = true
}

const restoreSession = (sessionToRestore: InterviewSession) => {
  session.value = sessionToRestore
  
  const currentQ = sessionToRestore.questions[sessionToRestore.currentQuestionIndex]
  if (currentQ) {
    currentQuestion.value = currentQ
    
    if (currentQ.userAnswer) {
      answer.value = currentQ.userAnswer
    }
    
    const restoredMessages: Message[] = []
    for (let i = 0; i <= sessionToRestore.currentQuestionIndex; i++) {
      const q = sessionToRestore.questions[i]
      restoredMessages.push({
        type: 'interviewer',
        content: q.question,
        category: q.category,
        questionIndex: i
      })
      if (q.userAnswer) {
        restoredMessages.push({
          type: 'user',
          content: q.userAnswer
        })
      }
    }
    messages.value = restoredMessages
  }
  
  stage.value = 'interview'
}

const startInterview = async () => {
  isCreating.value = true
  error.value = ''
  
  try {
    const newSession = await interviewApi.createSession({
      resumeText: resumeText.value,
      questionCount: questionCount.value,
      resumeId: resumeId.value,
      forceCreate: forceCreateNew.value
    })
    
    forceCreateNew.value = false
    
    const hasProgress = newSession.currentQuestionIndex > 0 ||
                        newSession.questions.some(q => q.userAnswer) ||
                        newSession.status === 'IN_PROGRESS'
    
    if (hasProgress) {
      restoreSession(newSession)
    } else {
      session.value = newSession
      
      if (newSession.questions.length > 0) {
        const firstQuestion = newSession.questions[0]
        currentQuestion.value = firstQuestion
        messages.value = [{
          type: 'interviewer',
          content: firstQuestion.question,
          category: firstQuestion.category,
          questionIndex: 0
        }]
      }
      
      stage.value = 'interview'
    }
  } catch (err) {
    error.value = '创建面试失败，请重试'
    console.error(err)
    forceCreateNew.value = false
  } finally {
    isCreating.value = false
  }
}

const handleSubmitAnswer = async () => {
  if (!answer.value.trim() || !session.value || !currentQuestion.value) return
  
  isSubmitting.value = true
  
  const userMessage: Message = {
    type: 'user',
    content: answer.value
  }
  messages.value.push(userMessage)
  
  try {
    const response = await interviewApi.submitAnswer({
      sessionId: session.value.sessionId,
      questionIndex: currentQuestion.value.questionIndex,
      answer: answer.value.trim()
    })
    
    answer.value = ''
    
    if (response.hasNextQuestion && response.nextQuestion) {
      currentQuestion.value = response.nextQuestion
      messages.value.push({
        type: 'interviewer',
        content: response.nextQuestion.question,
        category: response.nextQuestion.category,
        questionIndex: response.nextQuestion.questionIndex
      })
    } else {
      router.push('/interview')
    }
  } catch (err) {
    error.value = '提交答案失败，请重试'
    console.error(err)
    messages.value.pop()
  } finally {
    isSubmitting.value = false
  }
}

const handleCompleteEarly = async () => {
  if (!session.value) return
  
  isSubmitting.value = true
  try {
    await interviewApi.completeInterview(session.value.sessionId)
    showCompleteConfirm.value = false
    router.push('/interview')
  } catch (err) {
    error.value = '提前交卷失败，请重试'
    console.error(err)
  } finally {
    isSubmitting.value = false
  }
}

const handleQuestionCountChange = (count: number) => {
  questionCount.value = count
}

const handleAnswerChange = (newAnswer: string) => {
  answer.value = newAnswer
}

const handleShowCompleteConfirm = (show: boolean) => {
  showCompleteConfirm.value = show
}

const handleBack = () => {
  router.back()
}

const loadResumeText = async (id: number) => {
  try {
    const detail = await historyApi.getResumeDetail(id)
    resumeText.value = detail.resumeText
  } catch (err) {
    console.error('加载简历失败', err)
    window.$message.error('加载简历失败，请返回重试')
  }
}

onMounted(async () => {
  const sessionId = route.query.sessionId as string
  const rid = route.query.resumeId
  
  if (sessionId) {
    try {
      const sessionData = await interviewApi.getSession(sessionId)
      if (sessionData) {
        restoreSession(sessionData)
        if (sessionData.questions[0]?.userAnswer) {
          resumeId.value = rid ? Number(rid) : undefined
        }
      }
    } catch (err) {
      console.error('加载面试会话失败', err)
      window.$message.error('加载面试会话失败')
      router.push('/interview')
    }
  } else if (rid) {
    resumeId.value = Number(rid)
    await loadResumeText(Number(rid))
    await checkUnfinishedSession()
  } else {
    window.$message.error('缺少必要参数')
    router.push('/interview')
  }
})

watch(resumeId, (newId) => {
  if (newId && stage.value === 'config') {
    checkUnfinishedSession()
  }
})
</script>