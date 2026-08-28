<template>
  <div class="admin-page pop pop-page">
    <!-- 顶部：漫画横幅 -->
    <div class="pop-header">
      <div class="pop-dots"></div>
      <div class="pop-header-inner">
        <span class="pop-bubble">ADMIN PANEL</span>
        <h1>管理后台</h1>
      </div>
    </div>

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <!-- Tab 1：数据看板 -->
      <van-tab title="数据看板">
        <div class="stat-cards">
          <div class="stat-card sc-yellow">
            <div class="stat-num">{{ dashboard.totalGroupons ?? '-' }}</div>
            <div class="stat-label">拼单总数</div>
          </div>
          <div class="stat-card sc-pink">
            <div class="stat-num">{{ dashboard.totalUsers ?? '-' }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>

        <div class="chart-card">
          <div class="chart-title">拼单状态分布</div>
          <div ref="pieRef" class="chart-box"></div>
        </div>

        <div class="chart-card">
          <div class="chart-title">TOP 5 热门分类</div>
          <div ref="barRef" class="chart-box"></div>
        </div>
      </van-tab>

      <!-- Tab 2：拼单管理 -->
      <van-tab title="拼单管理">
        <van-search
          v-model="gQuery.keyword"
          placeholder="搜索拼单标题"
          show-action
          @search="loadGroupons"
          @clear="loadGroupons"
        >
          <template #action>
            <span class="pop-search-btn" @click="loadGroupons">搜索</span>
          </template>
        </van-search>
        <div class="filter-row">
          <span class="filter-label">状态</span>
          <span
            v-for="s in statusOptions"
            :key="s.value"
            class="pop-chip"
            :class="{ active: gQuery.status === s.value }"
            @click="pickStatus(s.value)"
          >{{ s.text }}</span>
        </div>

        <van-empty v-if="!gLoading && groupons.length === 0" description="暂无拼单" />
        <div v-else class="pop-list">
          <div v-for="item in groupons" :key="item.id" class="pop-card" :class="'st-' + item.status">
            <div class="pop-card-head">
              <span class="pop-card-title">{{ item.title }}</span>
              <span class="pop-tag" :class="'tag-' + item.status">{{ statusText(item.status) }}</span>
            </div>
            <div class="pop-card-meta">
              {{ item.categoryTag }} · {{ item.currentCount }}/{{ item.targetCount }}人 ·
              团长 {{ item.publisherNickname || item.publisherUsername }}
            </div>
            <div class="pop-card-meta sub">发布于 {{ item.createdAt }}</div>
            <van-button
              v-if="item.status !== 3"
              class="pop-btn danger"
              size="small"
              @click.stop="handleCancel(item)"
            >强制下架</van-button>
            <span v-else class="cancelled-label">已下架</span>
          </div>
        </div>

        <div class="pagination" v-if="gTotal > gPageSize">
          <van-button class="pop-btn" size="small" :disabled="gPageNum <= 1" @click="gPage(gPageNum - 1)">上一页</van-button>
          <span class="page-info">{{ gPageNum }} / {{ Math.max(1, Math.ceil(gTotal / gPageSize)) }}</span>
          <van-button class="pop-btn" size="small" :disabled="gPageNum >= Math.ceil(gTotal / gPageSize)" @click="gPage(gPageNum + 1)">下一页</van-button>
        </div>
      </van-tab>

      <!-- Tab 3：用户管理 -->
      <van-tab title="用户管理">
        <van-search
          v-model="uQuery.keyword"
          placeholder="搜索用户名 / 昵称"
          show-action
          @search="loadUsers"
          @clear="loadUsers"
        >
          <template #action>
            <span class="pop-search-btn" @click="loadUsers">搜索</span>
          </template>
        </van-search>
        <van-empty v-if="!uLoading && users.length === 0" description="暂无用户" />
        <div v-else class="pop-list">
          <div v-for="u in users" :key="u.id" class="pop-card user-card">
            <div class="pop-card-head">
              <span class="pop-card-title">{{ u.nickname || u.username }}</span>
              <span class="pop-tag" :class="u.status === 1 ? 'tag-1' : 'tag-3'">
                {{ u.role === 2 ? '管理员' : u.status === 1 ? '正常' : '已禁用' }}
              </span>
            </div>
            <div class="pop-card-meta">@{{ u.username }} · 注册于 {{ u.createdAt }}</div>
            <van-button
              v-if="u.role !== 2 && u.status === 1"
              class="pop-btn danger"
              size="small"
              @click.stop="handleBan(u)"
            >禁用</van-button>
            <van-button
              v-else-if="u.role !== 2 && u.status === 0"
              class="pop-btn"
              size="small"
              @click.stop="handleUnban(u)"
            >解除禁用</van-button>
          </div>
        </div>

        <div class="pagination" v-if="uTotal > uPageSize">
          <van-button class="pop-btn" size="small" :disabled="uPageNum <= 1" @click="uPage(uPageNum - 1)">上一页</van-button>
          <span class="page-info">{{ uPageNum }} / {{ Math.max(1, Math.ceil(uTotal / uPageSize)) }}</span>
          <van-button class="pop-btn" size="small" :disabled="uPageNum >= Math.ceil(uTotal / uPageSize)" @click="uPage(uPageNum + 1)">下一页</van-button>
        </div>
      </van-tab>

      <!-- Tab 4：操作日志 -->
      <van-tab title="操作日志">
        <van-search
          v-model="lQuery.keyword"
          placeholder="搜索操作人 / 详情"
          show-action
          @search="loadLogs"
          @clear="loadLogs"
        >
          <template #action>
            <span class="pop-search-btn" @click="loadLogs">搜索</span>
          </template>
        </van-search>
        <div class="filter-row">
          <span class="filter-label">类型</span>
          <span
            v-for="a in logActionOptions"
            :key="a.value"
            class="pop-chip"
            :class="{ active: lQuery.action === a.value }"
            @click="pickLogAction(a.value)"
          >{{ a.text }}</span>
        </div>

        <van-empty v-if="!lLoading && logs.length === 0" description="暂无操作日志" />
        <div v-else class="pop-list">
          <div v-for="item in logs" :key="item.id" class="pop-card">
            <div class="pop-card-head">
              <span class="pop-card-title">{{ logActionText(item.action) }}</span>
              <span class="pop-tag tag-1">{{ item.detail || '—' }}</span>
            </div>
            <div class="pop-card-meta">
              操作人 {{ item.adminUsername || ('#' + item.adminId) }} · 目标 #{{ item.targetId }}
            </div>
            <div class="pop-card-meta sub">{{ item.createdAt }}</div>
          </div>
        </div>

        <div class="pagination" v-if="lTotal > lPageSize">
          <van-button class="pop-btn" size="small" :disabled="lPageNum <= 1" @click="lPage(lPageNum - 1)">上一页</van-button>
          <span class="page-info">{{ lPageNum }} / {{ Math.max(1, Math.ceil(lTotal / lPageSize)) }}</span>
          <van-button class="pop-btn" size="small" :disabled="lPageNum >= Math.ceil(lTotal / lPageSize)" @click="lPage(lPageNum + 1)">下一页</van-button>
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import * as echarts from 'echarts'
import {
  getAdminGrouponList,
  adminCancelGroupon,
  getAdminUserList,
  banUser,
  unbanUser,
  getDashboard,
  getAdminLogList
} from '../api/admin.js'
import '../styles/pop-art.css'

const activeTab = ref(0)

// ---------- 看板 ----------
const dashboard = ref({})
const pieRef = ref(null)
const barRef = ref(null)
let pieChart = null
let barChart = null
let chartInited = false

const STATUS_TEXT = ['招募中', '已成团', '已结束', '已取消']
const STATUS_COLOR = ['#00bfff', '#07c160', '#969799', '#ff0506']
const POP_COLORS = ['#ffdd00', '#ff69b4', '#00bfff', '#a486ff', '#ff0506']

function statusText(s) {
  return STATUS_TEXT[s] || '未知'
}

async function loadDashboard() {
  try {
    const res = await getDashboard()
    dashboard.value = res.data
    if (chartInited) {
      renderCharts(res.data)
    }
  } catch (e) {
    showToast(e.message || '看板加载失败')
  }
}

function renderCharts(data) {
  if (!pieChart || !barChart) return
  const stats = data.statusStats || []
  const statusData = stats.map((s) => ({
    name: statusText(s.status),
    value: s.count
  }))

  pieChart.setOption({
    color: STATUS_COLOR,
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { fontWeight: 'bold', color: '#000' }
    },
    series: [{
      type: 'pie',
      radius: ['38%', '66%'],
      center: ['50%', '44%'],
      itemStyle: {
        borderColor: '#000',
        borderWidth: 2
      },
      label: {
        fontWeight: 'bold',
        color: '#000',
        formatter: '{b}\n{c}'
      },
      data: statusData
    }]
  })

  const cats = data.topCategories || []
  const catNames = cats.map((c) => c.categoryTag)
  const catCounts = cats.map((c) => c.count)

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 8, right: 16, top: 12, bottom: 30, containLabel: true },
    xAxis: {
      type: 'category',
      data: catNames,
      axisLabel: { fontWeight: 'bold', color: '#000' },
      axisLine: { lineStyle: { color: '#000', width: 2 } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontWeight: 'bold', color: '#000' },
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.12)' } }
    },
    series: [{
      type: 'bar',
      data: catCounts.map((v, i) => ({
        value: v,
        itemStyle: {
          color: POP_COLORS[i % POP_COLORS.length],
          borderColor: '#000',
          borderWidth: 2
        }
      })),
      barWidth: 26,
      label: {
        show: true,
        position: 'top',
        fontWeight: 'bold',
        color: '#000'
      }
    }]
  })
}

