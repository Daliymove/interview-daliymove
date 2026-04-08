<template>
  <div class="flex flex-col h-full">
    <div class="flex flex-wrap items-center justify-between mb-6 gap-4">
      <div>
        <h1 class="text-2xl font-bold text-slate-900 mb-1">问答助手</h1>
        <p class="text-slate-500 text-sm">选择知识库，向 AI 提问</p>
      </div>
      <div class="flex flex-wrap gap-2">
        <n-button @click="handleUpload">
          上传知识库
        </n-button>
        <n-button @click="handleBack">
          返回
        </n-button>
      </div>
    </div>

    <div class="flex-1 flex flex-col lg:flex-row gap-4 min-h-0">
      <div class="lg:w-64 flex-shrink-0 order-2 lg:order-1">
        <n-card class="h-full">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-base font-semibold text-slate-800">对话历史</h2>
            <n-button
              quaternary
              circle
              size="small"
              :disabled="selectedKbIds.size === 0"
              @click="handleNewSession"
            >
              <template #icon>
                <span class="i-carbon-add"></span>
              </template>
            </n-button>
          </div>

          <n-scrollbar class="flex-1">
            <template v-if="loadingSessions">
              <div class="text-center py-6">
                <n-spin size="small" />
              </div>
            </template>
            <template v-else-if="sessions.length === 0">
              <div class="text-center py-6 text-slate-400 text-sm">
                暂无对话历史
              </div>
            </template>
            <template v-else>
              <div class="space-y-2">
                <div
                  v-for="session in sessions"
                  :key="session.id"
                  :class="[
                    'p-3 rounded-lg cursor-pointer transition-all group',
                    currentSessionId === session.id
                      ? 'bg-primary/10 border border-primary'
                      : 'bg-gray-50 hover:bg-gray-100 border border-transparent',
                    session.isPinned ? 'border-l-4 border-l-primary' : ''
                  ]"
                  @click="handleLoadSession(session.id)"
                >
                  <div class="flex items-start justify-between gap-2">
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-1.5">
                        <span
                          v-if="session.isPinned"
                          class="i-carbon-pin-filled text-primary text-3.5 flex-shrink-0"
                        ></span>
                        <p class="font-medium text-slate-800 text-sm truncate">{{ session.title }}</p>
                      </div>
                      <p class="text-xs text-slate-500 mt-1">
                        {{ session.messageCount }} 条消息 · {{ formatTimeAgo(session.updatedAt) }}
                      </p>
                    </div>
                    <div class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all">
                      <n-button
                        quaternary
                        size="tiny"
                        :type="session.isPinned ? 'primary' : 'default'"
                        @click.stop="handleTogglePin(session.id)"
                      >
                        <template #icon>
                          <span :class="session.isPinned ? 'i-carbon-pin-filled' : 'i-carbon-pin'"></span>
                        </template>
                      </n-button>
                      <n-button
                        quaternary
                        size="tiny"
                        @click.stop="handleEditSessionTitle(session.id, session.title)"
                      >
                        <template #icon>
                          <span class="i-carbon-edit"></span>
                        </template>
                      </n-button>
                      <n-button
                        quaternary
                        size="tiny"
                        type="error"
                        @click.stop="handleShowDeleteConfirm(session)"
                      >
                        <template #icon>
                          <span class="i-carbon-trash-can"></span>
                        </template>
                      </n-button>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </n-scrollbar>
        </n-card>
      </div>

      <div class="flex-1 min-w-0 order-1 lg:order-2">
        <n-card class="h-full flex flex-col">
          <template v-if="selectedKbIds.size > 0">
            <div class="p-4 border-b border-gray-100">
              <h2 class="text-base font-semibold text-slate-800">
                {{ currentSessionTitle || (selectedKbIds.size === 1
                  ? knowledgeBases.find(kb => kb.id === Array.from(selectedKbIds)[0])?.name || '新对话'
                  : `${selectedKbIds.size} 个知识库 - 新对话`) }}
              </h2>
              <div class="flex flex-wrap gap-1.5 mt-2">
                <n-tag
                  v-for="kbId in Array.from(selectedKbIds)"
                  :key="kbId"
                  type="primary"
                  size="small"
                  bordered
                >
                  {{ knowledgeBases.find(k => k.id === kbId)?.name }}
                </n-tag>
              </div>
            </div>

            <div class="flex-1 min-h-0 relative">
              <template v-if="messages.length === 0">
                <div class="absolute inset-0 flex flex-col items-center justify-center text-slate-400">
                  <span class="i-carbon-chat text-12xl opacity-50 mb-3"></span>
                  <p class="text-sm">开始提问吧！</p>
                </div>
              </template>
              <template v-else>
                <n-scrollbar ref="scrollbarRef" class="h-full">
                  <div class="p-4 space-y-4">
                    <div
                      v-for="(msg, index) in messages"
                      :key="msg.id || index"
                      :class="['flex gap-3', msg.type === 'user' ? 'flex-row-reverse' : '']"
                    >
                      <div
                        :class="[
                          'w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center',
                          msg.type === 'user' ? 'bg-primary' : 'bg-gray-100'
                        ]"
                      >
                        <span
                          :class="[
                            'text-lg',
                            msg.type === 'user' ? 'i-carbon-user text-white' : 'i-carbon-bot text-gray-500'
                          ]"
                        ></span>
                      </div>

                      <div
                        :class="[
                          'max-w-[85%] rounded-xl px-4 py-3',
                          msg.type === 'user' ? 'bg-primary text-white' : 'bg-gray-50 text-gray-800'
                        ]"
                      >
                        <template v-if="msg.type === 'user'">
                          <p class="whitespace-pre-wrap leading-relaxed text-sm">{{ msg.content }}</p>
                        </template>
                        <template v-else>
                          <div class="prose prose-sm max-w-none" v-html="renderMarkdown(msg.content)"></div>
                          <template v-if="loading && index === messages.length - 1">
                            <span class="inline-block w-0.5 h-5 bg-primary animate-pulse ml-1"></span>
                          </template>
                        </template>
                      </div>
                    </div>
                  </div>
                </n-scrollbar>
              </template>
            </div>

            <div class="p-4 border-t border-gray-100">
              <div class="flex gap-3">
                <n-input
                  v-model:value="question"
                  placeholder="输入您的问题..."
                  :disabled="loading"
                  @keyup.enter="handleSubmitQuestion"
                />
                <n-button
                  type="primary"
                  :disabled="!question.trim() || selectedKbIds.size === 0 || loading"
                  :loading="loading"
                  @click="handleSubmitQuestion"
                >
                  发送
                                  </n-button>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="flex-1 flex items-center justify-center text-slate-400">
              <div class="text-center">
                <span class="i-carbon-bot text-12xl opacity-50 mb-3"></span>
                <p class="text-sm">请先在右侧选择知识库</p>
              </div>
            </div>
          </template>
        </n-card>
      </div>

      <template v-if="rightPanelOpen">
        <div class="lg:w-[280px] w-full flex-shrink-0 order-3">
          <n-card class="h-full flex flex-col">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-base font-semibold text-slate-800">选择知识库</h2>
              <n-button quaternary size="small" @click="rightPanelOpen = false">
                <template #icon>
                  <span class="i-carbon-chevron-left"></span>
                </template>
              </n-button>
            </div>

            <div class="flex gap-2 mb-3">
              <n-input
                v-model:value="searchKeyword"
                placeholder="搜索..."
                size="small"
                @keyup.enter="handleSearch"
              />
              <n-button size="small" type="primary" @click="handleSearch">
                搜索
              </n-button>
            </div>

            <div class="mb-3">
              <n-select
                v-model:value="sortBy"
                :options="sortOptions"
                size="small"
                placeholder="排序方式"
                @update:value="handleSortChange"
              />
            </div>

            <n-scrollbar class="flex-1">
              <template v-if="loadingList">
                <div class="text-center py-6">
                  <n-spin size="small" />
                </div>
              </template>
              <template v-else-if="knowledgeBases.length === 0">
                <div class="text-center py-6 text-slate-500">
                  <p class="mb-2 text-sm">{{ searchKeyword ? '未找到' : '暂无知识库' }}</p>
                  <template v-if="!searchKeyword">
                    <n-button text type="primary" size="small" @click="handleUpload">
                      立即上传
                    </n-button>
                  </template>
                </div>
              </template>
              <template v-else>
                <div class="space-y-2">
                  <div
                    v-for="group in groupedKnowledgeBases"
                    :key="group.name"
                    class="border border-gray-100 rounded-lg overflow-hidden"
                  >
                    <div
                      class="flex items-center justify-between px-3 py-2 bg-gray-50 hover:bg-gray-100 cursor-pointer transition-colors"
                      @click="toggleCategory(group.name)"
                    >
                      <div class="flex items-center gap-2">
                        <span
                          :class="[
                            'i-carbon-chevron-right text-slate-400 transition-transform',
                            group.isExpanded ? 'rotate-90' : ''
                          ]"
                        ></span>
                        <span class="font-medium text-slate-700 text-sm">{{ group.name }}</span>
                      </div>
                      <span class="text-xs text-slate-400">{{ group.items.length }}</span>
                    </div>

                    <template v-if="group.isExpanded">
                      <div class="p-2 space-y-1">
                        <div
                          v-for="kb in group.items"
                          :key="kb.id"
                          :class="[
                            'p-2 rounded-lg cursor-pointer transition-all',
                            selectedKbIds.has(kb.id)
                              ? 'bg-primary/10 border border-primary'
                              : 'bg-white hover:bg-gray-50 border border-transparent'
                          ]"
                          @click="handleToggleKb(kb.id)"
                        >
                          <div class="flex items-center gap-2">
                            <n-checkbox
                              :checked="selectedKbIds.has(kb.id)"
                              @update:checked="handleToggleKb(kb.id)"
                              @click.stop
                            />
                            <span class="font-medium text-slate-800 text-xs truncate flex-1">
                              {{ kb.name }}
                            </span>
                          </div>
                          <p class="text-xs text-slate-400 mt-0.5 ml-5">
                            {{ formatFileSize(kb.fileSize) }}
                          </p>
                        </div>
                      </div>
                    </template>
                  </div>
                </div>
              </template>
            </n-scrollbar>
          </n-card>
        </div>
      </template>

      <template v-if="!rightPanelOpen">
        <n-button
          class="lg:flex-shrink-0 w-full lg:w-10 order-3"
          @click="rightPanelOpen = true"
          title="展开知识库面板"
        >
          <template #icon>
            <span class="i-carbon-chevron-right"></span>
          </template>
        </n-button>
      </template>
    </div>

    <DeleteConfirmDialog
      :open="showDeleteDialog"
      @update:open="showDeleteDialog = $event"
      :item="sessionDeleteConfirm ? { id: 0, title: sessionDeleteConfirm.title } : null"
      item-type="对话"
      :on-confirm="handleDeleteSession"
      :on-cancel="handleCancelDeleteSession"
    />

    <n-modal
      v-model:show="editTitleVisible"
      preset="dialog"
      title="编辑标题"
      positive-text="保存"
      negative-text="取消"
      :disabled="!newSessionTitle.trim()"
      @positive-click="handleSaveSessionTitle"
    >
      <n-input
        v-model:value="newSessionTitle"
        placeholder="请输入新标题"
        autofocus
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
/**
 * RAG 问答助手页面
 * - 知识库选择
 * - 对话历史管理
 * - SSE 流式聊天
 * - Markdown 渲染
 */
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { knowledgeBaseApi } from '@/api/knowledgebase'
import { ragChatApi } from '@/api/ragChat'
import DeleteConfirmDialog from '@/components/common/DeleteConfirmDialog.vue'
import type { KnowledgeBaseItem, RagChatSessionListItem, SortOption } from '@/types/knowledge'
import { formatDateOnly } from '@/utils/date'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const router = useRouter()

