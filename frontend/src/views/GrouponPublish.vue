<template>
  <div class="publish-page pop pop-page">
    <van-nav-bar title="发布拼单" left-text="返回" left-arrow @click-left="goBack" />

    <van-form @submit="handleSubmit">
      <van-cell-group class="pop-group">
        <van-field
          v-model="form.title"
          name="title"
          label="标题"
          placeholder="如：拼奶茶，3人成团"
          maxlength="100"
          :rules="[{ required: true, message: '请输入标题' }]"
        />
        <van-field name="categoryTag" label="分类">
          <template #input>
            <van-dropdown-menu>
              <van-dropdown-item v-model="form.categoryTag" :options="categoryOptions" />
            </van-dropdown-menu>
          </template>
        </van-field>
        <van-field
          v-model="form.targetCount"
          type="number"
          name="targetCount"
          label="目标人数"
          placeholder="含团长，最少 2 人"
          :rules="[
            { required: true, message: '请输入目标人数' },
            { validator: (v) => Number(v) >= 2, message: '目标人数至少为 2' }
          ]"
        />
        <van-field
          v-model="form.deadline"
          is-link
          readonly
          name="deadline"
          label="截止时间"
          placeholder="请选择截止时间"
          :rules="[{ required: true, message: '请选择截止时间' }]"
          @click="showPicker = true"
        />
        <van-field
          v-model="form.description"
          name="description"
          label="描述"
          type="textarea"
          rows="3"
          autosize
          placeholder="补充拼单详情：时间、地点、费用 AA 方式等"
        />
      </van-cell-group>

      <div class="btn-area">
        <van-button block type="primary" native-type="submit" :loading="loading">
          发布
        </van-button>
      </div>
    </van-form>

    <!-- 截止时间选择弹窗：日期 + 时间组合（Vant4 已移除 datetime-picker） -->
    <van-popup v-model:show="showPicker" position="bottom" class="pop-picker">
      <div class="picker-box">
        <div class="picker-title">选择截止时间</div>
        <van-date-picker
          v-model="dateValues"
          :min-date="minDate"
          :max-date="maxDate"
          :show-toolbar="false"
        />
        <van-time-picker v-model="timeValues" :show-toolbar="false" />
        <div class="picker-footer">
          <van-button size="small" @click="showPicker = false">取消</van-button>
          <van-button size="small" type="primary" @click="onPickerConfirm">确定</van-button>
        </div>
      </div>
    </van-popup>

    <!-- 底部导航由全局 MainTabbar 提供（App.vue） -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { publishGroupon, getCategoryList } from '../api/groupon.js'
import '../styles/pop-art.css'

const router = useRouter()
const loading = ref(false)
const showPicker = ref(false)
const categories = ref([])

const form = ref({
  title: '',
  categoryTag: '',
  targetCount: '',
  description: '',
  deadline: null
})

const minDate = new Date()
const maxDate = new Date()
maxDate.setFullYear(maxDate.getFullYear() + 1)

// Vant4 date-picker 的 modelValue 是字符串数组 ['2026','08','26']
function todayValues() {
  const d = new Date()
  return [String(d.getFullYear()), String(d.getMonth() + 1).padStart(2, '0'), String(d.getDate()).padStart(2, '0')]
}
// Vant4 time-picker 的 modelValue 是 ['12','30']
function nowTimeValues() {
  const d = new Date()
  return [String(d.getHours()).padStart(2, '0'), String(d.getMinutes()).padStart(2, '0')]
}

const dateValues = ref(todayValues())
const timeValues = ref(nowTimeValues())

function onPickerConfirm() {
  const [y, m, d] = dateValues.value
  const [hh, mm] = timeValues.value
  form.value.deadline = `${y}-${m}-${d} ${hh}:${mm}:00`
  showPicker.value = false
}

const categoryOptions = computed(() =>
  categories.value.map((c) => ({ text: c, value: c }))
)

async function handleSubmit() {
  if (!form.value.categoryTag) {
    showToast('请选择分类')
    return
  }
  loading.value = true
  try {
    await publishGroupon({
      title: form.value.title,
      categoryTag: form.value.categoryTag,
      targetCount: Number(form.value.targetCount),
      description: form.value.description || null,
      deadline: form.value.deadline
    })
    showSuccessToast('发布成功')
    router.push('/groupon/list')
  } catch (e) {
    showToast(e.message || '发布失败')
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/groupon/list')
}

onMounted(async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
    if (res.data.length > 0) {
      form.value.categoryTag = res.data[0]
    }
  } catch (e) {
    // 忽略
  }
})
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  padding-bottom: 60px;
}
.btn-area {
  margin: 30px 16px;
}
.picker-box {
  padding: 16px 0 20px;
}
.picker-footer {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 8px;
}
</style>
