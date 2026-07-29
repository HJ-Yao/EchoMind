package com.qqbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 机器人状态 DTO
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否在线 */
    private Boolean online;

    /** 今日消息数量 */
    private Long todayMessageCount;

    /** 今日 AI 调用次数 */
    private Long todayAiCallCount;

    /** 今日活跃用户数 */
    private Long activeUserCount;

    /** WebSocket 是否已连接 */
    private Boolean wsConnected;

    /** 总消息数 */
    private Long totalMessageCount;

    /** 总用户数 */
    private Long totalUserCount;
}
