package com.qqbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringAI 配置类
 *
 * <p>配置阿里云百炼平台的 AI 模型连接，通过 OpenAI 兼容接口与百炼 MaaS 对接。
 * 百炼 MaaS 提供与 OpenAI API 兼容的接口端点，因此复用 Spring AI 的 OpenAI Starter。</p>
 *
 * <p>百炼 MaaS 专属端点：ws-ejqzyf5azzcyjs4i.cn-beijing.maas.aliyuncs.com
 * OpenAI 兼容路径：/compatible-mode/v1</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Configuration
public class AiConfig {

    /**
     * 百炼 API Key
     */
    @Value("${qqbot.ai.dashscope.api-key}")
    private String apiKey;

    /**
     * 百炼 OpenAI 兼容端点 Base URL
     */
    @Value("${qqbot.ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    /**
     * 模型名称（如 qwen3.7-max, qwen-plus）
     */
    @Value("${qqbot.ai.dashscope.model:qwen3.7-max}")
    private String model;

    /**
     * 温度参数，控制回复的随机性
     */
    @Value("${qqbot.ai.dashscope.temperature:0.7}")
    private Double temperature;

    /**
     * 最大 Token 数
     */
    @Value("${qqbot.ai.dashscope.max-tokens:4096}")
    private Integer maxTokens;

    /**
     * 创建 OpenAiApi 实例，指向百炼 MaaS OpenAI 兼容端点
     *
     * <p>使用 SpringAI 1.0.0 的 Builder 模式构建 OpenAiApi，
     * 自动处理 API Key 和 Base URL 的配置。</p>
     *
     * @return 配置好的 OpenAiApi 实例
     */
    @Bean
    public OpenAiApi dashScopeApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    /**
     * 创建 OpenAiChatModel，封装百炼聊天模型
     *
     * @param dashScopeApi 百炼 API 客户端
     * @return 配置好的 OpenAiChatModel 实例
     */
    @Bean
    public OpenAiChatModel dashScopeChatModel(OpenAiApi dashScopeApi) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(dashScopeApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * 创建 ChatClient，提供流式/非流式对话能力
     *
     * @param chatModel 聊天模型
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
