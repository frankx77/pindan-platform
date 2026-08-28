<template>
  <div class="detail-page pop pop-page">
    <van-nav-bar title="拼单详情" left-text="返回" left-arrow @click-left="goBack" />

    <van-loading v-if="loading" class="loading" size="24" vertical>加载中...</van-loading>

    <template v-else-if="detail">
      <!-- 状态卡片 -->
      <div class="pop-status-card" :class="'st-' + detail.groupon.status">
        <div class="pop-status-title">{{ detail.groupon.title }}</div>
        <span class="pop-status-tag">{{ statusText(detail.groupon.status) }}</span>
        <div class="pop-status-count">{{ detail.groupon.currentCount }} / {{ detail.groupon.targetCount }} 人</div>
      </div>

      <!-- 基本信息 -->
      <van-cell-group class="pop-group info-group">
        <van-cell title="分类" :value="detail.groupon.categoryTag" />
        <van-cell title="团长" :value="detail.publisher.nickname || detail.publisher.username" />
        <van-cell title="截止时间" :value="detail.groupon.deadline || '不限'" />
        <van-cell title="发布时间" :value="detail.groupon.createdAt" />
        <van-cell v-if="detail.groupon.description" title="详情描述" :label="detail.groupon.description" />
      </van-cell-group>

      <!-- 成员列表 -->
      <van-cell-group class="pop-group member-group">
        <template #title>
          <div class="pop-section-title">已加入成员（{{ detail.memberList.length + 1 }} 人）</div>
        </template>
        <van-cell :title="detail.publisher.nickname || detail.publisher.username" label="团长">
          <template #value>
            <span v-if="detail.groupon.status === 1 && detail.publisher.wechat" class="wechat">微信:{{ detail.publisher.wechat }}</span>
          </template>
        </van-cell>
        <van-cell v-for="m in detail.memberList" :key="m.id" :title="m.nickname || m.username">
          <template #value>
            <span v-if="detail.groupon.status === 1 && m.wechat" class="wechat">微信:{{ m.wechat }}</span>
          </template>
        </van-cell>
        <van-empty v-if="detail.memberList.length === 0" description="还没有成员加入" image-size="60" />
      </van-cell-group>

      <!-- 微信号保密提示（未成团） -->
      <div v-if="detail.groupon.status !== 1" class="pop-tip">
        🔒 拼团成功后即可查看彼此微信号（含团长）
      </div>

      <!-- 团长：待审批申请列表 -->
      <van-cell-group class="pop-group member-group" v-if="detail.isPublisher && detail.groupon.status === 0 && pendingList.length > 0">
        <template #title>
          <div class="pop-section-title">待审批申请（{{ pendingList.length }} 人）</div>
        </template>
        <van-cell v-for="r in pendingList" :key="r.id" :title="r.nickname || r.username" :label="'申请于 ' + (r.createdAt || '')">
          <template #right-icon>
            <div class="approve-actions">
              <van-button size="mini" type="success" @click="handleApprove(r.id)">通过</van-button>
              <van-button size="mini" type="danger" plain @click="handleReject(r.id)">拒绝</van-button>
            </div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 底部操作按钮 -->
      <div class="pop-action-bar">
        <!-- 普通用户：招募中且非团长且未申请过 → 申请加入 -->
        <van-button
          v-if="!detail.isPublisher && detail.groupon.status === 0 && detail.myJoinStatus === null"
          block type="primary" :loading="acting" @click="handleApply"
        >申请加入</van-button>

        <!-- 已申请待确认 -->
        <van-button v-else-if="!detail.isPublisher && detail.myJoinStatus === 0" block disabled>
          已申请，等待团长审批
        </van-button>

        <!-- 已加入成员：招募中可退出 -->
        <van-button
          v-if="!detail.isPublisher && detail.myJoinStatus === 1 && detail.groupon.status === 0"
          block type="warning" :loading="acting" @click="handleQuit"
        >退出拼单</van-button>

        <!-- 团长：招募中可取消 -->
        <van-button
          v-if="detail.isPublisher && detail.groupon.status === 0"
          block type="danger" plain :loading="acting" @click="handleCancel"
        >取消拼单</van-button>

        <!-- 状态提示 -->
        <div class="state-tip" v-if="!detail.isPublisher && detail.myJoinStatus === 2">您已被团长拒绝</div>
        <div class="state-tip" v-if="!detail.isPublisher && detail.myJoinStatus === 3">您已退出该拼单</div>
      </div>
    </template>

    <van-empty v-else description="拼单不存在" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import {
  getGrouponDetail, getGrouponRequests,
  applyGroupon, approveRequest, rejectRequest, quitRequest, cancelGroupon
} from '../api/groupon.js'
import '../styles/pop-art.css'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const acting = ref(false)
const detail = ref(null)
const requests = ref([])

const pendingList = computed(() => requests.value.filter((r) => r.status === 0))

function statusText(status) {
  return ['招募中', '已成团', '已结束', '已取消'][status] || '未知'
}

function statusColor(status) {
  return ['#1989fa', '#07c160', '#969799', '#ee0a24'][status] || '#969799'
}

function goBack() {
  router.back()
}

async function loadDetail() {
  const res = await getGrouponDetail(route.params.id)
  detail.value = res.data
  // 团长加载审批列表
  if (res.data.isPublisher) {
    const r = await getGrouponRequests(route.params.id)
    requests.value = r.data
  }
}

async function handleApply() {
  acting.value = true
  try {
    await applyGroupon(Number(route.params.id))
    showSuccessToast('申请成功，等待团长审批')
    await loadDetail()
  } catch (e) {
    showToast(e.message || '申请失败')
  } finally {
    acting.value = false
  }
}

async function handleApprove(id) {
  acting.value = true
  try {
    await approveRequest(id)
    showSuccessToast('已通过')
    await loadDetail()
  } catch (e) {
    showToast(e.message || '操作失败')
  } finally {
    acting.value = false
  }
}

async function handleReject(id) {
  acting.value = true
  try {
    await rejectRequest(id)
    showToast('已拒绝')
    await loadDetail()
  } catch (e) {
    showToast(e.message || '操作失败')
  } finally {
    acting.value = false
  }
}

async function handleQuit() {
  try {
    await showConfirmDialog({ title: '退出拼单', message: '确定退出该拼单吗？' })
  } catch (e) {
    return // 用户取消
  }
  acting.value = true
  try {
    await quitRequest(detail.value.myRequestId)
    showSuccessToast('已退出')
    await loadDetail()
  } catch (e) {
    showToast(e.message || '退出失败')
  } finally {
    acting.value = false
  }
}

async function handleCancel() {
  try {
    await showConfirmDialog({ title: '取消拼单', message: '取消后所有待确认申请将自动拒绝，确定吗？' })
  } catch (e) {
    return // 用户取消
  }
  acting.value = true
  try {
    await cancelGroupon(Number(route.params.id))
    showSuccessToast('已取消')
    await loadDetail()
  } catch (e) {
    showToast(e.message || '取消失败')
  } finally {
    acting.value = false
  }
}

onMounted(async () => {
  try {
    await loadDetail()
  } catch (e) {
    showToast(e.message || '加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding-bottom: 100px;
}
.loading {
  margin-top: 80px;
}
.info-group,
.member-group {
  margin-bottom: 4px;
}
.approve-actions {
  display: flex;
  gap: 6px;
}
</style>
