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
    return request.post<Result<InterviewSession>>('/api/interview/sessions', req, {
      timeout: 180000,
    }).then((res) => res.data)
  },

  /**
   * 获取会话信息
   * 
   * @param sessionId 会话ID
   * @returns 面试会话
   */
  async getSession(sessionId: string): Promise<InterviewSession> {
    return request.get<Result<InterviewSession>>(`/api/interview/sessions/${sessionId}`)
      .then((res) => res.data)
  },

  /**
   * 获取当前问题
   * 
   * @param sessionId 会话ID
   * @returns 当前问题响应
   */
  async getCurrentQuestion(sessionId: string): Promise<CurrentQuestionResponse> {
    return request.get<Result<CurrentQuestionResponse>>(`/api/interview/sessions/${sessionId}/question`)
      .then((res) => res.data)
  },

  /**
   * 提交答案
   * 
   * @param req 提交答案请求
   * @returns 提交答案响应
   */
  async submitAnswer(req: SubmitAnswerRequest): Promise<SubmitAnswerResponse> {
    return request.post<Result<SubmitAnswerResponse>>(
      `/api/interview/sessions/${req.sessionId}/answers`,
      { questionIndex: req.questionIndex, answer: req.answer },
      { timeout: 180000 }
    ).then((res) => res.data)
  },

  /**
   * 获取面试报告
   * 
   * @param sessionId 会话ID
   * @returns 面试报告
   */
  async getReport(sessionId: string): Promise<InterviewReport> {
    return request.get<Result<InterviewReport>>(`/api/interview/sessions/${sessionId}/report`, null, {
      timeout: 180000,
    }).then((res) => res.data)
  },

  /**
   * 查找未完成的面试会话
   * 
   * @param resumeId 简历ID
   * @returns 面试会话或null
   */
  async findUnfinishedSession(resumeId: number): Promise<InterviewSession | null> {
    try {
      return await request.get<Result<InterviewSession>>(`/api/interview/sessions/unfinished/${resumeId}`)
        .then((res) => res.data)
    } catch {
      return null
    }
  },

  /**
   * 暂存答案（不进入下一题）
   * 
   * @param req 提交答案请求
   */
  async saveAnswer(req: SubmitAnswerRequest): Promise<void> {
    return request.put<void>(
      `/api/interview/sessions/${req.sessionId}/answers`,
      { questionIndex: req.questionIndex, answer: req.answer }
    )
  },

  /**
   * 提前交卷
   * 
   * @param sessionId 会话ID
   */
  async completeInterview(sessionId: string): Promise<void> {
    return request.post<void>(`/api/interview/sessions/${sessionId}/complete`)
  },

  /**
   * 重新评估面试结果
   * 
   * @param sessionId 会话ID
   */
  async reevaluateInterview(sessionId: string): Promise<void> {
    return request.post<void>(`/api/interview/sessions/${sessionId}/reevaluate`)
  },
}