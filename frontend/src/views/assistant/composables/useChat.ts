import { ref, onUnmounted } from 'vue'
import { chatApi } from '@/api'
import type { Conversation, Message } from '@/types'

export function useChat() {
  const conversations = ref<Conversation[]>([])
  const currentConversation = ref<Conversation | null>(null)
  const messages = ref<Message[]>([])
  const isLoading = ref(false)
  const isStreaming = ref(false)
  
  let eventSource: EventSource | null = null
  let streamingMessageIndex = -1

  const loadConversations = async () => {
    try {
      isLoading.value = true
      const data = await chatApi.getConversations()
      conversations.value = data || []
    } catch (error) {
      console.error('Failed to load conversations:', error)
      conversations.value = []
    } finally {
      isLoading.value = false
    }
  }

  const loadConversation = async (id: number) => {
    try {
      isLoading.value = true
      const conversation = await chatApi.getConversation(id)
      currentConversation.value = conversation
      messages.value = conversation?.messages || []
    } catch (error) {
      console.error('Failed to load conversation:', error)
      currentConversation.value = null
      messages.value = []
    } finally {
      isLoading.value = false
    }
  }

  const createConversation = async () => {
    const conversation = await chatApi.createConversation()
    conversations.value.unshift(conversation)
    currentConversation.value = conversation
    messages.value = []
    return conversation
  }

  const deleteConversation = async (id: number) => {
    await chatApi.deleteConversation(id)
    conversations.value = conversations.value.filter(c => c.id !== id)
    if (currentConversation.value?.id === id) {
      currentConversation.value = null
      messages.value = []
    }
  }

  const updateTitle = async (id: number, title: string) => {
    await chatApi.updateTitle(id, title)
    const conversation = conversations.value.find(c => c.id === id)
    if (conversation) {
      conversation.title = title
    }
  }

  const sendMessage = (content: string) => {
    if (!content.trim() || isStreaming.value) return

    const conversationId = currentConversation.value?.id
    const token = localStorage.getItem('token')

    messages.value.push({
      id: Date.now(),
      role: 'user',
      content: content.trim(),
      createTime: Date.now()
    })

    messages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: '',
      createTime: Date.now()
    })
    
    streamingMessageIndex = messages.value.length - 1
    isStreaming.value = true

    const params = new URLSearchParams()
    if (conversationId) {
      params.append('conversationId', conversationId.toString())
    }
    params.append('message', content.trim())
    if (token) {
      params.append('Authorization', token)
    }

    const url = `/api/chat/stream?${params.toString()}`
    console.log('SSE connecting to:', url)

    eventSource = new EventSource(url)

    eventSource.onopen = () => {
      console.log('SSE connection opened')
    }

    eventSource.addEventListener('message', (e: MessageEvent) => {
      console.log('SSE message:', e.data)
      try {
        const data = JSON.parse(e.data)
        if (data.content && streamingMessageIndex >= 0) {
          messages.value[streamingMessageIndex].content += data.content
        }
      } catch (err) {
        console.error('Parse error:', err)
        if (streamingMessageIndex >= 0) {
          messages.value[streamingMessageIndex].content += e.data
        }
      }
    })

    eventSource.addEventListener('done', (e: MessageEvent) => {
      console.log('SSE done:', e.data)
      try {
        const data = JSON.parse(e.data)
        if (data.conversationId) {
          if (!currentConversation.value) {
            loadConversations()
          }
          if (!currentConversation.value || currentConversation.value.id !== data.conversationId) {
            const conv = conversations.value.find(c => c.id === data.conversationId)
            if (conv) {
              currentConversation.value = conv
              loadConversations()
            }
          }
        }
      } catch {
        // ignore
      }
      stopStreaming()
    })

    eventSource.onerror = (e: Event) => {
      console.error('SSE error:', e)
      stopStreaming()
    }
  }

  const stopStreaming = () => {
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
    isStreaming.value = false
    streamingMessageIndex = -1
  }

  onUnmounted(() => {
    stopStreaming()
  })

  return {
    conversations,
    currentConversation,
    messages,
    isLoading,
    isStreaming,
    loadConversations,
    loadConversation,
    createConversation,
    deleteConversation,
    updateTitle,
    sendMessage,
    stopStreaming
  }
}