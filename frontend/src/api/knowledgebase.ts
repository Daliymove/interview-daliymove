/**
 * 知识库 API
 * - 上传知识库文件
 * - 下载知识库文件
 * - 获取知识库列表
 * - 查询知识库
 * - 流式查询（SSE）
 */
import { request, getErrorMessage } from '@/utils/request'
import axios from 'axios'
import type {
  KnowledgeBaseItem,
  KnowledgeBaseStats,
  QueryRequest,
  QueryResponse,
  SortOption,
  UploadKnowledgeBaseResponse,
  VectorStatus,
} from '@/types/knowledge'
import type { Result } from '@/types/resume'

const API_BASE_URL = import.meta.env.PROD ? '' : 'http://localhost:8080'

export const knowledgeBaseApi = {
  /**
   * 上传知识库文件
   * 
   * @param file 文件
   * @param name 知识库名称
   * @param category 分类
   * @returns 上传响应
   */
  async uploadKnowledgeBase(file: File, name?: string, category?: string): Promise<UploadKnowledgeBaseResponse> {
    const formData = new FormData()
    formData.append('file', file)
    if (name) {
      formData.append('name', name)
    }
    if (category) {
      formData.append('category', category)
    }
    return request.upload<Result<UploadKnowledgeBaseResponse>>('/api/knowledgebase/upload', formData)
      .then((res) => res.data)
  },

  /**
   * 下载知识库文件
   * 
   * @param id 知识库ID
   * @returns 文件Blob
   */
  async downloadKnowledgeBase(id: number): Promise<Blob> {
    const response = await axios.get(`${API_BASE_URL}/api/knowledgebase/${id}/download`, {
      responseType: 'blob',
    })
    return response.data
  },

  /**
   * 获取所有知识库列表
   * 
   * @param sortBy 排序字段
   * @param vectorStatus 向量化状态
   * @returns 知识库列表
   */
  async getAllKnowledgeBases(sortBy?: SortOption, vectorStatus?: VectorStatus): Promise<KnowledgeBaseItem[]> {
    const params = new URLSearchParams()
    if (sortBy) {
      params.append('sortBy', sortBy)
    }
    if (vectorStatus) {
      params.append('vectorStatus', vectorStatus)
    }
    const queryString = params.toString()
    return request.get<Result<KnowledgeBaseItem[]>>(`/api/knowledgebase/list${queryString ? `?${queryString}` : ''}`)
      .then((res) => res.data)
  },

  /**
   * 获取知识库详情
   * 
   * @param id 知识库ID
   * @returns 知识库详情
   */
  async getKnowledgeBase(id: number): Promise<KnowledgeBaseItem> {
    return request.get<Result<KnowledgeBaseItem>>(`/api/knowledgebase/${id}`)
      .then((res) => res.data)
  },

  /**
   * 删除知识库
   * 
   * @param id 知识库ID
   */
  async deleteKnowledgeBase(id: number): Promise<void> {
    return request.delete(`/api/knowledgebase/${id}`)
  },

  /**
   * 获取所有分类
   * 
   * @returns 分类列表
   */
  async getAllCategories(): Promise<string[]> {
    return request.get<Result<string[]>>('/api/knowledgebase/categories')
      .then((res) => res.data)
  },

  /**
   * 根据分类获取知识库
   * 
   * @param category 分类名称
   * @returns 知识库列表
   */
  async getByCategory(category: string): Promise<KnowledgeBaseItem[]> {
    return request.get<Result<KnowledgeBaseItem[]>>(`/api/knowledgebase/category/${encodeURIComponent(category)}`)
      .then((res) => res.data)
  },

  /**
   * 获取未分类的知识库
   * 
   * @returns 知识库列表
   */
  async getUncategorized(): Promise<KnowledgeBaseItem[]> {
    return request.get<Result<KnowledgeBaseItem[]>>('/api/knowledgebase/uncategorized')
      .then((res) => res.data)
  },

  /**
   * 更新知识库分类
   * 
   * @param id 知识库ID
   * @param category 分类名称
   */
  async updateCategory(id: number, category: string | null): Promise<void> {
    return request.put(`/api/knowledgebase/${id}/category`, { category })
  },

  /**
   * 搜索知识库
   * 
   * @param keyword 关键词
   * @returns 知识库列表
   */
  async search(keyword: string): Promise<KnowledgeBaseItem[]> {
    return request.get<Result<KnowledgeBaseItem[]>>(`/api/knowledgebase/search?keyword=${encodeURIComponent(keyword)}`)
      .then((res) => res.data)
  },

  /**
   * 获取知识库统计信息
   * 
   * @returns 统计信息
   */
  async getStatistics(): Promise<KnowledgeBaseStats> {
    return request.get<Result<KnowledgeBaseStats>>('/api/knowledgebase/stats')
      .then((res) => res.data)
  },

  /**
   * 重新向量化知识库（手动重试）
   * 
   * @param id 知识库ID
   */
  async revectorize(id: number): Promise<void> {
    return request.post(`/api/knowledgebase/${id}/revectorize`)
  },

  /**
   * 基于知识库回答问题
   * 
   * @param req 查询请求
   * @returns 查询响应
   */
  async queryKnowledgeBase(req: QueryRequest): Promise<QueryResponse> {
    return request.post<Result<QueryResponse>>('/api/knowledgebase/query', req, {
      timeout: 180000,
    }).then((res) => res.data)
  },

  /**
   * 基于知识库回答问题（流式SSE）
   * 
   * @param req 查询请求
   * @param onMessage 消息回调
   * @param onComplete 完成回调
   * @param onError 错误回调
   */
  async queryKnowledgeBaseStream(
    req: QueryRequest,
    onMessage: (chunk: string) => void,
    onComplete: () => void,
    onError: (error: Error) => void
  ): Promise<void> {
    try {
      const response = await fetch(`${API_BASE_URL}/api/knowledgebase/query/stream`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req),
      })

      if (!response.ok) {
        try {
          const errorData = await response.json()
          if (errorData && errorData.message) {
            throw new Error(errorData.message)
          }
        } catch {
          // ignore
        }
        throw new Error(`请求失败 (${response.status})`)
      }

      const reader = response.body?.getReader()
      if (!reader) {
        throw new Error('无法获取响应流')
      }

      const decoder = new TextDecoder()
      let buffer = ''

      const extractContent = (line: string): string | null => {
        if (!line.startsWith('data:')) {
          return null
        }
        let content = line.substring(5)
        if (content.startsWith(' ')) {
          content = content.substring(1)
        }
        if (content.length === 0) {
          return '\n'
        }
        return content
      }

      while (true) {
        const { done, value } = await reader.read()

        if (done) {
          if (buffer) {
            const content = extractContent(buffer)
            if (content) {
              onMessage(content)
            }
          }
          onComplete()
          break
        }

        buffer += decoder.decode(value, { stream: true })

        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          const content = extractContent(line)
          if (content !== null) {
            onMessage(content)
          }
        }
      }
    } catch (error) {
      onError(new Error(getErrorMessage(error)))
    }
  },
}