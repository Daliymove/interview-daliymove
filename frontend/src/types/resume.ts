/**
 * 简历相关类型定义
 * - 简历分析结果
 * - 上传响应
 * - 评分详情
 * - 建议项
 */

/** API 响应包装 */
export interface Result<T> {
  code: number
  message: string
  data: T
  timestamp?: number
}

/** 简历分析响应类型 */
export interface ResumeAnalysisResponse {
  overallScore: number
  scoreDetail: ScoreDetail
  summary: string
  strengths: string[]
  suggestions: Suggestion[]
  originalText: string
}

/** 存储信息 */
export interface StorageInfo {
  fileKey: string
  fileUrl: string
  resumeId?: number
}

/** 上传API完整响应（异步模式：analysis 可能为空） */
export interface UploadResponse {
  analysis?: ResumeAnalysisResponse
  storage: StorageInfo
  duplicate?: boolean
  message?: string
}

/** 评分详情 */
export interface ScoreDetail {
  contentScore: number
  structureScore: number
  skillMatchScore: number
  expressionScore: number
  projectScore: number
}

/** 建议项 */
export interface Suggestion {
  category: string
  priority: '高' | '中' | '低'
  issue: string
  recommendation: string
}

/** API 错误响应 */
export interface ApiError {
  error: string
  detectedType?: string
  allowedTypes?: string[]
}

/** 分析状态 */
export type AnalyzeStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

/** 评估状态 */
export type EvaluateStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

/** 简历列表项 */
export interface ResumeListItem {
  id: number
  filename: string
  fileSize: number
  uploadedAt: string
  accessCount: number
  latestScore?: number
  lastAnalyzedAt?: string
  interviewCount: number
  analyzeStatus?: AnalyzeStatus
  analyzeError?: string
  storageUrl?: string
}

/** 简历统计 */
export interface ResumeStats {
  totalCount: number
  analyzedCount: number
  pendingCount: number
}

/** 分析项 */
export interface AnalysisItem {
  id: number
  overallScore: number
  contentScore: number
  structureScore: number
  skillMatchScore: number
  expressionScore: number
  projectScore: number
  summary: string
  analyzedAt: string
  strengths: string[]
  suggestions: unknown[]
}

/** 面试项 */
export interface InterviewItem {
  id: number
  sessionId: string
  totalQuestions: number
  status: string
  evaluateStatus?: EvaluateStatus
  evaluateError?: string
  overallScore: number | null
  overallFeedback: string | null
  createdAt: string
  completedAt: string | null
  questions?: unknown[]
  strengths?: string[]
  improvements?: string[]
  referenceAnswers?: unknown[]
}

/** 答案项 */
export interface AnswerItem {
  questionIndex: number
  question: string
  category: string
  userAnswer: string
  score: number
  feedback: string
  referenceAnswer?: string
  keyPoints?: string[]
  answeredAt: string
}

/** 简历详情 */
export interface ResumeDetail {
  id: number
  filename: string
  fileSize: number
  contentType: string
  storageUrl: string
  uploadedAt: string
  accessCount: number
  resumeText: string
  analyzeStatus?: AnalyzeStatus
  analyzeError?: string
  analyses: AnalysisItem[]
  interviews: InterviewItem[]
}

/** 面试详情（继承自 InterviewItem） */
export interface InterviewDetail extends InterviewItem {
  evaluateStatus?: EvaluateStatus
  evaluateError?: string
  answers: AnswerItem[]
}