function initCharts() {
  if (chartInited) return
  if (!pieRef.value || !barRef.value) return
  pieChart = echarts.init(pieRef.value)
  barChart = echarts.init(barRef.value)
  chartInited = true
  renderCharts(dashboard.value)
}

function onResize() {
  pieChart && pieChart.resize()
  barChart && barChart.resize()
}

// ---------- 拼单管理 ----------
const groupons = ref([])
const gLoading = ref(false)
const gTotal = ref(0)
const gPageNum = ref(1)
const gPageSize = ref(10)
const gQuery = ref({ keyword: '', status: '' })

const statusOptions = [
  { text: '全部', value: '' },
  { text: '招募中', value: 0 },
  { text: '已成团', value: 1 },
  { text: '已结束', value: 2 },
  { text: '已取消', value: 3 }
]

async function loadGroupons() {
  gLoading.value = true
  try {
    const res = await getAdminGrouponList({
      pageNum: gPageNum.value,
      pageSize: gPageSize.value,
      keyword: gQuery.value.keyword || undefined,
      status: gQuery.value.status === '' ? undefined : gQuery.value.status
    })
    groupons.value = res.data.list
    gTotal.value = res.data.total
  } catch (e) {
    showToast(e.message || '加载失败')
  } finally {
    gLoading.value = false
  }
}

