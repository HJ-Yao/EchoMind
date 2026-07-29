import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

/**
 * QQBot 管理后台 - 路由配置
 *
 * 定义应用的页面路由结构，包含：
 * - 主布局容器（MainLayout）及其子路由
 * - 各功能模块页面路由
 */
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: {
          title: '首页',
          icon: 'HomeFilled',
        },
      },
      {
        path: 'chat',
        name: 'ChatHistory',
        component: () => import('@/views/chat/ChatHistoryView.vue'),
        meta: {
          title: '聊天记录',
          icon: 'ChatDotRound',
        },
      },
      {
        path: 'character',
        name: 'Character',
        component: () => import('@/views/character/CharacterView.vue'),
        meta: {
          title: 'AI 角色管理',
          icon: 'UserFilled',
        },
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/SettingsView.vue'),
        meta: {
          title: '系统设置',
          icon: 'Setting',
        },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
