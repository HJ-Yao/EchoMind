package com.qqbot.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI 对话服务
 *
 * <p>封装 SpringAI ChatClient，提供与百炼大模型（通义千问）的对话能力。
 * 使用预设的 System Prompt 控制机器人的回复风格，确保回复自然、简洁。</p>
 *
 * <p>MVP 阶段使用简单的无状态对话模式，每次请求独立调用 AI，
 * 不保留历史上下文（后续版本将引入 Memory 和 RAG 增强）。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
public class AiService {

    /**
     * 系统 Prompt - 定义机器人的回复风格和行为准则
     *
     * <p>设计原则：
     * <ul>
     *   <li>回复简洁自然，像真人聊天</li>
     *   <li>不暴露内部实现细节</li>
     *   <li>避免机械化回答</li>
     *   <li>友好、乐于助人</li>
     * </ul></p>
     */
    private static final String SYSTEM_PROMPT = """
            你是一个QQ聊天机器人。

            你的任务：
            自然回复用户。

            要求：
            1. 回复简洁
            2. 像真人聊天
            3. 不暴露自己内部实现
            4. 避免机械化回答
            """;

    private final ChatClient chatClient;

    /**
     * 构造函数注入
     *
     * @param chatClient SpringAI ChatClient
     */
    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 同步对话
     *
     * <p>向 AI 模型发送用户消息，获取文本回复。
     * 使用系统 Prompt 约束回复风格，确保输出符合机器人定位。</p>
     *
     * @param userId      用户 ID（用于后续扩展 Memory 功能）
     * @param userMessage 用户消息文本
     * @return AI 回复文本，如果调用失败返回 null
     */
    public String chat(Long userId, String userMessage) {
        try {
            log.info("AI 对话请求: userId={}, message={}", userId, userMessage);

            String reply = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .content();

            log.info("AI 对话回复: userId={}, reply={}", userId, reply);
            return reply;

        } catch (Exception e) {
            log.error("AI 调用失败: userId={}, message={}", userId, userMessage, e);
            return null;
        }
    }
}
