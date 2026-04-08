/**
 * 历史 API
 * - 获取简历列表
 * - 获取简历详情
 * - 获取面试详情
 * - 导出PDF
 * - 删除简历/面试
 */
import { request } from '@/utils/request'
import type {
  InterviewDetail,
  ResumeDetail,
  ResumeListItem,
  ResumeStats,
  Result,
} from '@/types/resume'

export const historyApi = {
  /**
   * 获取所有简历列表
   * 
   * @returns 简历列表
   */
  async getResumes(): Promise<ResumeListItem[]> {
    return request.get<Result<ResumeListItem[]>>('/resume/history')
      .then((res) => res.data)
  },

  /**
   * 获取简历详情
   * 
   * @param id 简历ID
   * @returns 简历详情
   */
  async getResumeDetail(id: number): Promise<ResumeDetail> {
    return request.get<Result<ResumeDetail>>(`/resume/${id}`)
      .then((res) => res.data)
  },

  /**
   * 获取面试详情
   * 
   * @param sessionId 会话ID
   * @returns 面试详情
   */
  async getInterviewDetail(sessionId: string): Promise<InterviewDetail> {
    return request.get<Result<InterviewDetail>>(`/api/interview/sessions/${sessionId}/details`)
      .then((res) => res.data)
  },

  /**
   * 导出简历分析报告PDF
   * 
   * @param resumeId 简历ID
   * @returns PDF Blob
   */
  async exportAnalysisPdf(resumeId: number): Promise<Blob> {
    const response = await request.getInstance().get(`/resume/${resumeId}/export`, {
      responseType: 'blob',
    })
    return response.data
  },

  /**
   * 导出面试报告PDF
   * 
   * @param sessionId 会话ID
   * @returns PDF Blob
   */
  async exportInterviewPdf(sessionId: string): Promise<Blob> {
    const response = await request.getInstance().get(`/api/interview/sessions/${sessionId}/export`, {
      responseType: 'blob',
    })
    return response.data
  },

  /**
   * 删除简历
   * 
   * @param id 简历ID
   */
  async deleteResume(id: number): Promise<void> {
    return request.delete(`/resume/${id}`)
  },

  /**
   * 删除面试记录
   * 
   * @param sessionId 会话ID
   */
  async deleteInterview(sessionId: string): Promise<void> {
    return request.delete(`/api/interview/sessions/${sessionId}`)
  },

  /**
   * 获取简历统计信息
   * 
   * @returns 统计信息
   */
  async getStatistics(): Promise<ResumeStats> {
    return request.get<Result<ResumeStats>>('/resume/statistics')
      .then((res) => res.data)
  },

  /**
   * 重新分析简历
   * 
   * @param id 简历ID
   */
  async reanalyze(id: number): Promise<void> {
    return request.post(`/resume/${id}/reanalyze`)
  },
}