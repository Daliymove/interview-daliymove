<template>
  <div class="min-h-screen flex">
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary to-primary-dark relative overflow-hidden">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-20 left-20 w-72 h-72 bg-white rounded-full blur-3xl"></div>
        <div class="absolute bottom-20 right-20 w-96 h-96 bg-white rounded-full blur-3xl"></div>
      </div>
      <div class="relative z-10 flex flex-col justify-center px-16 text-white">
        <div class="flex items-center gap-3 mb-8">
          <div class="w-12 h-12 rounded-xl bg-white/20 flex-center">
            <span class="i-carbon-cube text-3xl"></span>
          </div>
          <span class="text-2xl font-bold">企业管理后台</span>
        </div>
        <h1 class="text-4xl font-bold mb-4">欢迎使用</h1>
        <p class="text-lg text-white/80 mb-8">
          高效、安全、现代化的企业级权限管理系统
        </p>
        <div class="space-y-4">
          <div class="flex items-center gap-3">
            <span class="i-carbon-checkmark-filled text-xl"></span>
            <span>基于 RBAC 的精细化权限控制</span>
          </div>
          <div class="flex items-center gap-3">
            <span class="i-carbon-checkmark-filled text-xl"></span>
            <span>动态菜单与路由配置</span>
          </div>
          <div class="flex items-center gap-3">
            <span class="i-carbon-checkmark-filled text-xl"></span>
            <span>安全可靠的认证机制</span>
          </div>
        </div>
      </div>
    </div>

    <div class="w-full lg:w-1/2 flex-center bg-gray-50 px-8">
      <div class="w-full max-w-md">
        <div class="text-center mb-8 lg:hidden">
          <div class="flex-center gap-2 mb-4">
            <div class="w-10 h-10 rounded-xl bg-primary flex-center">
              <span class="i-carbon-cube text-white text-xl"></span>
            </div>
            <span class="text-xl font-bold text-gray-800">企业管理后台</span>
          </div>
        </div>

        <n-card class="shadow-lg" :bordered="false">
          <div class="text-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800 mb-2">登录账户</h2>
            <p class="text-gray-500">请输入您的账户信息</p>
          </div>

          <n-form ref="formRef" :model="form" :rules="rules" size="large">
            <n-form-item path="username" label="用户名">
              <n-input 
                v-model:value="form.username" 
                placeholder="请输入用户名"
                :input-props="{ autocomplete: 'username' }"
              >
                <template #prefix>
                  <span class="i-carbon-user text-gray-400"></span>
                </template>
              </n-input>
            </n-form-item>
            <n-form-item path="password" label="密码">
              <n-input
                v-model:value="form.password"
                type="password"
                placeholder="请输入密码"
                show-password-on="click"
                :input-props="{ autocomplete: 'current-password' }"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <span class="i-carbon-locked text-gray-400"></span>
                </template>
              </n-input>
            </n-form-item>
            <n-form-item>
              <n-button 
                type="primary" 
                block 
                :loading="loading" 
                @click="handleLogin"
                class="h-11"
              >
                登 录
              </n-button>
            </n-form-item>
          </n-form>

          <div class="text-center">
            <n-divider class="text-gray-400">
              <span class="text-sm">演示账号</span>
            </n-divider>
            <div class="flex-center gap-6 text-sm text-gray-500">
              <span>用户名: <span class="font-medium text-gray-700">admin</span></span>
              <span>密码: <span class="font-medium text-gray-700">123456</span></span>
            </div>
          </div>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInst, FormRules } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInst | null>(null)
const loading = ref(false)

const form = ref({
  username: 'admin',
  password: '123456'
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    await userStore.login(form.value.username, form.value.password)
    window.$message.success('登录成功')
    await router.replace('/dashboard')
  } catch (error: any) {
    if (error?.message) {
      window.$message.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>