function pickStatus(v) {
  gQuery.value.status = v
  gPageNum.value = 1
  loadGroupons()
}

function gPage(p) {
  gPageNum.value = p
  loadGroupons()
}

async function handleCancel(item) {
  try {
    await showConfirmDialog({
      title: '强制下架',
      message: `确定下架「${item.title}」吗？待确认申请将被拒绝。`
    })
  } catch (e) {
    return
  }
  try {
    await adminCancelGroupon(item.id)
    showToast('已下架')
    loadGroupons()
    loadDashboard() // 看板数据同步刷新
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

// ---------- 用户管理 ----------
const users = ref([])
const uLoading = ref(false)
const uTotal = ref(0)
const uPageNum = ref(1)
const uPageSize = ref(10)
const uQuery = ref({ keyword: '' })

async function loadUsers() {
  uLoading.value = true
  try {
    const res = await getAdminUserList({
      pageNum: uPageNum.value,
      pageSize: uPageSize.value,
      keyword: uQuery.value.keyword || undefined
    })
    users.value = res.data.list
    uTotal.value = res.data.total
  } catch (e) {
    showToast(e.message || '加载失败')
  } finally {
    uLoading.value = false
  }
}

function uPage(p) {
  uPageNum.value = p
  loadUsers()
}

async function handleBan(u) {
  try {
    await showConfirmDialog({
      title: '禁用用户',
      message: `确定禁用 @${u.username} 吗？该用户将无法登录。`
    })
  } catch (e) {
    return
  }
  try {
    await banUser(u.id)
    showToast('已禁用')
    loadUsers()
    loadDashboard()
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

async function handleUnban(u) {
  try {
    await unbanUser(u.id)
    showToast('已解除禁用')
    loadUsers()
  } catch (e) {
    showToast(e.message || '操作失败')
  }
}

// ---------- 操作日志 ----------
const logs = ref([])
const lLoading = ref(false)
const lTotal = ref(0)
const lPageNum = ref(1)
const lPageSize = ref(10)
const lQuery = ref({ keyword: '', action: '' })

const logActionOptions = [
  { text: '全部', value: '' },
  { text: '强制下架', value: 'CANCEL_GROUPON' },
  { text: '禁用用户', value: 'BAN_USER' },
  { text: '解除禁用', value: 'UNBAN_USER' }
]
const LOG_ACTION_TEXT = {
  CANCEL_GROUPON: '强制下架拼单',
  BAN_USER: '禁用用户',
  UNBAN_USER: '解除禁用'
}

function logActionText(a) {
  return LOG_ACTION_TEXT[a] || a
}

async function loadLogs() {
  lLoading.value = true
  try {
    const res = await getAdminLogList({
      pageNum: lPageNum.value,
      pageSize: lPageSize.value,
      keyword: lQuery.value.keyword || undefined,
      action: lQuery.value.action || undefined
    })
    logs.value = res.data.list
    lTotal.value = res.data.total
  } catch (e) {
    showToast(e.message || '加载失败')
  } finally {
    lLoading.value = false
  }
}

function pickLogAction(v) {
  lQuery.value.action = v
  lPageNum.value = 1
  loadLogs()
}

function lPage(p) {
  lPageNum.value = p
  loadLogs()
}

// ---------- 切换 Tab ----------
function onTabChange(index) {
  if (index === 0) {
    // 进入看板 Tab：首次进入初始化图表（此时容器已渲染），每次进入刷新数据
    setTimeout(() => {
      initCharts()
      loadDashboard()
    }, 50)
  }
  if (index === 3) {
    loadLogs()
  }
}

onMounted(() => {
  window.addEventListener('resize', onResize)
  // 默认 Tab 就是"数据看板"，onTabChange 不会触发，首次进入直接初始化图表
  setTimeout(() => {
    initCharts()
    loadDashboard()
  }, 100)
  loadGroupons()
  loadUsers()
  loadLogs()
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  pieChart && pieChart.dispose()
  barChart && barChart.dispose()
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  padding-bottom: 40px;
}

/* 统计卡片 */
.stat-cards {
  display: flex;
  gap: 12px;
  padding: 16px;
}
.stat-card {
  flex: 1;
  padding: 16px 12px;
  border: 3px solid #000;
  box-shadow: 4px 4px 0 #000;
  text-align: center;
}
.sc-yellow { background: #ffdd00; }
.sc-pink { background: #ff69b4; }
.stat-num {
  font-family: Impact, 'Arial Black', 'SimHei', sans-serif;
  font-size: 34px;
  font-weight: 900;
  color: #000;
  line-height: 1;
}
.stat-label {
  margin-top: 6px;
  font-weight: 800;
  font-size: 13px;
  color: #000;
}

/* 图表卡片 */
.chart-card {
  margin: 0 16px 16px;
  border: 3px solid #000;
  box-shadow: 4px 4px 0 #000;
  background: #fff;
}
.chart-title {
  padding: 10px 14px;
  border-bottom: 2px solid #000;
  background: #00bfff;
  font-family: Impact, 'Arial Black', 'SimHei', sans-serif;
  font-weight: 800;
  font-size: 15px;
  letter-spacing: 0.06em;
}
.chart-box {
  width: 100%;
  height: 280px;
}

/* 筛选行 */
.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 16px 12px;
  align-items: center;
}
.filter-label {
  font-weight: 800;
  font-size: 13px;
}
.pop-chip {
  padding: 4px 12px;
  border: 2px solid #000;
  border-radius: 6px;
  background: #fff;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 2px 2px 0 #000;
}
.pop-chip.active {
  background: #ffdd00;
}
.pop-btn.danger {
  margin-top: 10px;
  background: #ff0506;
  color: #fff;
  border: 2px solid #000;
}
.cancelled-label {
  display: inline-block;
  margin-top: 10px;
  font-weight: 800;
  color: #969799;
}
.sub {
  font-size: 11px;
  opacity: 0.65;
}
.user-card .pop-btn {
  margin-top: 10px;
}
.pop-search-btn {
  padding: 4px 14px;
  background: #ffdd00;
  border: 2px solid #000;
  border-radius: 6px;
  font-weight: 800;
  font-size: 13px;
  box-shadow: 2px 2px 0 #000;
}
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 0 20px;
}
.page-info {
  font-weight: 800;
}
</style>
