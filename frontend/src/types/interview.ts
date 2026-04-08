/**
 * 面试相关类型定义
 * - 面试会话
 * - 面试问题
 * - 答案提交
 * - 面试报告
 */

/** 面试会话 */
export interface InterviewSession {
  sessionId: string
  resumeText: string
  totalQuestions: number
  currentQuestionIndex: number
  questions: InterviewQuestion[]
  status: 'CREATED' | 'IN_PROGRESS' | 'COMPLETED' | 'EVALUATED'
}

/** 面试问题 */
export interface InterviewQuestion {
  questionIndex: number
  question: string
  type: QuestionType
  category: string
  userAnswer: string | null
  score: number | null
  feedback: string | null
}

/** 问题类型 */
export type QuestionType =
  | 'PROJECT'
  | 'JAVA_BASIC'
  | 'JAVA_COLLECTION'
  | 'JAVA_CONCURRENT'
  | 'MYSQL'
  | 'REDIS'
  | 'SPRING'
  | 'SPRING_BOOT'

/** 创建面试请求 */
export interface CreateInterviewRequest {
  resumeText: string
  questionCount: number
  resumeId?: number
  forceCreate?: boolean
}

/** 提交答案请求 */
export interface SubmitAnswerRequest {
  sessionId: string
  questionIndex: number
  answer: string
}

/** 提交答案响应 */
export interface SubmitAnswerResponse {
  hasNextQuestion: boolean
  nextQuestion: InterviewQuestion | null
  currentIndex: number
  totalQuestions: number
}

/** 当前问题响应 */
export interface CurrentQuestionResponse {
  completed: boolean
  question?: InterviewQuestion
  message?: string
}

/** 面试报告 */
export interface InterviewReport {
  sessionId: string
  totalQuestions: number
  overallScore: number
  categoryScores: CategoryScore[]
  questionDetails: QuestionEvaluation[]
  overallFeedback: string
  strengths: string[]
  improvements: string[]
  referenceAnswers: ReferenceAnswer[]
}

/** 分类评分 */
export interface CategoryScore {
  category: string
  score: number
  questionCount: number
}

/** 问题评估 */
export interface QuestionEvaluation {
  questionIndex: number
  question: string
  category: string
  userAnswer: string
  score: number
  feedback: string
}

/** 参考答案 */
export interface ReferenceAnswer {
  questionIndex: number
  question: string
  referenceAnswer: string
  keyPoints: string[]
}