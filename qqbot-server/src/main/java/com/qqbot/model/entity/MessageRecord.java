package com.qqbot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息记录实体
 *
 * <p>持久化每条 QQ 聊天消息，记录发送者、角色（用户/机器人）、消息内容和时间。
 * 支持按用户 ID 和创建时间排序查询，为后续 Memory/RAG 功能提供数据基础。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_record", indexes = {
        @Index(name = "idx_message_record_user_id", columnList = "user_id"),
        @Index(name = "idx_message_record_create_time", columnList = "create_time"),
        @Index(name = "idx_message_record_user_time", columnList = "user_id, create_time")
})
public class MessageRecord {

    /**
     * 主键 ID（自增，使用序列以提高批量插入性能）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联用户 ID（对应 qq_user 表的 id）
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户 QQ 号（冗余字段，方便查询）
     */
    @Column(name = "qq_id", nullable = false, length = 32)
    private Long qqId;

    /**
     * 用户昵称（冗余字段，记录消息时的昵称快照）
     */
    @Column(name = "nickname", length = 128)
    private String nickname;

    /**
     * 消息角色：user（用户消息）或 assistant（机器人回复）
     */
    @Column(name = "role", nullable = false, length = 16)
    private String role;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型：private（私聊）或 group（群聊）
     */
    @Column(name = "message_type", length = 16)
    private String messageType;

    /**
     * 群号（仅群聊消息有值）
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * JPA 持久化前自动设置时间
     */
    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}
