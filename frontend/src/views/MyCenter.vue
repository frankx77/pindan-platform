<template>
  <div class="my-center pop pop-page">
    <!-- 用户信息卡片 -->
    <div class="pop-user-card">
      <div class="pop-dots"></div>
      <div class="pop-user-inner">
        <div class="avatar">
          <van-image
            v-if="userInfo.avatar"
            round
            width="56"
            height="56"
            :src="userInfo.avatar"
          />
          <van-icon v-else name="user-circle-o" size="56" />
        </div>
        <div class="user-meta">
          <div class="pop-nickname">{{ userInfo.nickname || userInfo.username }}</div>
          <div class="pop-username">@{{ userInfo.username }}</div>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <van-cell-group class="pop-group pop-menu">
      <!-- 管理后台：仅管理员可见 -->
      <van-cell
        v-if="userInfo.role === 2"
        title="管理后台"
        icon="bar-chart-o"
        is-link
        @click="router.push('/admin')"
      />
      <van-cell title="退出登录" icon="close" @click="handleLogout" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import '../styles/pop-art.css'

const router = useRouter()

// 从 localStorage 读取登录用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))

async function handleLogout() {
  try {
    await showConfirmDialog({
      title: '退出登录',
      message: '确定要退出当前账号吗？'
    })
  } catch (e) {
    return // 用户取消
  }
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  showToast('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.my-center {
  min-height: 100vh;
}
.user-meta {
  color: #000;
}
</style>
