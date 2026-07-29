package com.qqbot.common;

/**
 * System Prompt 常量
 *
 * <p>定义内置的默认 System Prompt，供 AiService 和 AiCharacterService 共同引用，
 * 避免循环依赖。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
public final class SystemPromptConstants {

    /**
     * 内置默认 System Prompt（当数据库中没有配置默认角色时使用）
     *
     * <p>设计原则：
     * <ul>
     *   <li>回复简洁自然，像真人聊天</li>
     *   <li>不暴露内部实现细节</li>
     *   <li>避免机械化回答</li>
     *   <li>友好、乐于助人</li>
     * </ul></p>
     */
    public static final String DEFAULT_SYSTEM_PROMPT = """
            你是一个QQ聊天机器人。

            你的任务：
            自然回复用户。

            要求：
            1. 回复简洁
            2. 像真人聊天
            3. 不暴露自己内部实现
            4. 避免机械化回答
            """;

    private SystemPromptConstants() {
        // 工具类，禁止实例化
    }
}
