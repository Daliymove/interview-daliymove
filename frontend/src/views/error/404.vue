<template>
  <div class="min-h-screen flex-center bg-gray-50">
    <div class="text-center">
      <div class="mb-8">
        <div class="w-32 h-32 mx-auto rounded-full bg-primary/10 flex-center">
          <span class="i-carbon-document text-6xl text-primary"></span>
        </div>
      </div>
      <h1 class="text-8xl font-bold text-gray-200">404</h1>
      <p class="text-xl text-gray-500 mt-4 mb-8">抱歉，您访问的页面不存在</p>
      <n-button type="primary" size="large" @click="$router.push('/')">
        <template #icon>
          <span class="i-carbon-home"></span>
        </template>
        返回首页
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>