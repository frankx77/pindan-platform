import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/groupon/list'
  },
  {
    path: '/login',
    name: 'Login',
    // 路由懒加载
    component: () => import('../views/Login.vue'),
    meta: { hideTab: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { hideTab: true }
  },
  {
    path: '/groupon/list',
    name: 'GrouponList',
    component: () => import('../views/GrouponList.vue'),
    meta: { tab: 'list' }
  },
  {
    path: '/message',
    name: 'MessageList',
    component: () => import('../views/MessageList.vue'),
    meta: { tab: 'message' }
  },
  {
    path: '/message/assistant',
    name: 'Assistant',
    component: () => import('../views/Assistant.vue'),
    meta: { hideTab: true }
  },
  {
    path: '/groupon/publish',
    name: 'GrouponPublish',
    component: () => import('../views/GrouponPublish.vue'),
    meta: { tab: 'publish' }
  },
  {
    path: '/groupon/detail/:id',
    name: 'GrouponDetail',
    component: () => import('../views/GrouponDetail.vue'),
    meta: { hideTab: true }
  },
  {
    path: '/groupon/my/published',
    name: 'MyPublished',
    component: () => import('../views/MyPublished.vue'),
    meta: { tab: 'mygroupon' }
  },
  {
    path: '/my',
    name: 'MyCenter',
    component: () => import('../views/MyCenter.vue'),
    meta: { tab: 'my' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { hideTab: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
