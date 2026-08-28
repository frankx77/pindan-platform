import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vant 4 全量引入（课程设计规模，全量引入更省心）
import Vant from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)
app.use(router)
app.use(Vant)
app.mount('#app')
