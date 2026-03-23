<template>
  <div class="space-y-5">
    <div class="flex-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">仪表盘</h1>
        <p class="text-gray-500 mt-1">欢迎回来，{{ userStore.userInfo?.nickname }}</p>
      </div>
      <n-button type="primary">
        <template #icon>
          <span class="i-carbon-document"></span>
        </template>
        查看文档
      </n-button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
      <div 
        v-for="stat in statistics" 
        :key="stat.label"
        class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-shadow"
      >
        <div class="flex-between">
          <div>
            <p class="text-gray-500 text-sm">{{ stat.label }}</p>
            <p class="text-3xl font-bold text-gray-800 mt-2">{{ stat.value }}</p>
            <p class="text-sm mt-2" :class="stat.trend > 0 ? 'text-green-500' : 'text-red-500'">
              <span :class="stat.trend > 0 ? 'i-carbon-arrow-up' : 'i-carbon-arrow-down'"></span>
              {{ Math.abs(stat.trend) }}% 较上月
            </p>
          </div>
          <div 
            class="w-12 h-12 rounded-xl flex-center text-white text-2xl"
            :class="stat.color"
          >
            <span :class="stat.icon"></span>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-5">
      <n-card title="快捷操作" class="lg:col-span-1">
        <div class="grid grid-cols-2 gap-3">
          <div 
            v-for="action in quickActions" 
            :key="action.label"
            class="p-4 rounded-lg border border-gray-100 hover:border-primary hover:bg-primary/5 cursor-pointer transition-all"
            @click="handleQuickAction(action.path)"
          >
            <div class="flex flex-col items-center gap-2">
              <div class="w-10 h-10 rounded-lg bg-primary/10 flex-center text-primary">
                <span :class="action.icon" class="text-xl"></span>
              </div>
              <span class="text-sm text-gray-700">{{ action.label }}</span>
            </div>
          </div>
        </div>
      </n-card>

      <n-card title="系统信息" class="lg:col-span-2">
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-3">
            <div class="flex items-center gap-3">
              <span class="i-carbon-settings text-gray-400"></span>
              <span class="text-gray-500 text-sm">系统版本</span>
              <span class="ml-auto text-gray-800">v1.0.0</span>
            </div>
            <div class="flex items-center gap-3">
              <span class="i-carbon-code text-gray-400"></span>
              <span class="text-gray-500 text-sm">前端框架</span>
              <span class="ml-auto text-gray-800">Vue 3 + Naive UI</span>
            </div>
            <div class="flex items-center gap-3">
              <span class="i-carbon-data-base text-gray-400"></span>
              <span class="text-gray-500 text-sm">后端框架</span>
              <span class="ml-auto text-gray-800">Spring Boot 3</span>
            </div>
          </div>
          <div class="space-y-3">
            <div class="flex items-center gap-3">
              <span class="i-carbon-locked text-gray-400"></span>
              <span class="text-gray-500 text-sm">认证方式</span>
              <span class="ml-auto text-gray-800">Sa-Token</span>
            </div>
            <div class="flex items-center gap-3">
              <span class="i-carbon-data-vis-4 text-gray-400"></span>
              <span class="text-gray-500 text-sm">数据库</span>
              <span class="ml-auto text-gray-800">MySQL 8</span>
            </div>
            <div class="flex items-center gap-3">
              <span class="i-carbon-time text-gray-400"></span>
              <span class="text-gray-500 text-sm">构建时间</span>
              <span class="ml-auto text-gray-800">{{ buildTime }}</span>
            </div>
          </div>
        </div>
      </n-card>
    </div>

    <n-card title="最近活动">
      <n-empty v-if="recentActivities.length === 0" description="暂无活动记录" />
      <div v-else class="space-y-4">
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id"
          class="flex items-center gap-4 p-3 rounded-lg hover:bg-gray-50 transition-colors"
        >
          <div 
            class="w-8 h-8 rounded-full flex-center text-white text-sm"
            :class="activity.type === 'login' ? 'bg-green-500' : 'bg-blue-500'"
          >
            <span :class="activity.type === 'login' ? 'i-carbon-login' : 'i-carbon-edit'"></span>
          </div>
          <div class="flex-1">
            <p class="text-gray-800">{{ activity.content }}</p>
            <p class="text-gray-400 text-sm">{{ activity.time }}</p>
          </div>
        </div>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const buildTime = new Date().toLocaleDateString('zh-CN')

const statistics = ref([
  { label: '用户总数', value: '1,234', trend: 12.5, icon: 'i-carbon-user-multiple', color: 'bg-blue-500' },
  { label: '角色总数', value: '56', trend: 5.2, icon: 'i-carbon-user-role', color: 'bg-green-500' },
  { label: '菜单总数', value: '128', trend: -2.1, icon: 'i-carbon-menu', color: 'bg-orange-500' },
  { label: '权限总数', value: '256', trend: 8.7, icon: 'i-carbon-locked', color: 'bg-purple-500' }
])

const quickActions = ref([
  { label: '用户管理', icon: 'i-carbon-user-multiple', path: '/system/user' },
  { label: '角色管理', icon: 'i-carbon-user-role', path: '/system/role' },
  { label: '权限管理', icon: 'i-carbon-locked', path: '/system/permission' },
  { label: '菜单管理', icon: 'i-carbon-menu', path: '/system/menu' }
])

const recentActivities = ref([
  { id: 1, type: 'login', content: '管理员登录系统', time: '2 分钟前' },
  { id: 2, type: 'edit', content: '修改了用户 "admin" 的信息', time: '10 分钟前' },
  { id: 3, type: 'edit', content: '新增角色 "测试角色"', time: '1 小时前' }
])

const handleQuickAction = (path: string) => {
  router.push(path)
}
</script>