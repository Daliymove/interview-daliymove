<template>
  <div class="flex flex-col h-full">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-900 flex items-center gap-2">
        <span class="i-carbon-upload text-primary text-2xl"></span>
        上传简历
      </h1>
      <p class="text-gray-500 mt-1">上传简历开始您的 AI 模拟面试之旅</p>
    </div>

    <div class="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-6 min-h-0">
      <div class="lg:col-span-2">
        <n-card class="h-full">
          <div class="mb-6">
            <h2 class="text-lg font-semibold text-gray-900 mb-2">上传简历文件</h2>
            <p class="text-sm text-gray-500">支持 PDF、DOCX、TXT 格式，最大 10MB</p>
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
                <n-button
                  type="primary"
                  size="large"
                  :loading="uploading"
                  :disabled="uploading"
                >
                  开始上传并分析
                </n-button>
              </template>
              <template v-else>
                <div class="w-16 h-16 mx-auto bg-gray-100 rounded-full flex items-center justify-center mb-4">
                  <span class="i-carbon-cloud-upload text-3xl text-gray-400"></span>
                </div>
                <h3 class="text-lg font-medium text-gray-900 mb-2">点击或拖拽文件至此处</h3>
                <p class="text-sm text-gray-500 mb-4">支持 PDF、DOCX、TXT 格式</p>
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
              <span>支持 PDF、Word 文档和纯文本格式</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>文件大小限制为 10MB</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>上传后系统会自动解析和分析简历</span>
            </div>
            <div class="flex gap-2">
              <span class="i-carbon-checkmark text-emerald-500 flex-shrink-0 mt-0.5"></span>
              <span>分析完成后可开始模拟面试</span>
            </div>
          </div>
        </n-card>

        <n-card>
          <h3 class="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <span class="i-carbon-light text-primary"></span>
            小提示
          </h3>
          <div class="space-y-3 text-sm text-gray-600">
            <p>为确保最佳分析效果，建议您：</p>
            <ul class="list-disc list-inside space-y-1 ml-2">
              <li>使用清晰的 PDF 格式简历</li>
              <li>确保简历内容完整、格式规范</li>
              <li>包含项目经验和技能描述</li>
              <li>避免扫描件或图片格式</li>
            </ul>
          </div>
        </n-card>

        <n-card>
          <h3 class="text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <span class="i-carbon-privacy text-primary"></span>
            隐私保护
          </h3>
          <div class="text-sm text-gray-600">
            <p>您的简历数据将被安全存储，仅用于 AI 分析和模拟面试，不会用于其他用途。</p>
          </div>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 简历上传页面
 * - 支持文件拖拽上传
 * - 文件类型和大小验证
 * - 上传后跳转到简历详情
 * - 提供使用说明和提示
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { resumeApi } from '@/api/resume'
import type { UploadCustomRequestOptions } from 'naive-ui'

const router = useRouter()
const uploading = ref(false)
const error = ref('')
const selectedFile = ref<File | null>(null)
const dragOver = ref(false)

const accept = '.pdf,.doc,.docx,.txt'

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const clearFile = () => {
  selectedFile.value = null
  error.value = ''
}

const validateFile = (file: File): boolean => {
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    error.value = '文件大小不能超过 10MB'
    return false
  }

  const ext = file.name.split('.').pop()?.toLowerCase()
  const allowedExts = ['pdf', 'doc', 'docx', 'txt']
  if (!ext || !allowedExts.includes(ext)) {
    error.value = '仅支持 PDF、DOC、DOCX、TXT 格式'
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
    const data = await resumeApi.uploadAndAnalyze(file.file)

    // 验证返回数据
    if (!data?.storage?.resumeId) {
      throw new Error('上传失败，未获取到简历信息')
    }

    const { storage, duplicate } = data
    const resumeId = storage.resumeId

    // 根据是否重复简历显示不同提示
    if (duplicate) {
      window.$message.warning('检测到相同简历，已返回历史分析结果')
    } else {
      window.$message.success('简历上传成功，正在分析中...')
    }

    // 跳转到简历详情页
    router.push(`/resume/${resumeId}`)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '上传失败，请稍后重试'
    uploading.value = false
  }
}
</script>