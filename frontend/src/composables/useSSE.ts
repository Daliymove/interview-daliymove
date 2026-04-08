/**
 * SSE 流组合式函数
 * - 建立 SSE 连接
 * - 处理消息事件
 * - 支持错误恢复
 * - 自动清理连接
 */
import { ref, onUnmounted, type Ref } from 'vue'

export interface UseSSEOptions {
  /** SSE URL */
  url: string
  /** 是否立即连接，默认 false */
  immediate?: boolean
  /** 消息回调 */
  onMessage?: (event: MessageEvent) => void
  /** 自定义事件回调映射 */
  eventHandlers?: Record<string, (event: MessageEvent) => void>
  /** 错误回调 */
  onError?: (error: Event) => void
  /** 连接打开回调 */
  onOpen?: () => void
  /** 重连次数，默认 3 */
  maxRetries?: number
  /** 重连延迟（毫秒），默认 1000 */
  retryDelay?: number
}

export interface UseSSEReturn {
  /** 是否正在连接 */
  isConnected: Ref<boolean>
  /** 错误信息 */
  error: Ref<Event | null>
  /** 开始连接 */
  connect: () => void
  /** 断开连接 */
  disconnect: () => void
  /** 重连次数 */
  retryCount: Ref<number>
}

export function useSSE(options: UseSSEOptions): UseSSEReturn {
  const {
    url,
    immediate = false,
    onMessage,
    eventHandlers = {},
    onError,
    onOpen,
    maxRetries = 3,
    retryDelay = 1000
  } = options

  const isConnected = ref(false)
  const error = ref<Event | null>(null)
  const retryCount = ref(0)
  
  let eventSource: EventSource | null = null

  const connect = () => {
    if (eventSource) {
      disconnect()
    }

    eventSource = new EventSource(url)

    eventSource.onopen = () => {
      isConnected.value = true
      error.value = null
      retryCount.value = 0
      onOpen?.()
    }

    eventSource.onerror = (err: Event) => {
      error.value = err
      isConnected.value = false
      onError?.(err)

      if (retryCount.value < maxRetries) {
        retryCount.value++
        setTimeout(() => {
          connect()
        }, retryDelay)
      } else {
        disconnect()
      }
    }

    eventSource.onmessage = (event: MessageEvent) => {
      onMessage?.(event)
    }

    for (const [eventName, handler] of Object.entries(eventHandlers)) {
      eventSource.addEventListener(eventName, handler)
    }
  }

  const disconnect = () => {
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
    isConnected.value = false
  }

  onUnmounted(() => {
    disconnect()
  })

  if (immediate) {
    connect()
  }

  return {
    isConnected,
    error,
    connect,
    disconnect,
    retryCount
  }
}