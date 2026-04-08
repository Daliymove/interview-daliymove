/**
 * 轮询逻辑组合式函数
 * - 定时轮询 API
 * - 支持配置轮询间隔
 * - 支持自定义停止条件
 * - 自动清理定时器
 */
import { ref, onUnmounted, type Ref } from 'vue'

export interface UsePollingOptions<T> {
  /** 获取数据的函数 */
  fetchFn: () => Promise<T>
  /** 轮询间隔（毫秒），默认 2000ms */
  interval?: number
  /** 停止轮询的条件函数 */
  shouldStop?: (data: T) => boolean
  /** 立即开始轮询，默认 false */
  immediate?: boolean
  /** 错误回调 */
  onError?: (error: Error) => void
  /** 成功回调 */
  onSuccess?: (data: T) => void
}

export interface UsePollingReturn<T> {
  /** 当前数据 */
  data: Ref<T | null>
  /** 是否正在轮询 */
  isPolling: Ref<boolean>
  /** 错误信息 */
  error: Ref<Error | null>
  /** 开始轮询 */
  start: () => void
  /** 停止轮询 */
  stop: () => void
  /** 重置状态 */
  reset: () => void
}

export function usePolling<T>(options: UsePollingOptions<T>): UsePollingReturn<T> {
  const {
    fetchFn,
    interval = 2000,
    shouldStop,
    immediate = false,
    onError,
    onSuccess
  } = options

  const data = ref<T | null>(null) as Ref<T | null>
  const isPolling = ref(false)
  const error = ref<Error | null>(null)
  
  let timerId: number | null = null

  const tick = async () => {
    if (!isPolling.value) return

    try {
      const result = await fetchFn()
      data.value = result
      error.value = null
      onSuccess?.(result)

      if (shouldStop?.(result)) {
        stop()
        return
      }

      timerId = window.setTimeout(tick, interval)
    } catch (err) {
      const errorObj = err instanceof Error ? err : new Error(String(err))
      error.value = errorObj
      onError?.(errorObj)
      
      timerId = window.setTimeout(tick, interval)
    }
  }

  const start = () => {
    if (isPolling.value) return
    
    isPolling.value = true
    error.value = null
    tick()
  }

  const stop = () => {
    isPolling.value = false
    if (timerId !== null) {
      window.clearTimeout(timerId)
      timerId = null
    }
  }

  const reset = () => {
    stop()
    data.value = null
    error.value = null
  }

  onUnmounted(() => {
    stop()
  })

  if (immediate) {
    start()
  }

  return {
    data,
    isPolling,
    error,
    start,
    stop,
    reset
  }
}