package com.qqbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * <p>为 QQ 消息处理和 AI 调用提供异步执行能力，避免阻塞 WebSocket 连接线程。
 * 使用 CallerRunsPolicy 拒绝策略，在队列满时由调用线程执行，保证消息不丢失。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 4;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 16;

    /**
     * 空闲线程存活时间（秒）
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * 任务队列容量
     */
    private static final int QUEUE_CAPACITY = 500;

    /**
     * 创建消息处理线程池
     *
     * <p>用于异步处理接收到的 QQ 消息，包括 AI 调用和回复发送。
     * 线程池参数根据 QQ 机器人消息量设计，MVP 阶段使用较小配置。</p>
     *
     * @return ThreadPoolExecutor 实例
     */
    @Bean("messageExecutor")
    public ThreadPoolExecutor messageExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        log.info("消息处理线程池已初始化: core={}, max={}, queue={}",
                CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY);
        return executor;
    }
}
