import { get, post } from '@/utils/request'
import type { BotStatus, ChatRecord, AiCharacter, PageParams, PageResult } from '@/types'

/**
 * 机器人状态 API
 */

/**
 * 获取机器人当前运行状态
 *
 * @returns 机器人状态信息
 */
export function getBotStatus(): Promise<BotStatus> {
  return get<BotStatus>('/bot/status')
}

/**
 * 聊天记录 API
 */

/**
 * 分页查询聊天记录
 *
 * @param params 分页参数
 * @returns 聊天记录分页数据
 */
export function getChatRecords(params: PageParams): Promise<PageResult<ChatRecord>> {
  return get<PageResult<ChatRecord>>('/chat/records', params as unknown as Record<string, unknown>)
}

/**
 * AI 角色 API
 */

/**
 * 获取所有 AI 角色列表
 *
 * @returns AI 角色列表
 */
export function getCharacters(): Promise<AiCharacter[]> {
  return get<AiCharacter[]>('/character/list')
}

/**
 * 创建 AI 角色
 *
 * @param data 角色数据
 * @returns 创建的角色
 */
export function createCharacter(data: Record<string, unknown>): Promise<unknown> {
  return post<unknown>('/character/create', data)
}

/**
 * 删除 AI 角色
 *
 * @param id 角色 ID
 */
export function deleteCharacter(id: number): Promise<unknown> {
  return post<unknown>(`/character/delete?id=${id}`)
}
