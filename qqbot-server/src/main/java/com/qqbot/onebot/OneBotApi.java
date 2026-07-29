package com.qqbot.onebot;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * OneBot11 HTTP API 调用客户端
 *
 * <p>通过 HTTP 协议调用 OneBot 标准 API，实现向 QQ 发送消息等操作。
 * OneBot11 标准规定 HTTP API 监听在特定端口上，NapCat 默认端口为 3000。</p>
 *
 * <p>API 参考：<a href="https://github.com/botuniverse/onebot-11">OneBot11 标准</a></p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Component
public class OneBotApi {

    /**
     * OneBot HTTP API 基础地址（NapCat 默认 http://127.0.0.1:3000）
     */
    @Value("${qqbot.onebot.http.url:http://127.0.0.1:3000}")
    private String httpUrl;

    /**
     * OneBot HTTP API Token
     */
    @Value("${qqbot.onebot.http.token:}")
    private String httpToken;

    /**
     * 发送私聊消息
     *
     * <p>调用 OneBot send_private_msg API，向指定用户发送文本消息。</p>
     *
     * @param userId  目标用户 QQ 号
     * @param message 消息文本内容
     */
    public void sendPrivateMessage(Long userId, String message) {
        JSONObject params = JSONUtil.createObj()
                .set("user_id", userId)
                .set("message", buildMessageArray(message))
                .set("auto_escape", false);

        String result = callApi("send_private_msg", params);
        log.info("发送私聊消息: userId={}, message={}, result={}", userId, message, result);
    }

    /**
     * 发送群聊消息
     *
     * <p>调用 OneBot send_group_msg API，向指定群发送文本消息。</p>
     *
     * @param groupId 目标群号
     * @param message 消息文本内容
     */
    public void sendGroupMessage(Long groupId, String message) {
        JSONObject params = JSONUtil.createObj()
                .set("group_id", groupId)
                .set("message", buildMessageArray(message))
                .set("auto_escape", false);

        String result = callApi("send_group_msg", params);
        log.info("发送群聊消息: groupId={}, message={}, result={}", groupId, message, result);
    }

    /**
     * 通用 OneBot API 调用方法
     *
     * <p>构建标准的 OneBot API 请求体，包含 action 和 params 字段，
     * 通过 HTTP POST 发送到 OneBot HTTP API 端点。</p>
     *
     * @param action API 动作名称（如 send_private_msg、send_group_msg）
     * @param params API 参数
     * @return API 响应体字符串，调用失败返回 null
     */
    private String callApi(String action, JSONObject params) {
        try {
            JSONObject body = JSONUtil.createObj()
                    .set("action", action)
                    .set("params", params);

            HttpRequest request = HttpRequest.post(httpUrl + "/" + action)
                    .header("Content-Type", "application/json")
                    .timeout(10000);

            // 如果配置了 Token，添加 Authorization 头
            if (httpToken != null && !httpToken.isBlank()) {
                request.header("Authorization", "Bearer " + httpToken);
            }

            HttpResponse response = request.body(body.toString()).execute();

            if (response.isOk()) {
                return response.body();
            } else {
                log.error("OneBot API 调用失败: action={}, status={}, body={}",
                        action, response.getStatus(), response.body());
                return null;
            }

        } catch (Exception e) {
            log.error("OneBot API 调用异常: action={}", action, e);
            return null;
        }
    }

    /**
     * 构建 OneBot 消息段数组
     *
     * <p>将纯文本消息转换为 OneBot v11 标准消息段格式。
     * NapCat 4.x 严格要求数组格式，不接受纯字符串。</p>
     *
     * @param text 文本内容
     * @return OneBot 消息段 JSONArray
     */
    private cn.hutool.json.JSONArray buildMessageArray(String text) {
        cn.hutool.json.JSONArray array = new cn.hutool.json.JSONArray();
        JSONObject segment = JSONUtil.createObj()
                .set("type", "text")
                .set("data", JSONUtil.createObj().set("text", text));
        array.add(segment);
        return array;
    }
}
