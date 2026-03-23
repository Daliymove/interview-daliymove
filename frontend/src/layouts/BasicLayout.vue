<template>
  <n-layout has-sider class="h-screen">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="220"
      :collapsed="collapsed"
      show-trigger
      :native-scrollbar="false"
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="h-14 flex-center border-b border-gray-100">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 rounded-lg bg-primary flex-center">
            <span class="i-carbon-cube text-white text-lg"></span>
          </div>
          <span v-if="!collapsed" class="text-base font-semibold text-gray-800">企业管理后台</span>
        </div>
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="20"
        :options="menuOptions"
        :value="currentKey"
        :indent="20"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <n-layout>
      <n-layout-header bordered class="h-14 px-5 flex-between bg-white">
        <div class="flex items-center gap-4">
          <n-breadcrumb>
            <n-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </n-breadcrumb-item>
          </n-breadcrumb>
        </div>
        <div class="flex items-center gap-3">
          <n-tooltip trigger="hover">
            <template #trigger>
              <n-button quaternary circle size="small">
                <template #icon>
                  <span class="i-carbon-help text-lg text-gray-500"></span>
                </template>
              </n-button>
            </template>
            帮助文档
          </n-tooltip>
          <n-dropdown :options="userOptions" @select="handleUserSelect">
            <div class="flex items-center gap-2 cursor-pointer px-3 py-1.5 rounded-lg hover:bg-gray-50 transition-colors">
              <n-avatar round size="small" :src="userStore.userInfo?.avatar" class="bg-primary">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </n-avatar>
              <span class="text-sm text-gray-700">{{ userStore.userInfo?.nickname }}</span>
              <span class="i-carbon-chevron-down text-gray-400 text-sm"></span>
            </div>
          </n-dropdown>
        </div>
      </n-layout-header>

      <n-layout-content class="p-5 bg-gray-50" content-style="min-height: calc(100vh - 56px);">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { MenuOption } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)

const renderIcon = (icon: string) => {
  return () => h('span', { class: `i-${icon} text-lg` })
}

const menuOptions: MenuOption[] = [
  {
    label: '仪表盘',
    key: 'dashboard',
    icon: renderIcon('carbon-dashboard')
  },
  {
    label: '系统管理',
    key: 'system',
    icon: renderIcon('carbon-settings'),
    children: [
      {
        label: '用户管理',
        key: 'system-user',
        icon: renderIcon('carbon-user-multiple')
      },
      {
        label: '角色管理',
        key: 'system-role',
        icon: renderIcon('carbon-user-role')
      },
      {
        label: '权限管理',
        key: 'system-permission',
        icon: renderIcon('carbon-locked')
      },
      {
        label: '菜单管理',
        key: 'system-menu',
        icon: renderIcon('carbon-menu')
      }
    ]
  },
  {
    label: '日志管理',
    key: 'log',
    icon: renderIcon('carbon-document'),
    children: [
      {
        label: '操作日志',
        key: 'log-operation',
        icon: renderIcon('carbon-activity')
      }
    ]
  }
]

const currentKey = computed(() => {
  const path = route.path
  if (path === '/dashboard') return 'dashboard'
  if (path.startsWith('/system/user')) return 'system-user'
  if (path.startsWith('/system/role')) return 'system-role'
  if (path.startsWith('/system/permission')) return 'system-permission'
  if (path.startsWith('/system/menu')) return 'system-menu'
  if (path.startsWith('/log/operation')) return 'log-operation'
  return ''
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    path: item.path,
    title: item.meta?.title as string
  }))
})

const userOptions = [
  {
    label: '个人信息',
    key: 'profile',
    icon: () => h('span', { class: 'i-carbon-user text-base' })
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h('span', { class: 'i-carbon-logout text-base' })
  }
]

const handleMenuSelect = (key: string) => {
  const keyPathMap: Record<string, string> = {
    'dashboard': '/dashboard',
    'system-user': '/system/user',
    'system-role': '/system/role',
    'system-permission': '/system/permission',
    'system-menu': '/system/menu',
    'log-operation': '/log/operation'
  }
  
  const path = keyPathMap[key]
  if (path) {
    router.push(path)
  }
}

const handleUserSelect = async (key: string) => {
  if (key === 'logout') {
    try {
      await authApi.logout()
    } catch (error) {
      // ignore
    }
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>