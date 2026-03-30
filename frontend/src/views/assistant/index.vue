<template>
  <div class="h-[calc(100vh-56px-40px)] flex bg-white rounded-lg overflow-hidden">
    <div class="w-64 border-r border-gray-100 flex flex-col">
      <div class="p-3 border-b border-gray-100">
        <n-button type="primary" block @click="handleNewChat">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新建对话
        </n-button>
      </div>
      
      <div class="flex-1 overflow-y-auto p-2">
        <div v-if="conversations.length === 0" class="text-center text-gray-400 py-8">
          <span class="i-carbon-chat text-4xl block mb-2"></span>
          <span class="text-sm">暂无对话记录</span>
        </div>
        
        <div
          v-for="conv in conversations"
          :key="conv.id"
          :class="[
            'p-3 rounded-lg cursor-pointer mb-1 group transition-colors',
            currentConversation?.id === conv.id ? 'bg-primary/10 text-primary' : 'hover:bg-gray-50'
          ]"
          @click="selectConversation(conv.id)"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2 flex-1 min-w-0">
              <span class="i-carbon-chat text-lg flex-shrink-0"></span>
              <span class="truncate text-sm">{{ conv.title }}</span>
            </div>
            <n-dropdown
              trigger="click"
              :options="getConversationOptions()"
              @select="(key: string) => handleConversationAction(key, conv.id)"
            >
              <span
                class="i-carbon-overflow-menu-horizontal opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer p-1"
                @click.stop
              ></span>
            </n-dropdown>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col">
      <div v-if="!currentConversation" class="flex-1 flex-center">
        <div class="text-center">
          <div class="w-20 h-20 rounded-full bg-primary/10 flex-center mx-auto mb-4">
            <span class="i-carbon-chat-bot text-4xl text-primary"></span>
          </div>
          <h2 class="text-xl font-medium text-gray-800 mb-2">AI 助手</h2>
          <p class="text-gray-400 text-sm mb-6">基于大语言模型的智能对话助手</p>
          <n-button type="primary" @click="handleNewChat">
            开始对话
          </n-button>
        </div>
      </div>

      <template v-else>
        <div class="h-16 px-6 flex-center border-b border-gray-100 bg-gradient-to-r from-primary/5 to-transparent">
          <h1 class="text-lg font-bold text-gray-800">{{ currentConversation.title }}</h1>
        </div>

        <div ref="messageContainerRef" class="flex-1 overflow-y-auto p-4">
          <div class="max-w-4xl mx-auto space-y-4">
            <MessageItem
              v-for="(msg, index) in messages"
              :key="msg.id || index"
              :message="msg"
              :is-streaming="isStreaming && index === messages.length - 1 && msg.role === 'assistant'"
            />
          </div>
        </div>

        <div class="p-4 border-t border-gray-100">
          <div class="max-w-4xl mx-auto flex gap-2">
            <n-input
              v-model:value="inputMessage"
              type="textarea"
              placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
              :autosize="{ minRows: 1, maxRows: 4 }"
              :disabled="isStreaming"
              @keydown.enter.exact="handleSend"
            />
            <n-button
              v-if="!isStreaming"
              type="primary"
              :disabled="!inputMessage.trim()"
              @click="handleSend"
            >
              <template #icon>
                <span class="i-carbon-send"></span>
              </template>
            </n-button>
            <n-button v-else type="error" @click="stopStreaming">
              <template #icon>
                <span class="i-carbon-stop-filled"></span>
              </template>
            </n-button>
          </div>
        </div>
      </template>
    </div>

    <n-modal
      v-model:show="editTitleVisible"
      preset="dialog"
      title="编辑标题"
      positive-text="确定"
      negative-text="取消"
      @positive-click="handleSaveTitle"
    >
      <n-input v-model:value="editingTitle" placeholder="请输入对话标题" />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { useChat } from './composables/useChat'
import MessageItem from './components/MessageItem.vue'

const {
  conversations,
  currentConversation,
  messages,
  isStreaming,
  loadConversations,
  loadConversation,
  createConversation,
  deleteConversation,
  updateTitle,
  sendMessage,
  stopStreaming
} = useChat()

const inputMessage = ref('')
const messageContainerRef = ref<HTMLElement | null>(null)
const editTitleVisible = ref(false)
const editingTitle = ref('')
const editingConversationId = ref<number | null>(null)

onMounted(() => {
  loadConversations()
})

watch(messages, () => {
  nextTick(() => {
    if (messageContainerRef.value) {
      messageContainerRef.value.scrollTop = messageContainerRef.value.scrollHeight
    }
  })
}, { deep: true })

const handleNewChat = async () => {
  await createConversation()
}

const selectConversation = (id: number) => {
  loadConversation(id)
}

const handleSend = () => {
  if (!inputMessage.value.trim() || isStreaming.value) return
  const content = inputMessage.value
  inputMessage.value = ''
  sendMessage(content)
}

const getConversationOptions = () => [
  { label: '重命名', key: 'rename' },
  { label: '删除', key: 'delete' }
]

const handleConversationAction = (key: string, id: number) => {
  if (key === 'rename') {
    const conv = conversations.value.find(c => c.id === id)
    if (conv) {
      editingConversationId.value = id
      editingTitle.value = conv.title
      editTitleVisible.value = true
    }
  } else if (key === 'delete') {
    window.$dialog.warning({
      title: '确认删除',
      content: '确定要删除这个对话吗？',
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        deleteConversation(id)
      }
    })
  }
}

const handleSaveTitle = async () => {
  if (editingConversationId.value && editingTitle.value.trim()) {
    await updateTitle(editingConversationId.value, editingTitle.value.trim())
  }
}
</script>