const handleUpload = () => {
  router.push('/knowledge/upload')
}

const handleBack = () => {
  router.push('/knowledge')
}

const knowledgeBases = ref<KnowledgeBaseItem[]>([])
const selectedKbIds = ref<Set<number>>(new Set())
const loadingList = ref(true)
const searchKeyword = ref('')
const sortBy = ref<SortOption>('time')
const expandedCategories = ref<Set<string>>(new Set(['未分类']))
const rightPanelOpen = ref(true)

const sessions = ref<RagChatSessionListItem[]>([])
const currentSessionId = ref<number | null>(null)
const currentSessionTitle = ref<string>('')
const loadingSessions = ref(false)
const sessionDeleteConfirm = ref<{ id: number; title: string } | null>(null)
const showDeleteDialog = ref(false)
const editTitleVisible = ref(false)
const editingSessionId = ref<number | null>(null)
const newSessionTitle = ref('')

const messages = ref<Array<{ id?: number; type: 'user' | 'assistant'; content: string; timestamp: Date }>>([])
const question = ref('')
const loading = ref(false)
const scrollbarRef = ref()

const sortOptions = [
  { label: '时间排序', value: 'time' },
  { label: '大小排序', value: 'size' },
  { label: '访问排序', value: 'access' },
  { label: '提问排序', value: 'question' }
]

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
  highlight: (str: string, lang: string): string => {
    if (!str) return '<pre class="hljs"><code></code></pre>'
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch {
        // ignore
      }
    }
    return `<pre class="hljs"><code>${escapeHtml(str)}</code></pre>`
  }
})

