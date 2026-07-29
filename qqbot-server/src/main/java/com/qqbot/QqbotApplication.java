package com.qqbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * QQBot AI Agent 平台 - 主启动类
 *
 * <p>基于 SpringBoot + SpringAI + NapCat/OneBot11 构建的 QQ 机器人 AI Agent 平台。
 * MVP 阶段实现核心链路：QQ消息 → WebSocket接收 → SpringAI(百炼) → OneBot API → QQ回复。</p>
 *
 * @author QQbot Team
 * @version 1.0.0
 * @since 2026-07-29
 */
@EnableAsync
@SpringBootApplication
public class QqbotApplication {

    /**
     * 应用主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(QqbotApplication.class, args);
    }
}
