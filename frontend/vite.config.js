import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: '0.0.0.0',
    // 允许 natapp 公网域名访问（手机演示扫码用）
    allowedHosts: ['.natappfree.cc'],
    proxy: {
      // 开发期代理到后端，避免跨域
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