const escapeHtml = (s: string): string => {
  if (!s) return ''
  return s.replace(/[&<>"']/g, (c) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c] || c))
}

const renderMarkdown = (text: string): string => {
  if (!text) return ''
  const formatted = formatMarkdown(text)
  try {
    return md.render(formatted)
  } catch (e) {
    console.error('Markdown render error:', e)
    return escapeHtml(text)
  }
}

const formatMarkdown = (text: string): string => {
  if (!text) return ''
  return text
    .replace(/\\n/g, '\n')
    .replace(/^(#{1,6})([^\s#\n])/gm, '$1 $2')
    .replace(/^(\s*)(\d+)\.([^\s\n])/gm, '$1$2. $3')
    .replace(/^(\s*[-*])([^\s\n-])/gm, '$1 $2')
    .replace(/\n{3,}/g, '\n\n')
}

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const formatTimeAgo = (dateStr: string): string => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  return formatDateOnly(dateStr)
}

interface CategoryGroup {
  name: string
  items: KnowledgeBaseItem[]
  isExpanded: boolean
}

const groupedKnowledgeBases = computed<CategoryGroup[]>(() => {
  const groups: Map<string, KnowledgeBaseItem[]> = new Map()

  knowledgeBases.value.forEach(kb => {
    const category = kb.category || '未分类'
    if (!groups.has(category)) {
      groups.set(category, [])
    }
    groups.get(category)!.push(kb)
  })

  const result: CategoryGroup[] = []
  const sortedCategories = Array.from(groups.keys()).sort((a, b) => {
    if (a === '未分类') return 1
    if (b === '未分类') return -1
    return a.localeCompare(b)
  })

  sortedCategories.forEach(name => {
    result.push({
      name,
      items: groups.get(name)!,
      isExpanded: expandedCategories.value.has(name),
    })
  })

  return result
})

const loadKnowledgeBases = async () => {
  loadingList.value = true
  try {
    const list = await knowledgeBaseApi.getAllKnowledgeBases(sortBy.value, 'COMPLETED')
    knowledgeBases.value = list
  } catch (err) {
    console.error('加载知识库列表失败', err)
  } finally {
    loadingList.value = false
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadKnowledgeBases()
    return
  }
  loadingList.value = true
  try {
    const list = await knowledgeBaseApi.search(searchKeyword.value.trim())
    knowledgeBases.value = list.filter(kb => kb.vectorStatus === 'COMPLETED')
  } catch (err) {
    console.error('搜索知识库失败', err)
  } finally {
    loadingList.value = false
  }
}

const toggleCategory = (category: string) => {
  if (expandedCategories.value.has(category)) {
    expandedCategories.value.delete(category)
  } else {
    expandedCategories.value.add(category)
  }
}

const handleSortChange = () => {
  searchKeyword.value = ''
  loadKnowledgeBases()
}

const loadSessions = async () => {
  loadingSessions.value = true
  try {
    const list = await ragChatApi.listSessions()
    sessions.value = list
  } catch (err) {
    console.error('加载会话列表失败', err)
  } finally {
    loadingSessions.value = false
  }
}

const handleToggleKb = (kbId: number) => {
  if (selectedKbIds.value.has(kbId)) {
    selectedKbIds.value.delete(kbId)
  } else {
    selectedKbIds.value.add(kbId)
  }
  if (currentSessionId.value) {
    currentSessionId.value = null
    currentSessionTitle.value = ''
    messages.value = []
  }
}

const handleNewSession = () => {
  currentSessionId.value = null
  currentSessionTitle.value = ''
  messages.value = []
}

const handleLoadSession = async (sessionId: number) => {
  try {
    const detail = await ragChatApi.getSessionDetail(sessionId)
    currentSessionId.value = detail.id
    currentSessionTitle.value = detail.title
    selectedKbIds.value = new Set(detail.knowledgeBases.map(kb => kb.id))
    messages.value = detail.messages.map(m => ({
      id: m.id,
      type: m.type,
      content: m.content,
      timestamp: new Date(m.createdAt),
    }))
  } catch (err) {
    console.error('加载会话失败', err)
    window.$message.error('加载会话失败')
  }
}

const handleDeleteSession = async () => {
  if (!sessionDeleteConfirm.value) return
  try {
    await ragChatApi.deleteSession(sessionDeleteConfirm.value.id)
    await loadSessions()
    if (currentSessionId.value === sessionDeleteConfirm.value.id) {
      handleNewSession()
    }
    showDeleteDialog.value = false
    sessionDeleteConfirm.value = null
    window.$message.success('删除成功')
  } catch (err) {
    console.error('删除会话失败', err)
    window.$message.error('删除失败')
  }
}

const handleCancelDeleteSession = () => {
  showDeleteDialog.value = false
  sessionDeleteConfirm.value = null
}

const handleShowDeleteConfirm = (session: { id: number; title: string }) => {
  sessionDeleteConfirm.value = session
  showDeleteDialog.value = true
}

const handleEditSessionTitle = (sessionId: number, currentTitle: string) => {
  editingSessionId.value = sessionId
  newSessionTitle.value = currentTitle
  editTitleVisible.value = true
}

const handleSaveSessionTitle = async () => {
  if (!editingSessionId.value || !newSessionTitle.value.trim()) return
  try {
    await ragChatApi.updateSessionTitle(editingSessionId.value, newSessionTitle.value.trim())
    await loadSessions()
    if (currentSessionId.value === editingSessionId.value) {
      currentSessionTitle.value = newSessionTitle.value.trim()
    }
    editTitleVisible.value = false
    editingSessionId.value = null
    newSessionTitle.value = ''
    window.$message.success('标题更新成功')
  } catch (err) {
    console.error('更新会话标题失败', err)
    window.$message.error('更新失败')
  }
  return true
}

const handleTogglePin = async (sessionId: number) => {
  try {
    await ragChatApi.togglePin(sessionId)
    await loadSessions()
  } catch (err) {
    console.error('切换置顶状态失败', err)
    window.$message.error('操作失败')
  }
}

let rafId: number | null = null

const handleSubmitQuestion = async () => {
  if (!question.value.trim() || selectedKbIds.value.size === 0 || loading.value) return

  const userQuestion = question.value.trim()
  question.value = ''
  loading.value = true

  let sessionId = currentSessionId.value
  if (!sessionId) {
    try {
      const session = await ragChatApi.createSession(Array.from(selectedKbIds.value))
      sessionId = session.id
      currentSessionId.value = sessionId
      currentSessionTitle.value = session.title
    } catch (err) {
      console.error('创建会话失败', err)
      window.$message.error('创建会话失败')
      loading.value = false
      return
    }
  }

  messages.value.push({
    type: 'user',
    content: userQuestion,
    timestamp: new Date(),
  })

  messages.value.push({
    type: 'assistant',
    content: '',
    timestamp: new Date(),
  })

  let fullContent = ''

  const updateAssistantMessage = (content: string) => {
    const lastIndex = messages.value.length - 1
    if (lastIndex >= 0 && messages.value[lastIndex].type === 'assistant') {
      messages.value[lastIndex].content = content
    }
  }

  try {
    await ragChatApi.sendMessageStream(
      sessionId,
      userQuestion,
      (chunk: string) => {
        fullContent += chunk
        if (rafId) {
          cancelAnimationFrame(rafId)
        }
        rafId = requestAnimationFrame(() => {
          updateAssistantMessage(fullContent)
        })
      },
      () => {
        loading.value = false
        loadSessions()
      },
      (error: Error) => {
        console.error('流式查询失败:', error)
        updateAssistantMessage(fullContent || error.message || '回答失败，请重试')
        loading.value = false
      }
    )
  } catch (err) {
    console.error('发起流式查询失败:', err)
    updateAssistantMessage(err instanceof Error ? err.message : '回答失败，请重试')
    loading.value = false
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (scrollbarRef.value) {
      scrollbarRef.value.scrollTo({ top: scrollbarRef.value.$el.scrollHeight, behavior: 'smooth' })
    }
  })
}

watch(messages, scrollToBottom, { deep: true })

onMounted(() => {
  loadKnowledgeBases()
  loadSessions()
})
</script>

<style scoped>
.prose :deep(pre) {
  margin: 0.5rem 0;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
}

.prose :deep(code) {
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace;
  font-size: 0.875rem;
}

.prose :deep(p) {
  margin: 0.5rem 0;
}

.prose :deep(ul),
.prose :deep(ol) {
  margin: 0.5rem 0;
  padding-left: 1.5rem;
}

.prose :deep(blockquote) {
  margin: 0.5rem 0;
  padding-left: 1rem;
  border-left: 3px solid #e5e7eb;
  color: #6b7280;
}

.animate-pulse {
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}
</style>
