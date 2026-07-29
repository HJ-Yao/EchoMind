import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { BotStatus } from '@/types'
import { getBotStatus } from '@/api/bot'

/**
 * 全局应用状态管理
 *
 * 管理机器人运行状态、全局配置等跨组件共享数据。
 */
export const useAppStore = defineStore('app', () => {
  /** 侧边栏是否折叠 */
  const sidebarCollapsed = ref(false)

  /** 机器人状态 */
  const botStatus = ref<BotStatus>({
    online: false,
    todayMessageCount: 0,
    todayAiCallCount: 0,
    activeUserCount: 0,
    wsConnected: false,
  })

  /** 是否正在加载机器人状态 */
  const statusLoading = ref(false)

  /**
   * 切换侧边栏折叠状态
   */
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  /**
   * 获取并更新机器人状态
   */
  async function fetchBotStatus() {
    statusLoading.value = true
    try {
      const data = await getBotStatus()
      if (data) {
        botStatus.value = data
      }
    } catch (error) {
      console.error('获取机器人状态失败:', error)
    } finally {
      statusLoading.value = false
    }
  }

  return {
    sidebarCollapsed,
    botStatus,
    statusLoading,
    toggleSidebar,
    fetchBotStatus,
  }
})
