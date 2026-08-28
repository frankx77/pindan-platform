<template>
  <div class="pop-tabbar">
    <div
      v-for="t in tabs"
      :key="t.path"
      class="pop-tab"
      :class="{ active: route.path === t.path, 'publish-tab': t.publish }"
      @click="go(t.path)"
    >
      <span class="pop-tab-icon">
        <!-- 拼团：购物袋 -->
        <svg v-if="t.icon === 'bag'" viewBox="0 0 24 24" width="26" height="26">
          <path d="M5.5 7.5h13l-.9 12.5H6.4L5.5 7.5z" fill="#fff" stroke="#000" stroke-width="2" />
          <path d="M9 9.5a3 3 0 0 1 6 0" fill="none" stroke="#000" stroke-width="2" />
          <circle cx="12" cy="14" r="2" fill="#ffdd00" stroke="#000" stroke-width="1.5" />
        </svg>
        <!-- 消息：漫画对话气泡 -->
        <svg v-else-if="t.icon === 'bubble'" viewBox="0 0 24 24" width="26" height="26">
          <path d="M4.5 5h15v11.5h-9L6 20v-3.5H4.5z" fill="#fff" stroke="#000" stroke-width="2" />
          <circle cx="8.5" cy="10.8" r="1.4" fill="#000" />
          <circle cx="12" cy="10.8" r="1.4" fill="#000" />
          <circle cx="15.5" cy="10.8" r="1.4" fill="#000" />
        </svg>
        <!-- 发布：加号 -->
        <svg v-else-if="t.icon === 'plus'" viewBox="0 0 24 24" width="26" height="26">
          <path d="M12 5.5v13M5.5 12h13" stroke="#fff" stroke-width="3.4" stroke-linecap="round" />
        </svg>
        <!-- 我的拼团：叠卡 -->
        <svg v-else-if="t.icon === 'cards'" viewBox="0 0 24 24" width="26" height="26">
          <rect x="7" y="8" width="13" height="13" fill="#ffdd00" stroke="#000" stroke-width="2" />
          <rect x="4" y="4" width="13" height="13" fill="#fff" stroke="#000" stroke-width="2" />
        </svg>
        <!-- 我的：人形 -->
        <svg v-else viewBox="0 0 24 24" width="26" height="26">
          <circle cx="12" cy="8" r="4" fill="#ff69b4" stroke="#000" stroke-width="2" />
          <path d="M4.5 20c1.6-4 4.7-6 7.5-6s5.9 2 7.5 6" fill="#fff" stroke="#000" stroke-width="2" />
        </svg>
        <span v-if="t.badge && unreadCount > 0" class="pop-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
      </span>
      <span class="pop-tab-text">{{ t.label }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUnreadCount } from '../api/message.js'

const route = useRoute()
const router = useRouter()

// 5 个一级 Tab（由 App.vue 按路由 meta 控制显隐）
const tabs = [
  { path: '/groupon/list', label: '拼团', icon: 'bag' },
  { path: '/message', label: '消息', icon: 'bubble', badge: true },
  { path: '/groupon/publish', label: '发布', icon: 'plus', publish: true },
  { path: '/groupon/my/published', label: '我的拼团', icon: 'cards' },
  { path: '/my', label: '我的', icon: 'user' }
]

// 未读消息数（轮询更新，5 秒一次；符合"Axios 轮询代替 WebSocket"设计）
const unreadCount = ref(0)
let timer = null

function go(path) {
  if (route.path !== path) router.push(path)
}

async function refreshUnread() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data.count
  } catch (e) {
    // 未登录或网络错误时静默
  }
}

onMounted(() => {
  refreshUnread()
  timer = setInterval(refreshUnread, 5000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 自绘底部导航：白底黑顶边、激活黄底、波普图标、粉色凸起发布徽章 */
.pop-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  height: 58px;
  padding-bottom: env(safe-area-inset-bottom);
  background: #fff;
  border-top: 3px solid #000;
}

.pop-tab {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  padding-bottom: 7px;
  background: #fff;
  color: #000;
  transition: all 0.1s ease-out;
}
.pop-tab.active {
  background: #ffdd00;
}
.pop-tab:active {
  transform: scale(0.96);
}

.pop-tab-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  margin-bottom: 3px;
}
.pop-tab-icon svg {
  display: block;
}
.pop-tab.active .pop-tab-icon svg {
  transform: scale(1.1);
}

.pop-tab-text {
  font-size: 11px;
  font-weight: 800;
  font-family: Impact, 'Arial Black', 'SimHei', 'Microsoft YaHei', sans-serif;
  line-height: 1;
  text-align: center;
}

/* 未读角标（消息 tab） */
.pop-badge {
  position: absolute;
  top: -7px;
  right: -12px;
  min-width: 17px;
  height: 17px;
  padding: 0 4px;
  border-radius: 9px;
  background: #ff0506;
  border: 1.5px solid #000;
  color: #fff;
  font-size: 10px;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: Impact, 'Arial Black', sans-serif;
}

/* 发布：粉色凸起徽章（绝对定位，不影响文字对齐） */
.publish-tab .pop-tab-icon {
  position: absolute;
  top: -14px;
  width: 44px;
  height: 44px;
  margin-bottom: 0;
  background: #ff69b4;
  border: 2px solid #000;
  border-radius: 8px;
  box-shadow: 3px 3px 0 #000;
}
.publish-tab.active .pop-tab-icon {
  background: #ff69b4;
}
</style>
