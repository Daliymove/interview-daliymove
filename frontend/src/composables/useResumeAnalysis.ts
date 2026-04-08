/**
 * 简历分析轮询组合式函数
 * - 封装简历分析状态轮询逻辑
 * - 提供简洁的 API 给页面使用
 */
import { computed, type Ref } from 'vue'
import { usePolling } from './usePolling'
import type { ResumeDetail, AnalyzeStatus } from '@/types/resume'

export interface UseResumeAnalysisOptions {
  /** 简历 ID */
  resumeId: number | Ref<number>
  /** 获取简历详情的函数 */
  fetchDetail: (id: number) => Promise<ResumeDetail>
  /** 轮询间隔（毫秒），默认 3000ms */
  interval?: number
  /** 立即开始轮询，默认 false */
  immediate?: boolean
  /** 分析完成回调 */
  onCompleted?: (detail: ResumeDetail) => void
  /** 分析失败回调 */
  onFailed?: (detail: ResumeDetail) => void
}

export interface UseResumeAnalysisReturn {
  /** 简历详情 */
  detail: Ref<ResumeDetail | null>
  /** 分析状态 */
  status: Ref<AnalyzeStatus | undefined>
  /** 分析错误信息 */
  analyzeError: Ref<string | undefined>
  /** 是否正在分析 */
  isAnalyzing: Ref<boolean>
  /** 是否已完成 */
  isCompleted: Ref<boolean>
  /** 是否失败 */
  isFailed: Ref<boolean>
  /** 是否正在轮询 */
  isPolling: Ref<boolean>
  /** 错误信息 */
  error: Ref<Error | null>
  /** 开始轮询 */
  startPolling: () => void
  /** 停止轮询 */
  stopPolling: () => void
  /** 重置状态 */
  reset: () => void
}

export function useResumeAnalysis(options: UseResumeAnalysisOptions): UseResumeAnalysisReturn {
  const {
    resumeId,
    fetchDetail,
    interval = 3000,
    immediate = false,
    onCompleted,
    onFailed
  } = options

  const id = typeof resumeId === 'number' 
    ? computed(() => resumeId) 
    : resumeId

  const shouldStop = (detail: ResumeDetail) => {
    const status = detail.analyzeStatus
    return status === 'COMPLETED' || status === 'FAILED'
  }

  const onSuccess = (detail: ResumeDetail) => {
    const status = detail.analyzeStatus
    if (status === 'COMPLETED') {
      onCompleted?.(detail)
    } else if (status === 'FAILED') {
      onFailed?.(detail)
    }
  }

  const polling = usePolling<ResumeDetail>({
    fetchFn: () => fetchDetail(id.value),
    interval,
    shouldStop,
    immediate,
    onSuccess
  })

  const status = computed(() => polling.data.value?.analyzeStatus)
  const analyzeError = computed(() => polling.data.value?.analyzeError)
  const isAnalyzing = computed(() => 
    status.value === 'PENDING' || status.value === 'PROCESSING'
  )
  const isCompleted = computed(() => status.value === 'COMPLETED')
  const isFailed = computed(() => status.value === 'FAILED')

  return {
    detail: polling.data,
    status,
    analyzeError,
    isAnalyzing,
    isCompleted,
    isFailed,
    isPolling: polling.isPolling,
    error: polling.error,
    startPolling: polling.start,
    stopPolling: polling.stop,
    reset: polling.reset
  }
}