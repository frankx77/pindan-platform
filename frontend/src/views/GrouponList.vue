<template>
  <div class="list-page pop pop-page">
    <!-- 顶部：漫画横幅 -->
    <div class="pop-header">
      <div class="pop-dots"></div>
      <div class="pop-header-inner">
        <span class="pop-bubble">CAMPUS GROUPON</span>
        <h1>拼单大厅</h1>
      </div>
    </div>

    <!-- 顶部搜索栏 -->
    <van-search
      v-model="query.keyword"
      placeholder="搜索拼单标题"
      @search="handleSearch"
      @clear="handleSearch"
    />

    <!-- 分类筛选 -->
    <van-dropdown-menu active-color="#00bfff">
      <van-dropdown-item v-model="query.categoryTag" :options="categoryOptions" @change="handleSearch" />
      <van-dropdown-item v-model="query.status" :options="statusOptions" @change="handleSearch" />
    </van-dropdown-menu>

    <!-- 拼单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-empty v-if="!loading && list.length === 0" description="暂无拼单，去发布一个吧" />
      <div v-else class="pop-list">
        <div
          v-for="item in list"
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

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <van-button class="pop-btn" size="small" :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">上一页</van-button>
        <span class="page-info">{{ pageNum }} / {{ totalPages }}</span>
        <van-button class="pop-btn" size="small" :disabled="pageNum >= totalPages" @click="changePage(pageNum + 1)">下一页</van-button>
      </div>
    </van-pull-refresh>

    <!-- 底部导航由全局 MainTabbar 提供（App.vue） -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getGrouponList, getCategoryList } from '../api/groupon.js'
import '../styles/pop-art.css'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const refreshing = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = ref({ keyword: '', categoryTag: '', status: 0 }) // 默认只看招募中
const categories = ref([])

const categoryOptions = computed(() => [
  { text: '全部分类', value: '' },
  ...categories.value.map((c) => ({ text: c, value: c }))
])

const statusOptions = [
  { text: '全部状态', value: '' },
  { text: '招募中', value: 0 },
  { text: '已成团', value: 1 },
  { text: '已结束', value: 2 },
  { text: '已取消', value: 3 }
]

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

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

async function loadList() {
  loading.value = true
  try {
    const res = await getGrouponList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryTag: query.value.categoryTag || undefined,
      keyword: query.value.keyword || undefined,
      status: query.value.status === '' ? undefined : query.value.status
    })
    list.value = res.data.list
    total.value = res.data.total
  } catch (e) {
    showToast(e.message || '加载失败')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadList()
}

function changePage(page) {
  pageNum.value = page
  loadList()
}

function onRefresh() {
  loadList()
}

function goDetail(id) {
  router.push(`/groupon/detail/${id}`)
}

onMounted(async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (e) {
    // 分类加载失败不阻塞列表
  }
  loadList()
})
</script>

<style scoped>
.list-page {
  min-height: 100vh;
  padding-bottom: 60px;
}
</style>
