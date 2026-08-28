<template>
  <div class="register-page pop pop-page pop-auth">
    <div class="pop-auth-header">
      <span class="pop-bubble">JOIN US!</span>
      <h1>注册账号</h1>
      <p>加入校园拼单社区</p>
    </div>

    <van-form @submit="handleRegister">
      <van-cell-group class="pop-group">
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="3~20 个字符"
          :rules="[{ required: true, message: '请输入用户名' }, { validator: checkUsername, message: '用户名需 3~20 个字符' }]"
        />
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="6~30 个字符"
          :rules="[{ required: true, message: '请输入密码' }, { validator: checkPassword, message: '密码需 6~30 个字符' }]"
        />
        <van-field
          v-model="form.confirmPassword"
          type="password"
          name="confirmPassword"
          label="确认密码"
          placeholder="再次输入密码"
          :rules="[{ validator: checkConfirm, message: '两次密码不一致' }]"
        />
        <van-field
          v-model="form.wechat"
          name="wechat"
          label="微信号"
          placeholder="拼团成功后互相联系用"
          :rules="[{ required: true, message: '请填写微信号' }]"
        />
      </van-cell-group>

      <div class="btn-area">
        <van-button block type="primary" native-type="submit" :loading="loading">
          注册
        </van-button>
        <van-button block type="primary" plain class="back-btn" @click="goBack">
          已有账号，去登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register } from '../api/user.js'
import '../styles/pop-art.css'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '', confirmPassword: '', wechat: '' })

const checkUsername = (val) => val.length >= 3 && val.length <= 20
const checkPassword = (val) => val.length >= 6 && val.length <= 30
const checkConfirm = (val) => val === form.value.password

async function handleRegister() {
  loading.value = true
  try {
    const res = await register({
      username: form.value.username,
      password: form.value.password,
      wechat: form.value.wechat
    })
    showToast('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    showToast(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  padding-top: 5vh;
}
</style>
