<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbot-user-avatar text-primary text-xl"></span>
          <span class="text-lg font-semibold">个人助手</span>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <n-card class="cursor-pointer hover:shadow-md transition-shadow" @click="handleFeature('schedule')">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl bg-blue-50 flex-center">
              <span class="i-carbon-calendar text-2xl text-blue-500"></span>
            </div>
            <div>
              <div class="font-medium">日程管理</div>
              <div class="text-sm text-gray-400">管理您的日程安排</div>
            </div>
          </div>
        </n-card>
        <n-card class="cursor-pointer hover:shadow-md transition-shadow" @click="handleFeature('reminder')">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl bg-green-50 flex-center">
              <span class="i-carbon-notification text-2xl text-green-500"></span>
            </div>
            <div>
              <div class="font-medium">提醒事项</div>
              <div class="text-sm text-gray-400">设置重要提醒</div>
            </div>
          </div>
        </n-card>
        <n-card class="cursor-pointer hover:shadow-md transition-shadow" @click="handleFeature('note')">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl bg-yellow-50 flex-center">
              <span class="i-carbon-notebook text-2xl text-yellow-500"></span>
            </div>
            <div>
              <div class="font-medium">笔记记录</div>
              <div class="text-sm text-gray-400">记录工作笔记</div>
            </div>
          </div>
        </n-card>
        <n-card class="cursor-pointer hover:shadow-md transition-shadow" @click="handleFeature('task')">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl bg-purple-50 flex-center">
              <span class="i-carbon-task text-2xl text-purple-500"></span>
            </div>
            <div>
              <div class="font-medium">任务清单</div>
              <div class="text-sm text-gray-400">管理待办任务</div>
            </div>
          </div>
        </n-card>
      </div>

      <n-divider>今日待办</n-divider>

      <n-list hoverable clickable>
        <n-list-item v-for="item in todoList" :key="item.id">
          <template #prefix>
            <n-checkbox v-model:checked="item.done" @update:checked="handleTodoChange(item)" />
          </template>
          <n-thing :title="item.title" :description="item.time">
            <template #avatar>
              <n-tag :type="item.priority === 'high' ? 'error' : item.priority === 'medium' ? 'warning' : 'info'" size="small">
                {{ item.priority === 'high' ? '高优先' : item.priority === 'medium' ? '中优先' : '低优先' }}
              </n-tag>
            </template>
          </n-thing>
        </n-list-item>
      </n-list>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface TodoItem {
  id: number
  title: string
  time: string
  priority: 'high' | 'medium' | 'low'
  done: boolean
}

const todoList = ref<TodoItem[]>([])

const loadData = async () => {
  todoList.value = [
    { id: 1, title: '完成简历筛选', time: '09:00 - 10:00', priority: 'high', done: false },
    { id: 2, title: '面试候选人张三', time: '10:30 - 11:30', priority: 'high', done: false },
    { id: 3, title: '整理面试反馈', time: '14:00 - 15:00', priority: 'medium', done: false },
    { id: 4, title: '更新招聘需求', time: '16:00 - 17:00', priority: 'low', done: true }
  ]
}

const handleFeature = (feature: string) => {
  const featureMap: Record<string, string> = {
    schedule: '日程管理',
    reminder: '提醒事项',
    note: '笔记记录',
    task: '任务清单'
  }
  window.$message.info(`打开${featureMap[feature]}功能`)
}

const handleTodoChange = (item: TodoItem) => {
  window.$message.success(item.done ? `已完成: ${item.title}` : `取消完成: ${item.title}`)
}

onMounted(() => {
  loadData()
})
</script>