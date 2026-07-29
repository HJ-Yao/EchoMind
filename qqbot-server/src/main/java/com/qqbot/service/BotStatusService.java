package com.qqbot.service;

import com.qqbot.dto.BotStatusDTO;
import com.qqbot.repository.MessageRecordRepository;
import com.qqbot.repository.QqUserRepository;
import com.qqbot.websocket.OneBotWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 机器人状态服务
 *
 * <p>聚合机器人运行时的各项统计指标，包括在线状态、消息量、用户数等。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotStatusService {

    private final OneBotWebSocketClient wsClient;
    private final MessageRecordRepository messageRecordRepository;
    private final QqUserRepository qqUserRepository;

    /**
     * 获取机器人当前运行状态
     *
     * <p>统计项包括：WebSocket 连接状态、今日消息数、AI 调用次数、
     * 活跃用户数、累计消息/用户总数。</p>
     *
     * @return 机器人状态 DTO
     */
    public BotStatusDTO getStatus() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        long todayMessages = messageRecordRepository.countTodayMessages(startOfDay, endOfDay);
        long todayAiCalls = messageRecordRepository.countTodayAiCalls(startOfDay, endOfDay);
        long todayActiveUsers = messageRecordRepository.countTodayActiveUsers(startOfDay, endOfDay);
        long totalMessages = messageRecordRepository.count();
        long totalUsers = qqUserRepository.count();

        return BotStatusDTO.builder()
                .online(true)
                .wsConnected(wsClient.isConnected())
                .todayMessageCount(todayMessages)
                .todayAiCallCount(todayAiCalls)
                .activeUserCount(todayActiveUsers)
                .totalMessageCount(totalMessages)
                .totalUserCount(totalUsers)
                .build();
    }
}
