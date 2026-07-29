package com.qqbot.bot;

import com.qqbot.ai.AiService;
import com.qqbot.model.entity.QqUser;
import com.qqbot.onebot.OneBotApi;
import com.qqbot.service.ChatRecordService;
import com.qqbot.service.QqUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * QQ 消息处理器
 *
 * <p>负责接收从 WebSocket 层传入的 QQ 消息，协调 AI 服务和 OneBot API
 * 完成消息回复。处理流程包含用户注册、消息持久化和 AI 对话。</p>
 *
 * <p>处理流程：
 * <ol>
 *   <li>根据 QQ 号查找或创建用户记录</li>
 *   <li>保存用户消息到数据库</li>
 *   <li>调用 {@link AiService} 获取 AI 回复</li>
 *   <li>保存 AI 回复到数据库</li>
 *   <li>通过 {@link OneBotApi} 将回复发送回 QQ</li>
 * </ol></p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Component
public class MessageHandler {

    private final AiService aiService;
    private final OneBotApi oneBotApi;
    private final QqUserService qqUserService;
    private final ChatRecordService chatRecordService;

    /**
     * 构造函数注入
     */
    public MessageHandler(AiService aiService, OneBotApi oneBotApi,
                          QqUserService qqUserService, ChatRecordService chatRecordService) {
        this.aiService = aiService;
        this.oneBotApi = oneBotApi;
        this.qqUserService = qqUserService;
        this.chatRecordService = chatRecordService;
    }

    /**
     * 处理 QQ 消息（异步执行）
     *
     * <p>根据消息类型（私聊/群聊）分别调用对应的处理方法。
     * 使用 {@link Async} 注解确保不阻塞 WebSocket 消息接收线程。</p>
     *
     * @param messageType 消息类型：private（私聊）或 group（群聊）
     * @param qqId        发送者 QQ 号
     * @param message     消息文本内容
     * @param groupId     群号（仅群聊时有值）
     * @param selfId      机器人自身 QQ 号
     * @param nickname    发送者昵称（从 OneBot sender 信息中提取）
     */
    @Async("messageExecutor")
    public void handleMessage(String messageType, Long qqId, String message,
                              Long groupId, Long selfId, String nickname) {
        try {
            log.info("收到消息: type={}, qqId={}, groupId={}, nickname={}, message={}",
                    messageType, qqId, groupId, nickname, message);

            // 1. 查找或创建用户
            QqUser user = qqUserService.findOrCreateUser(qqId, nickname);

            // 2. 根据消息类型处理
            if ("private".equals(messageType)) {
                handlePrivateMessage(user, qqId, nickname, message);
            } else if ("group".equals(messageType)) {
                handleGroupMessage(user, qqId, nickname, message, groupId);
            } else {
                log.warn("未知消息类型: {}", messageType);
            }
        } catch (Exception e) {
            log.error("处理消息异常: qqId={}, message={}", qqId, message, e);
        }
    }

    /**
     * 处理私聊消息
     *
     * <p>持久化用户消息 → 调用 AI → 持久化 AI 回复 → 发送回复给用户。</p>
     *
     * @param user     用户实体
     * @param qqId     QQ 号
     * @param nickname 用户昵称
     * @param message  消息内容
     */
    private void handlePrivateMessage(QqUser user, Long qqId, String nickname, String message) {
        // 保存用户消息
        chatRecordService.saveUserMessage(user.getId(), qqId, nickname, message, "private", null);

        // 调用 AI
        String reply = aiService.chat(user.getId(), message);
        if (reply == null || reply.isBlank()) {
            reply = getFallbackReply();
        }

        // 保存 AI 回复
        chatRecordService.saveAiReply(user.getId(), qqId, nickname, reply, "private", null);

        // 发送回复
        oneBotApi.sendPrivateMessage(qqId, reply);
        log.info("私聊回复: qqId={}, reply={}", qqId, reply);
    }

    /**
     * 处理群聊消息
     *
     * <p>持久化用户消息 → 调用 AI → 持久化 AI 回复 → 发送回复到群。</p>
     *
     * @param user     用户实体
     * @param qqId     QQ 号
     * @param nickname 用户昵称
     * @param message  消息内容
     * @param groupId  群号
     */
    private void handleGroupMessage(QqUser user, Long qqId, String nickname, String message, Long groupId) {
        // 保存用户消息
        chatRecordService.saveUserMessage(user.getId(), qqId, nickname, message, "group", groupId);

        // 调用 AI
        String reply = aiService.chat(user.getId(), message);
        if (reply == null || reply.isBlank()) {
            reply = getFallbackReply();
        }

        // 保存 AI 回复
        chatRecordService.saveAiReply(user.getId(), qqId, nickname, reply, "group", groupId);

        // 发送回复
        oneBotApi.sendGroupMessage(groupId, reply);
        log.info("群聊回复: groupId={}, qqId={}, reply={}", groupId, qqId, reply);
    }

    /**
     * 获取降级回复（AI 调用失败时使用）
     *
     * @return 降级回复文本
     */
    private String getFallbackReply() {
        return "抱歉，我暂时无法回复，请稍后再试～";
    }
}
