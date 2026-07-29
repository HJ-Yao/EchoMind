package com.qqbot.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qqbot.bot.MessageHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * OneBot11 反向 WebSocket 客户端
 *
 * <p>主动连接 NapCat 的 WebSocket Server，接收 QQ 消息事件并转发给 {@link MessageHandler} 处理。
 * 支持自动重连机制，连接断开后按指数退避策略自动重连。</p>
 *
 * <p>OneBot11 事件格式参考：
 * <pre>{@code
 * {
 *   "time": 1630000000,
 *   "self_id": 123456789,
 *   "post_type": "message",
 *   "message_type": "private",
 *   "sub_type": "friend",
 *   "user_id": 987654321,
 *   "message": "你好",
 *   "raw_message": "你好",
 *   "sender": {
 *     "user_id": 987654321,
 *     "nickname": "用户昵称"
 *   }
 * }
 * }</pre></p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Component
public class OneBotWebSocketClient {

    /**
     * NapCat WebSocket Server 地址
     */
    @Value("${qqbot.onebot.ws.url:ws://127.0.0.1:3001}")
    private String wsUrl;

    /**
     * WebSocket 连接 Token（NapCat 配置的 Authorization Bearer Token）
     */
    @Value("${qqbot.onebot.ws.token:}")
    private String wsToken;

    /**
     * 是否启用自动重连
     */
    @Value("${qqbot.onebot.ws.auto-reconnect:true}")
    private boolean autoReconnect;

    /**
     * 最大重连次数（-1 表示无限重连）
     */
    @Value("${qqbot.onebot.ws.max-reconnect-attempts:-1}")
    private int maxReconnectAttempts;

    /**
     * 初始重连延迟（秒）
     */
    @Value("${qqbot.onebot.ws.reconnect-base-delay:5}")
    private int reconnectBaseDelay;

    /**
     * 最大重连延迟（秒）
     */
    @Value("${qqbot.onebot.ws.reconnect-max-delay:60}")
    private int reconnectMaxDelay;

    private final MessageHandler messageHandler;
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * WebSocket 客户端实例
     */
    private WebSocketClient client;

    /**
     * 当前重连次数
     */
    private int reconnectCount = 0;

    /**
     * 构造函数注入
     *
     * @param messageHandler 消息处理器
     */
    public OneBotWebSocketClient(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Bean 初始化后自动连接 WebSocket
     */
    @PostConstruct
    public void connect() {
        try {
            doConnect();
        } catch (Exception e) {
            log.error("WebSocket 初始连接失败: {}", e.getMessage(), e);
            if (autoReconnect) {
                scheduleReconnect();
            }
        }
    }

    /**
     * Bean 销毁前断开连接
     */
    @PreDestroy
    public void disconnect() {
        log.info("正在断开 WebSocket 连接...");
        reconnectScheduler.shutdownNow();
        if (client != null && client.isOpen()) {
            client.close();
        }
    }

    /**
     * 执行 WebSocket 连接
     *
     * <p>创建 WebSocket 客户端并连接到 NapCat Server，设置消息处理器。
     * 连接成功后会重置重连计数器。</p>
     */
    private synchronized void doConnect() throws Exception {
        URI uri = new URI(wsUrl);
        Map<String, String> headers = new ConcurrentHashMap<>();
        if (wsToken != null && !wsToken.isBlank()) {
            headers.put("Authorization", "Bearer " + wsToken);
        }

        client = new WebSocketClient(uri, headers) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                log.info("OneBot WebSocket 连接成功: {}", wsUrl);
                reconnectCount = 0;
            }

            @Override
            public void onMessage(String rawMessage) {
                log.debug("收到 OneBot 消息: {}", rawMessage);
                handleMessage(rawMessage);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.warn("OneBot WebSocket 连接关闭: code={}, reason={}, remote={}", code, reason, remote);
                if (autoReconnect) {
                    scheduleReconnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                log.error("OneBot WebSocket 连接异常: {}", ex.getMessage(), ex);
            }
        };

        // 设置连接超时
        client.setConnectionLostTimeout(30);
        client.connect();
    }

    /**
     * 处理接收到的 OneBot 消息
     *
     * <p>解析 JSON 消息，过滤出 message 类型的 post_type，
     * 提取用户信息和消息内容后交给 {@link MessageHandler} 异步处理。</p>
     *
     * @param rawMessage 原始 JSON 消息字符串
     */
    private void handleMessage(String rawMessage) {
        try {
            JSONObject json = JSONUtil.parseObj(rawMessage);

            // 只处理消息事件
            String postType = json.getStr("post_type");
            if (!"message".equals(postType)) {
                log.debug("忽略非消息事件: post_type={}", postType);
                return;
            }

            // 提取消息基本信息
            String messageType = json.getStr("message_type");
            Long userId = json.getLong("user_id");
            String message = json.getStr("raw_message");
            Long groupId = json.getLong("group_id");
            Long selfId = json.getLong("self_id");

            if (userId == null || message == null || message.isBlank()) {
                log.debug("消息缺少必要字段: userId={}, message={}", userId, message);
                return;
            }

            // 异步处理消息
            messageHandler.handleMessage(messageType, userId, message, groupId, selfId);

        } catch (Exception e) {
            log.error("解析 OneBot 消息失败: {}", rawMessage, e);
        }
    }

    /**
     * 调度重连任务
     *
     * <p>使用指数退避策略计算重连延迟：baseDelay * 2^reconnectCount，
     * 但不超过最大延迟。达到最大重连次数后停止重连。</p>
     */
    private void scheduleReconnect() {
        if (maxReconnectAttempts > 0 && reconnectCount >= maxReconnectAttempts) {
            log.error("已达最大重连次数({})，停止重连", maxReconnectAttempts);
            return;
        }

        int delay = Math.min(reconnectBaseDelay * (1 << Math.min(reconnectCount, 10)), reconnectMaxDelay);
        reconnectCount++;
        log.info("将在 {} 秒后进行第 {} 次重连...", delay, reconnectCount);

        reconnectScheduler.schedule(() -> {
            try {
                doConnect();
            } catch (Exception e) {
                log.error("第 {} 次重连失败: {}", reconnectCount, e.getMessage());
                scheduleReconnect();
            }
        }, delay, TimeUnit.SECONDS);
    }
}
