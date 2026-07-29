package com.qqbot.bot;

import com.qqbot.ai.AiService;
import com.qqbot.onebot.OneBotApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * QQ 消息处理器
 *
 * <p>负责接收从 WebSocket 层传入的 QQ 消息，协调 AI 服务和 OneBot API
 * 完成消息回复。支持私聊和群聊两种消息类型。</p>
 *
 * <p>处理流程：
 * <ol>
 *   <li>接收消息（message_type、user_id、message 等）</li>
 *   <li>调用 {@link AiService} 获取 AI 回复</li>
 *   <li>通过 {@link OneBotApi} 将回复发送回 QQ</li>
 * </ol></p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final AiService aiService;
    private final OneBotApi oneBotApi;

    /**
     * 处理 QQ 消息（异步执行）
     *
     * <p>根据消息类型（私聊/群聊）分别调用对应的处理方法。
     * 使用 {@link Async} 注解确保不阻塞 WebSocket 消息接收线程。</p>
     *
     * @param messageType 消息类型：private（私聊）或 group（群聊）
     * @param userId      发送者 QQ 号
     * @param message     消息文本内容
     * @param groupId     群号（仅群聊时有值）
     * @param selfId      机器人自身 QQ 号
     */
    @Async("messageExecutor")
    public void handleMessage(String messageType, Long userId, String message,
                              Long groupId, Long selfId) {
        try {
            log.info("收到消息: type={}, userId={}, groupId={}, message={}",
                    messageType, userId, groupId, message);

            if ("private".equals(messageType)) {
                handlePrivateMessage(userId, message);
            } else if ("group".equals(messageType)) {
                handleGroupMessage(groupId, userId, message);
            } else {
                log.warn("未知消息类型: {}", messageType);
            }
        } catch (Exception e) {
            log.error("处理消息异常: userId={}, message={}", userId, message, e);
        }
    }

    /**
     * 处理私聊消息
     *
     * <p>直接调用 AI 获取回复，将结果发送给对应用户。
     * 如果 AI 调用失败或返回空内容，发送预设的降级回复。</p>
     *
     * @param userId  发送者 QQ 号
     * @param message 消息内容
     */
    private void handlePrivateMessage(Long userId, String message) {
        String reply = aiService.chat(userId, message);
        if (reply == null || reply.isBlank()) {
            reply = getFallbackReply();
        }
        oneBotApi.sendPrivateMessage(userId, reply);
        log.info("私聊回复: userId={}, reply={}", userId, reply);
    }

    /**
     * 处理群聊消息
     *
     * <p>群聊场景下调用 AI 获取回复并发送到对应群中。
     * 注意：群聊消息只有在 @机器人 或被提及时才应被 WebSocket 层转发到此方法。</p>
     *
     * @param groupId 群号
     * @param userId  发送者 QQ 号
     * @param message 消息内容
     */
    private void handleGroupMessage(Long groupId, Long userId, String message) {
        String reply = aiService.chat(userId, message);
        if (reply == null || reply.isBlank()) {
            reply = getFallbackReply();
        }
        oneBotApi.sendGroupMessage(groupId, reply);
        log.info("群聊回复: groupId={}, userId={}, reply={}", groupId, userId, reply);
    }

    /**
     * 获取降级回复
     *
     * <p>当 AI 服务不可用或返回空内容时，使用此降级回复，
     * 确保用户始终能得到反馈。</p>
     *
     * @return 降级回复文本
     */
    private String getFallbackReply() {
        return "抱歉，我暂时无法回复，请稍后再试～";
    }
}
