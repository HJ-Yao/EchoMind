<script setup lang="ts">
/**
 * 首页/仪表盘
 *
 * 显示机器人运行状态概览，包括：
 * - 在线状态
 * - 今日消息数量
 * - AI 调用次数
 * - 活跃用户数
 */
import { useAppStore } from '@/stores'
import { onMounted, ref } from 'vue'

const appStore = useAppStore()
const loading = ref(false)

/** 统计卡片配置 */
const statCards = [
  {
    title: '今日消息数',
    value: () => appStore.botStatus.todayMessageCount,
    icon: 'ChatDotRound',
    color: '#409eff',
  },
  {
    title: 'AI 调用次数',
    value: () => appStore.botStatus.todayAiCallCount,
    icon: 'Cpu',
    color: '#67c23a',
  },
  {
    title: '活跃用户数',
    value: () => appStore.botStatus.activeUserCount,
    icon: 'User',
    color: '#e6a23c',
  },
]

onMounted(async () => {
  loading.value = true
  await appStore.fetchBotStatus()
  loading.value = false
})
</script>

<template>
  <div class="dashboard-view">
    <h2 class="page-title">首页概览</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col
        v-for="(card, index) in statCards"
        :key="index"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
      >
        <el-card class="stat-card" shadow="hover" v-loading="loading">
          <div class="stat-card-body">
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value() }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 机器人状态面板 -->
    <el-card class="status-panel" shadow="hover">
      <template #header>
        <span>机器人运行状态</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机器人状态">
          <el-tag :type="appStore.botStatus.online ? 'success' : 'danger'">
            {{ appStore.botStatus.online ? '在线' : '离线' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="WebSocket 连接">
          <el-tag :type="appStore.botStatus.wsConnected ? 'success' : 'danger'">
            {{ appStore.botStatus.wsConnected ? '已连接' : '未连接' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="今日消息数">
          {{ appStore.botStatus.todayMessageCount }}
        </el-descriptions-item>
        <el-descriptions-item label="AI 调用次数">
          {{ appStore.botStatus.todayAiCallCount }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard-view {
  max-width: 1200px;
}

.page-title {
  margin-bottom: 24px;
  font-size: 20px;
  font-weight: 600;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-card-body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.status-panel {
  margin-top: 20px;
}
</style>
