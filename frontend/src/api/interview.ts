/**
 * 面试 API
 * - 创建面试会话
 * - 获取会话信息
 * - 提交答案
 * - 获取面试报告
 */
import { request } from '@/utils/request'
import type {
  CreateInterviewRequest,
  CurrentQuestionResponse,
  InterviewReport,
  InterviewSession,
  SubmitAnswerRequest,
  SubmitAnswerResponse,
} from '@/types/interview'
import type { Result } from '@/types/resume'

export const interviewApi = {
  /**
   * 创建面试会话
   * 
   * @param req 创建请求
   * @returns 面试会话
   */
async createSession(req: CreateInterviewRequest): Promise<InterviewSession> {
    return request.post<Result<InterviewSession>>('/interview/sessions', req, {
      timeout: 180000,
    }).then((res) => res.data)
  },

  async getSession(sessionId: string): Promise<InterviewSession> {
    return request.get<Result<InterviewSession>>(`/interview/sessions/${sessionId}`)
      .then((res) => res.data)
  },

  async getCurrentQuestion(sessionId: string): Promise<CurrentQuestionResponse> {
    return request.get<Result<CurrentQuestionResponse>>(`/interview/sessions/${sessionId}/question`)
      .then((res) => res.data)
  },

  async submitAnswer(req: SubmitAnswerRequest): Promise<SubmitAnswerResponse> {
    return request.post<Result<SubmitAnswerResponse>>(
      `/interview/sessions/${req.sessionId}/answers`,
      { questionIndex: req.questionIndex, answer: req.answer },
      { timeout: 180000 }
    ).then((res) => res.data)
  },

  async getReport(sessionId: string): Promise<InterviewReport> {
    return request.get<Result<InterviewReport>>(`/interview/sessions/${sessionId}/report`, null, {
      timeout: 180000,
    }).then((res) => res.data)
  },

  async findUnfinishedSession(resumeId: number): Promise<InterviewSession | null> {
    try {
      return await request.get<Result<InterviewSession>>(`/interview/sessions/unfinished/${resumeId}`)
        .then((res) => res.data)
    } catch {
      return null
    }
  },

  async saveAnswer(req: SubmitAnswerRequest): Promise<void> {
    return request.put<void>(
      `/interview/sessions/${req.sessionId}/answers`,
      { questionIndex: req.questionIndex, answer: req.answer }
    )
  },

  async completeInterview(sessionId: string): Promise<void> {
    return request.post<void>(`/interview/sessions/${sessionId}/complete`)
  },

  async reevaluateInterview(sessionId: string): Promise<void> {
    return request.post<void>(`/interview/sessions/${sessionId}/reevaluate`)
  },
}