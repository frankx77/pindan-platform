<template>
  <div class="login-page pop pop-page pop-auth">
    <div class="pop-auth-header">
      <span class="pop-bubble">WELCOME BACK!</span>
      <h1>校园拼单平台</h1>
      <p>发布拼单 · 找队友 · 一起省</p>
    </div>

    <van-form @submit="handleLogin">
      <van-cell-group class="pop-group">
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
      </van-cell-group>

      <div class="btn-area">
        <van-button block type="primary" native-type="submit" :loading="loading">
          登录
        </van-button>
        <van-button block type="primary" plain class="register-btn" @click="goRegister">
          注册新账号
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login } from '../api/user.js'
import '../styles/pop-art.css'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form.value)
    // 存 token + 用户信息
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    showToast('登录成功')
    router.push('/groupon/list')
  } catch (e) {
    showToast(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding-top: 6vh;
}
</style>
