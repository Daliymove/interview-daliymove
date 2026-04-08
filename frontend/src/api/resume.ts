/**
 * 简历 API
 * - 上传简历并分析
 * - 健康检查
 */
import { request } from '@/utils/request'
import type { UploadResponse, Result } from '@/types/resume'

export const resumeApi = {
  /**
   * 上传简历并获取分析结果
   * 
   * @param file 简历文件
   * @returns 上传响应，包含分析结果和存储信息
   */
  async uploadAndAnalyze(file: File): Promise<UploadResponse> {
    const formData = new FormData()
    formData.append('file', file)
    return request.upload<Result<UploadResponse>>('/resume/upload', formData)
      .then((res: Result<UploadResponse>) => res.data)
  },

  /**
   * 健康检查
   * 
   * @returns 健康状态
   */
  async healthCheck(): Promise<{ status: string; service: string }> {
    return request.get<Result<{ status: string; service: string }>>('/resume/health')
      .then((res) => res.data)
  },
}