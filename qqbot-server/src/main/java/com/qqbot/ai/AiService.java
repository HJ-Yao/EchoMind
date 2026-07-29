package com.qqbot.ai;

import com.qqbot.common.SystemPromptConstants;
import com.qqbot.service.AiCharacterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI 对话服务
 *
 * <p>封装 SpringAI ChatClient，提供与百炼大模型（qwen3.7-max）的对话能力。
 * 支持从数据库加载 AI 角色（人格模拟）的 System Prompt，实现不同风格的回复。</p>
 *
 * <p>MVP 阶段使用无状态对话模式，每次请求独立调用 AI。
 * 后续版本将引入 Memory 和 RAG 增强（基于 pgvector）。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
public class AiService {

    private final ChatClient chatClient;

    /**
     * AI 角色服务（延迟注入，避免循环依赖）
     */
    @Autowired(required = false)
    private AiCharacterService aiCharacterService;

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
     * 优先从数据库加载默认角色的 System Prompt，如果数据库未配置则使用内置默认 Prompt。</p>
     *
     * @param userId      用户 ID（用于后续扩展 Memory 功能）
     * @param userMessage 用户消息文本
     * @return AI 回复文本，如果调用失败返回 null
     */
    public String chat(Long userId, String userMessage) {
        try {
            log.info("AI 对话请求: userId={}, message={}", userId, userMessage);

            String systemPrompt = getSystemPrompt();

            String reply = chatClient.prompt()
                    .system(systemPrompt)
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

    /**
     * 获取当前生效的 System Prompt
     *
     * <p>优先从 AiCharacterService 获取数据库配置的默认角色 Prompt，
     * 如果服务不可用或未配置，fallback 到内置默认 Prompt。</p>
     *
     * @return System Prompt 文本
     */
    private String getSystemPrompt() {
        if (aiCharacterService != null) {
            try {
                return aiCharacterService.getDefaultSystemPrompt();
            } catch (Exception e) {
                log.warn("获取数据库 System Prompt 失败，使用内置默认 Prompt: {}", e.getMessage());
            }
        }
        return SystemPromptConstants.DEFAULT_SYSTEM_PROMPT;
    }
}
