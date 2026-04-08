/**
 * 知识库相关类型定义
 * - 知识库项
 * - 统计信息
 * - 查询请求/响应
 */

/** 向量化状态 */
export type VectorStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

/** 知识库项 */
export interface KnowledgeBaseItem {
  id: number
  name: string
  category: string | null
  originalFilename: string
  fileSize: number
  contentType: string
  uploadedAt: string
  lastAccessedAt: string
  accessCount: number
  questionCount: number
  vectorStatus: VectorStatus
  vectorError: string | null
  chunkCount: number
}

/** 知识库统计 */
export interface KnowledgeBaseStats {
  totalCount: number
  totalQuestionCount: number
  totalAccessCount: number
  completedCount: number
  processingCount: number
}

/** 排序选项 */
export type SortOption = 'time' | 'size' | 'access' | 'question'

/** 上传知识库响应 */
export interface UploadKnowledgeBaseResponse {
  knowledgeBase: {
    id: number
    name: string
    category: string
    fileSize: number
    contentLength: number
  }
  storage: {
    fileKey: string
    fileUrl: string
  }
  duplicate: boolean
}

/** 查询请求 */
export interface QueryRequest {
  knowledgeBaseIds: number[]
  question: string
}

/** 查询响应 */
export interface QueryResponse {
  answer: string
  knowledgeBaseId: number
  knowledgeBaseName: string
}

/** RAG 聊天会话 */
export interface RagChatSession {
  id: number
  title: string
  knowledgeBaseIds: number[]
  createdAt: string
}

/** RAG 聊天会话列表项 */
export interface RagChatSessionListItem {
  id: number
  title: string
  messageCount: number
  knowledgeBaseNames: string[]
  updatedAt: string
  isPinned: boolean
}

/** RAG 聊天消息 */
export interface RagChatMessage {
  id: number
  type: 'user' | 'assistant'
  content: string
  createdAt: string
}

/** RAG 聊天知识库项 */
export interface RagChatKnowledgeBaseItem {
  id: number
  name: string
  originalFilename: string
  fileSize: number
  contentType: string
  uploadedAt: string
  lastAccessedAt: string
  accessCount: number
  questionCount: number
}

/** RAG 聊天会话详情 */
export interface RagChatSessionDetail {
  id: number
  title: string
  knowledgeBases: RagChatKnowledgeBaseItem[]
  messages: RagChatMessage[]
  createdAt: string
  updatedAt: string
}