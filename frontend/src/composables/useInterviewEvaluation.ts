/**
 * 面试评估轮询组合式函数
 * - 封装面试评估状态轮询逻辑
 * - 提供简洁的 API 给页面使用
 */
import { computed, type Ref } from 'vue'
import { usePolling } from './usePolling'
import type { InterviewDetail, EvaluateStatus } from '@/types/resume'

export interface UseInterviewEvaluationOptions {
  /** 面试会话 ID */
  sessionId: string | Ref<string>
  /** 获取面试详情的函数 */
  fetchDetail: (sessionId: string) => Promise<InterviewDetail>
  /** 轮询间隔（毫秒），默认 3000ms */
  interval?: number
  /** 立即开始轮询，默认 false */
  immediate?: boolean
  /** 评估完成回调 */
  onCompleted?: (detail: InterviewDetail) => void
  /** 评估失败回调 */
  onFailed?: (detail: InterviewDetail) => void
}

export interface UseInterviewEvaluationReturn {
  /** 面试详情 */
  detail: Ref<InterviewDetail | null>
  /** 评估状态 */
  status: Ref<EvaluateStatus | undefined>
  /** 评估错误信息 */
  evaluateError: Ref<string | undefined>
  /** 是否正在评估 */
  isEvaluating: Ref<boolean>
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

export function useInterviewEvaluation(options: UseInterviewEvaluationOptions): UseInterviewEvaluationReturn {
  const {
    sessionId,
    fetchDetail,
    interval = 3000,
    immediate = false,
    onCompleted,
    onFailed
  } = options

  const id = typeof sessionId === 'string' 
    ? computed(() => sessionId) 
    : sessionId

  const shouldStop = (detail: InterviewDetail) => {
    const status = detail.evaluateStatus
    return status === 'COMPLETED' || status === 'FAILED'
  }

  const onSuccess = (detail: InterviewDetail) => {
    const status = detail.evaluateStatus
    if (status === 'COMPLETED') {
      onCompleted?.(detail)
    } else if (status === 'FAILED') {
      onFailed?.(detail)
    }
  }

  const polling = usePolling<InterviewDetail>({
    fetchFn: () => fetchDetail(id.value),
    interval,
    shouldStop,
    immediate,
    onSuccess
  })

  const status = computed(() => polling.data.value?.evaluateStatus)
  const evaluateError = computed(() => polling.data.value?.evaluateError)
  const isEvaluating = computed(() => 
    status.value === 'PENDING' || status.value === 'PROCESSING'
  )
  const isCompleted = computed(() => status.value === 'COMPLETED')
  const isFailed = computed(() => status.value === 'FAILED')

  return {
    detail: polling.data,
    status,
    evaluateError,
    isEvaluating,
    isCompleted,
    isFailed,
    isPolling: polling.isPolling,
    error: polling.error,
    startPolling: polling.start,
    stopPolling: polling.stop,
    reset: polling.reset
  }
}