<template>
  <div class="assistant-page pop pop-page">
    <van-nav-bar title="拼团小助手" left-text="返回" left-arrow @click-left="goBack" />

    <!-- 更新日志 -->
    <van-cell-group class="pop-group">
      <template #title>
        <div class="pop-section-title">📢 更新日志</div>
      </template>
      <div class="pop-log-item" v-for="log in logs" :key="log.version">
        <div class="pop-log-head">
          <span class="pop-log-version">{{ log.version }}</span>
          <span class="pop-log-date">{{ log.date }}</span>
        </div>
        <ul>
          <li v-for="(item, i) in log.items" :key="i">{{ item }}</li>
        </ul>
      </div>
    </van-cell-group>

    <!-- 基本规则 -->
    <van-cell-group class="pop-group">
      <template #title>
        <div class="pop-section-title">📋 基本规则</div>
      </template>
      <van-cell
        v-for="rule in rules"
        :key="rule.title"
        :title="rule.title"
        :label="rule.desc"
      />
    </van-cell-group>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import '../styles/pop-art.css'

const router = useRouter()

const logs = [
  {
    version: 'v0.4',
    date: '2026-08-25',
    items: [
      '新增「消息」中心：团长及时收到新成员申请提醒，点击直达审批；申请结果一键通知',
      '新增「拼团小助手」：更新日志与使用规则，帮助大家用得明白、拼得放心',
      '导航升级为五个入口：拼团、消息、发布、我的拼团、我的',
      '「发布」按钮醒目强调，一键发起拼单'
    ]
  },
  {
    version: 'v0.3',
    date: '2026-08-25',
    items: [
      '拼团成功后，团长与成员可互相查看微信号，线下对接更顺畅',
      '微信号全程保密，未成团的拼单不展示任何联系方式'
    ]
  },
  {
    version: 'v0.2',
    date: '2026-08-25',
    items: [
      '支持完整拼团流程：发布 → 申请 → 团长审批 → 满员自动成团',
      '成员可随时退出，团长可取消拼单，超时自动结束',
      '首页默认展示「招募中」的拼单，界面清爽'
    ]
  },
  {
    version: 'v0.1',
    date: '2026-08-25',
    items: [
      '账号注册/登录，支持发布与浏览拼单',
      '拼单分类、关键词搜索，快速找到想要的团'
    ]
  }
]

const rules = [
  {
    title: '平台不涉及任何资金交易',
    desc: '拼单费用请在线下自行协商 AA，平台仅提供组局撮合，不代收、不代付任何款项。'
  },
  {
    title: '成团后及时沟通',
    desc: '拼团成功后请尽快互加微信确认细节（时间、地点、费用分摊方式），避免临时变动造成误会。'
  },
  {
    title: '变动请及时告知',
    desc: '如无法按约参与，请第一时间在群内说明并联系团长协调，不要无故缺席或失联。'
  },
  {
    title: '文明用语，互相尊重',
    desc: '在拼单详情、申请与沟通中请保持友善，严禁辱骂、骚扰等不文明行为。'
  },
  {
    title: '违规内容会被下架',
    desc: '涉及违法违规、虚假信息、恶意刷单的拼单，管理员有权强制下架，并视情节禁用账号。'
  }
]

function goBack() {
  router.back()
}
</script>

<style scoped>
.assistant-page {
  min-height: 100vh;
  padding-bottom: 20px;
}
</style>
