<template>
  <div class="my-page pop pop-page">
    <van-nav-bar title="我的拼团" right-text="导出 Excel" @click-right="handleExport" />

    <van-tabs v-model:active="activeTab">
      <van-tab title="我发布的">
        <div v-if="published.length > 0">
          <div
            v-for="item in published"
            :key="item.id"
            class="pop-card"
            :class="'st-' + item.status"
            @click="goDetail(item.id)"
          >
            <div class="pop-card-head">
              <span class="pop-card-title">{{ item.title }}</span>
              <span class="pop-tag" :class="'tag-' + item.status">{{ statusText(item.status) }}</span>
            </div>
            <div class="pop-card-meta">{{ formatLabel(item) }}</div>
            <div class="pop-progress">
              <div class="pop-progress-fill" :class="'fill-' + item.status" :style="{ width: pct(item) }"></div>
            </div>
          </div>
        </div>
        <van-empty v-else description="还没有发布过拼单" />
      </van-tab>

      <van-tab title="我参加的">
        <div v-if="joined.length > 0">
          <div
            v-for="item in joined"
            :key="item.id"
            class="pop-card"
            :class="'st-' + item.status"
            @click="goDetail(item.id)"
          >
            <div class="pop-card-head">
              <span class="pop-card-title">{{ item.title }}</span>
              <span class="pop-tag" :class="'tag-' + item.status">{{ statusText(item.status) }}</span>
            </div>
            <div class="pop-card-meta">{{ formatLabel(item) }}</div>
            <div class="pop-progress">
              <div class="pop-progress-fill" :class="'fill-' + item.status" :style="{ width: pct(item) }"></div>
            </div>
          </div>
        </div>
        <van-empty v-else description="还没有参加任何拼单" />
      </van-tab>
    </van-tabs>

    <!-- 二级页面说明：本页为一级 Tab（我的拼团），底部导航由全局 MainTabbar 提供 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { getMyPublished, getMyJoined, exportMyPublished, exportMyJoined } from '../api/groupon.js'
import '../styles/pop-art.css'

const router = useRouter()
const activeTab = ref(0)
const published = ref([])
const joined = ref([])
const exporting = ref(false)

function statusText(status) {
  return ['招募中', '已成团', '已结束', '已取消'][status] || '未知'
}

function statusColor(status) {
  return ['#1989fa', '#07c160', '#969799', '#ee0a24'][status] || '#969799'
}

function formatLabel(item) {
  return `${item.categoryTag} · ${item.currentCount}/${item.targetCount}人 · 截止 ${item.deadline || '不限'}`
}

function pct(item) {
  if (!item.targetCount) return '0%'
  return Math.min(100, Math.round((item.currentCount / item.targetCount) * 100)) + '%'
}

function goDetail(id) {
  router.push(`/groupon/detail/${id}`)
}

// 导出当前 Tab 的拼单为 Excel
async function handleExport() {
  if (exporting.value) return
  exporting.value = true
  try {
    const isPublished = activeTab.value === 0
    const res = isPublished ? await exportMyPublished() : await exportMyJoined()
    const filename = isPublished ? '我发布的拼单.xlsx' : '我参加的拼单.xlsx'
    downloadBlob(res.data, filename)
    showSuccessToast('导出成功，请查收下载文件')
  } catch (e) {
    showToast(e.message || '导出失败')
  } finally {
    exporting.value = false
  }
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

onMounted(async () => {
  try {
    const [p, j] = await Promise.all([getMyPublished(), getMyJoined()])
    published.value = p.data
    joined.value = j.data
  } catch (e) {
    showToast(e.message || '加载失败')
  }
})
</script>

<style scoped>
.my-page {
  min-height: 100vh;
}
</style>
