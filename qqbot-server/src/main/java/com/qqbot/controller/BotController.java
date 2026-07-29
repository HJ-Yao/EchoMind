package com.qqbot.controller;

import com.qqbot.common.Result;
import com.qqbot.dto.BotStatusDTO;
import com.qqbot.service.BotStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机器人状态 Controller
 *
 * <p>提供机器人运行状态查询接口，供管理后台首页仪表盘使用。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@RestController
@RequestMapping("/bot")
@RequiredArgsConstructor
public class BotController {

    private final BotStatusService botStatusService;

    /**
     * 获取机器人当前运行状态
     *
     * <p>返回 WebSocket 连接状态、今日消息数、AI 调用次数、
     * 活跃用户数、累计统计数据等。</p>
     *
     * @return 机器人状态
     */
    @GetMapping("/status")
    public Result<BotStatusDTO> getStatus() {
        BotStatusDTO status = botStatusService.getStatus();
        return Result.success(status);
    }
}
