/**
 * QQBot 管理后台 - 类型定义
 */

/** 通用 API 响应 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页请求参数 */
export interface PageParams {
  page: number
  size: number
}

/** 分页响应数据 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

/** 机器人状态 */
export interface BotStatus {
  /** 是否在线 */
  online: boolean
  /** 今日消息数 */
  todayMessageCount: number
  /** 今日 AI 调用次数 */
  todayAiCallCount: number
  /** 活跃用户数 */
  activeUserCount: number
  /** WebSocket 连接状态 */
  wsConnected: boolean
}

/** 聊天记录 */
export interface ChatRecord {
  id: number
  userId: number
  nickname: string
  role: 'user' | 'assistant'
  content: string
  createTime: string
}

/** AI 角色/人格 */
export interface AiCharacter {
  id: number
  name: string
  description: string
  systemPrompt: string
  createTime: string
}
