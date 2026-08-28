<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MainTabbar from './components/MainTabbar.vue'
import { getHealth } from './api/health.js'

const route = useRoute()

// 底部导航仅在非隐藏页显示（meta.hideTab=true 的页面如登录/详情不显示）
const showTabbar = computed(() => !route.meta.hideTab)

onMounted(async () => {
  try {
    const res = await getHealth()
    console.log('后端联调成功:', res.data)
  } catch (e) {
    console.error('后端联调失败:', e)
  }
})
</script>

<template>
  <router-view />
  <MainTabbar v-if="showTabbar" />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
    Segoe UI, Arial, Roboto, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft Yahei',
    sans-serif;
  background: #f7f8fa;
  color: #323233;
}
</style>
