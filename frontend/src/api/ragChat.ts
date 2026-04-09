/**
 * RAG 聊天 API
 * - 创建会话
 * - 获取会话列表
 * - 获取会话详情
 * - 发送消息（流式SSE）
 */
import { request, getErrorMessage } from '@/utils/request'
import type {
  RagChatSession,
  RagChatSessionDetail,
  RagChatSessionListItem,
} from '@/types/knowledge'
import type { Result } from '@/types/resume'

const API_BASE_URL = import.meta.env.PROD ? '' : 'http://localhost:8901'

export const ragChatApi = {
  async createSession(knowledgeBaseIds: number[], title?: string): Promise<RagChatSession> {
    return request.post<Result<RagChatSession>>('/rag-chat/sessions', {
      knowledgeBaseIds,
      title,
    }).then((res) => res.data)
  },

  async listSessions(): Promise<RagChatSessionListItem[]> {
    return request.get<Result<RagChatSessionListItem[]>>('/rag-chat/sessions')
      .then((res) => res.data)
  },

  async getSessionDetail(sessionId: number): Promise<RagChatSessionDetail> {
    return request.get<Result<RagChatSessionDetail>>(`/rag-chat/sessions/${sessionId}`)
      .then((res) => res.data)
  },

  async updateSessionTitle(sessionId: number, title: string): Promise<void> {
    return request.put(`/rag-chat/sessions/${sessionId}/title`, { title })
  },

  async updateKnowledgeBases(sessionId: number, knowledgeBaseIds: number[]): Promise<void> {
    return request.put(`/rag-chat/sessions/${sessionId}/knowledge-bases`, {
      knowledgeBaseIds,
    })
  },

  async togglePin(sessionId: number): Promise<void> {
    return request.put(`/rag-chat/sessions/${sessionId}/pin`)
  },

  async deleteSession(sessionId: number): Promise<void> {
    return request.delete(`/rag-chat/sessions/${sessionId}`)
  },

  /**
   * 发送消息（流式SSE）
   * 
   * @param sessionId 会话ID
   * @param question 问题
   * @param onMessage 消息回调
   * @param onComplete 完成回调
   * @param onError 错误回调
   */
  async sendMessageStream(
    sessionId: number,
    question: string,
    onMessage: (chunk: string) => void,
    onComplete: () => void,
    onError: (error: Error) => void
  ): Promise<void> {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/rag-chat/sessions/${sessionId}/messages/stream`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ question }),
        }
      )

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

      const extractEventContent = (event: string): string | null => {
        if (!event.trim()) return null

        const lines = event.split('\n')
        const contentParts: string[] = []

        for (const line of lines) {
          if (line.startsWith('data:')) {
            contentParts.push(line.substring(5))
          }
        }

        if (contentParts.length === 0) return null

        return contentParts.join('')
          .replace(/\\n/g, '\n')
          .replace(/\\r/g, '\r')
      }

      while (true) {
        const { done, value } = await reader.read()

        if (done) {
          if (buffer) {
            const content = extractEventContent(buffer)
            if (content) {
              onMessage(content)
            }
          }
          onComplete()
          break
        }

        buffer += decoder.decode(value, { stream: true })

        let newlineIndex = buffer.indexOf('\n\n')
        if (newlineIndex === -1) {
          const singleLineIndex = buffer.indexOf('\n')
          if (singleLineIndex !== -1 && buffer.substring(0, singleLineIndex).startsWith('data:')) {
            const line = buffer.substring(0, singleLineIndex)
            const content = extractEventContent(line)
            if (content) {
              onMessage(content)
            }
            buffer = buffer.substring(singleLineIndex + 1)
          }
          continue
        }

        const eventBlock = buffer.substring(0, newlineIndex)
        buffer = buffer.substring(newlineIndex + 2)

        const content = extractEventContent(eventBlock)
        if (content !== null) {
          onMessage(content)
        }
      }
    } catch (error) {
      onError(new Error(getErrorMessage(error)))
    }
  },
}