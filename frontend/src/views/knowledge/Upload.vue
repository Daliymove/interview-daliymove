<template>
  <div class="flex flex-col h-full">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-900 flex items-center gap-2">
        <span class="i-carbon-folder text-primary text-2xl"></span>
        上传知识库
      </h1>
      <p class="text-gray-500 mt-1">上传文档，AI 将基于知识库内容回答您的问题</p>
    </div>

    <div class="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-6 min-h-0">
      <div class="lg:col-span-2">
        <n-card class="h-full">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-gray-900 mb-2">上传文档文件</h2>
            <p class="text-sm text-gray-500">支持 PDF、DOCX、DOC、TXT、MD 格式，最大 50MB</p>
          </div>

          <n-upload
            :accept="accept"
            :disabled="uploading"
            :show-file-list="false"
            :custom-request="handleUpload"
            drag
          >
            <div
              class="text-center p-12 border-2 border-dashed rounded-lg transition-all cursor-pointer hover:border-primary hover:bg-primary-50"
              :class="dragOver ? 'border-primary bg-primary-50' : 'border-gray-300'"
              @dragover="dragOver = true"
              @dragleave="dragOver = false"
              @drop="dragOver = false"
            >
              <template v-if="selectedFile">
                <div class="w-16 h-16 mx-auto bg-emerald-100 rounded-full flex items-center justify-center mb-4">
                  <span class="i-carbon-document text-3xl text-emerald-600"></span>
                </div>
                <div class="flex items-center justify-center gap-3 bg-gray-50 px-4 py-3 rounded-lg max-w-md mx-auto mb-4">
                  <div class="text-left flex-1 min-w-0">
                    <p class="font-medium text-gray-900 truncate">{{ selectedFile.name }}</p>
                    <p class="text-sm text-gray-500">{{ formatFileSize(selectedFile.size) }}</p>
                  </div>
                  <n-button quaternary circle size="small" @click.stop="clearFile">
                    <template #icon>
                      <span class="i-carbon-close text-red-500"></span>
                    </template>
                  </n-button>
                </div>

                <div class="max-w-md mx-auto mb-4">
                  <n-input
                    v-model:value="knowledgeName"
                    placeholder="知识库名称（可选，留空则使用文件名）"
                    :disabled="uploading"
                  >
                    <template #prefix>
                      <span class="i-carbon-edit text-gray-400"></span>
                    </template>
                  </n-input>
                </div>

                <div class="flex items-center justify-center gap-3">
                  <n-button
                    size="large"
                    :disabled="uploading"
                    @click.stop="handleBack"
                  >
                    返回
                  </n-button>
                  <n-button
                    type="primary"
                    size="large"
                    :loading="uploading"
                    :disabled="uploading"
                  >
                    开始上传
                  </n-button>
                </div>
              </template>
              <template v-else>
                <div class="w-16 h-16 mx-auto bg-gray-100 rounded-full flex items-center justify-center mb-4">
                  <span class="i-carbon-cloud-upload text-3xl text-gray-400"></span>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-2">点击或拖拽文件至此处</h3>
                <p class="text-sm text-gray-500 mb-4">支持 PDF、DOCX、DOC、TXT、MD 格式</p>
                <n-button type="primary">选择文件</n-button>
              </template>
            </div>
          </n-upload>

          <template v-if="error">
            <n-alert type="error" class="mt-4" :bordered="false">
              <template #icon>
                <span class="i-carbon-warning-alt"></span>
              </template>
              {{ error }}
            </n-alert>
          </template>
        </n-card>
      </div>

      <div class="space-y-6">
        <n-card>
          <h3 class="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <span class="i-carbon-information text-primary"></span>
            使用说明
          </h3>
          <div class="space-y-3 text-sm text-gray-600">
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>支持 PDF、Word、文本和 Markdown 格式</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>文件大小限制为 50MB</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>上传后系统会自动向量化处理</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>支持基于知识库的智能问答</span>
            </div>
          </div>
        </n-card>

        <n-card>
          <h3 class="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <span class="i-carbon-light text-primary"></span>
            小提示
          </h3>
          <div class="space-y-3 text-sm text-gray-600">
            <p>为获得最佳问答效果，建议您：</p>
            <ul class="list-disc list-inside space-y-1 ml-2">
              <li>上传结构清晰的文档</li>
              <li>内容包含标题和段落</li>
              <li>避免图片过多的文档</li>
              <li>为知识库设置清晰的名称</li>
            </ul>
          </div>
        </n-card>

        <n-card>
          <h3 class="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <span class="i-carbon-time text-primary"></span>
            处理时间
          </h3>
          <div class="text-sm text-gray-600">
            <p>文档上传后需要一定时间进行向量化处理，处理时间取决于文档大小和内容复杂度。</p>
          </div>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 知识库上传页面
 * - 支持文件拖拽上传
 * - 文件类型和大小验证
 * - 知识库名称输入（可选）
 * - 上传后跳转到管理页面
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { knowledgeBaseApi } from '@/api/knowledgebase'
import type { UploadCustomRequestOptions } from 'naive-ui'

const router = useRouter()
const uploading = ref(false)
const error = ref('')
const selectedFile = ref<File | null>(null)
const knowledgeName = ref('')
const dragOver = ref(false)

const accept = '.pdf,.doc,.docx,.txt,.md'

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const clearFile = () => {
  selectedFile.value = null
  knowledgeName.value = ''
  error.value = ''
}

const validateFile = (file: File): boolean => {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    error.value = '文件大小不能超过 50MB'
    return false
  }

  const ext = file.name.split('.').pop()?.toLowerCase()
  const allowedExts = ['pdf', 'doc', 'docx', 'txt', 'md']
  if (!ext || !allowedExts.includes(ext)) {
    error.value = '仅支持 PDF、DOC、DOCX、TXT、MD 格式'
    return false
  }

  return true
}

const handleUpload = async ({ file }: UploadCustomRequestOptions) => {
  if (!file.file) return

  if (!validateFile(file.file)) {
    return
  }

  uploading.value = true
  error.value = ''

  try {
    const name = knowledgeName.value.trim() || undefined
    const data = await knowledgeBaseApi.uploadKnowledgeBase(file.file, name)
    window.$message.success(`知识库 "${data.knowledgeBase.name}" 上传成功，正在向量化处理...`)
    router.push('/knowledge')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '上传失败，请重试'
    uploading.value = false
  }
}

const handleBack = () => {
  router.push('/knowledge')
}
</script>