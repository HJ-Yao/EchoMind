<script setup lang="ts">
/**
 * 主布局容器
 *
 * 提供管理后台的整体布局结构，包含：
 * - 顶部导航栏
 * - 左侧菜单栏
 * - 右侧内容区域
 */
import { useAppStore } from '@/stores'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

/** 菜单项配置 */
const menuItems = [
  { path: '/dashboard', title: '首页', icon: 'HomeFilled' },
  { path: '/chat', title: '聊天记录', icon: 'ChatDotRound' },
  { path: '/character', title: 'AI 角色', icon: 'UserFilled' },
  { path: '/settings', title: '系统设置', icon: 'Setting' },
]

/** 当前激活的菜单项 */
function isActive(path: string): boolean {
  return route.path === path || route.path.startsWith(path + '/')
}

/** 导航到指定页面 */
function navigateTo(path: string) {
  router.push(path)
}
</script>

<template>
  <el-container class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="main-header">
      <div class="header-left">
        <span class="header-title">QQBot AI Agent 管理后台</span>
      </div>
      <div class="header-right">
        <el-tag :type="appStore.botStatus.wsConnected ? 'success' : 'danger'" size="small">
          {{ appStore.botStatus.wsConnected ? '已连接' : '未连接' }}
        </el-tag>
      </div>
    </el-header>

    <el-container class="main-body">
      <!-- 左侧菜单栏 -->
      <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="main-aside">
        <el-menu
          :default-active="route.path"
          :collapse="appStore.sidebarCollapsed"
          :collapse-transition="false"
          router
          class="aside-menu"
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.path"
            :index="item.path"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-header {
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  flex-shrink: 0;
}

.header-title {
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.main-body {
  flex: 1;
  overflow: hidden;
}

.main-aside {
  background-color: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
}

.aside-menu {
  border-right: none;
}

.main-content {
  background-color: #ffffff;
  padding: 24px;
  overflow-y: auto;
}
</style>
