<template>
  <div class="message-page pop pop-page">
    <van-nav-bar title="消息" />

    <!-- 置顶：拼团小助手 -->
    <van-cell-group class="pop-group pop-assistant-group">
      <van-cell
        title="拼团小助手"
        icon="service-o"
        label="功能介绍、更新日志与基本规则"
        is-link
        @click="goAssistant"
      />
    </van-cell-group>

    <!-- 消息列表 -->
    <van-cell-group class="pop-group msg-group">
      <van-cell
        v-for="m in messages"
        :key="m.id"
        :title="m.title"
        :label="m.content"
        :is-link="m.grouponId != null"
        :to="m.grouponId != null ? `/groupon/detail/${m.grouponId}` : undefined"
        @click="m.grouponId != null && handleRead(m.id)"
      >
        <template #icon>
          <div class="pop-msg-icon" :class="m.type.toLowerCase()">
            <van-icon :name="typeIcon(m.type)" size="20" />
          </div>
        </template>
        <template #right-icon>
          <div class="pop-msg-right">
            <span v-if="m.isRead === 0" class="pop-unread-dot"></span>
            <span class="pop-msg-time">{{ formatTime(m.createdAt) }}</span>
          </div>
        </template>
      </van-cell>
      <van-empty v-if="messages.length === 0" description="暂无消息" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMessages, markMessageRead, markAllRead } from '../api/message.js'
import '../styles/pop-art.css'

const router = useRouter()
const messages = ref([])
let timer = null

function typeIcon(type) {
  return {
    APPLY_NEW: 'friends-o',
    JOIN_SUCCESS: 'checked',
    JOIN_FAIL: 'warning-o'
  }[type] || 'info-o'
}

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(5, 16) // MM-DD HH:mm
}

async function loadMessages() {
  try {
    const res = await getMessages()
    messages.value = res.data
  } catch (e) {
    // 静默
  }
}

async function handleRead(id) {
  await markMessageRead(id)
  loadMessages()
}

function goAssistant() {
  router.push('/message/assistant')
}

onMounted(async () => {
  await loadMessages()
  // 进入消息页：全部标记已读
  try {
    await markAllRead()
  } catch (e) {
    // 静默
  }
  // 5 秒轮询刷新（符合"Axios 轮询代替 WebSocket"设计）
  timer = setInterval(loadMessages, 5000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.message-page {
  min-height: 100vh;
}
.msg-group {
  margin-bottom: 12px;
}
</style>
