package com.qqbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天记录 DTO
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息 ID */
    private Long id;

    /** 用户 ID */
    private Long userId;

    /** QQ 号 */
    private Long qqId;

    /** 用户昵称 */
    private String nickname;

    /** 消息角色：user / assistant */
    private String role;

    /** 消息内容 */
    private String content;

    /** 消息类型：private / group */
    private String messageType;

    /** 群号（群聊时有值） */
    private Long groupId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
