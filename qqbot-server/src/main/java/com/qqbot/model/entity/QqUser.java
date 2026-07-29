package com.qqbot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QQ 用户实体
 *
 * <p>记录与机器人交互过的 QQ 用户基本信息。
 * 当用户首次发送消息时自动创建记录，后续消息更新昵称等信息。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qq_user")
public class QqUser {

    /**
     * 主键 ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * QQ 号（唯一索引）
     */
    @Column(name = "qq_id", nullable = false, unique = true, length = 32)
    private Long qqId;

    /**
     * QQ 昵称
     */
    @Column(name = "nickname", length = 128)
    private String nickname;

    /**
     * 头像 URL
     */
    @Column(name = "avatar", length = 512)
    private String avatar;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * JPA 持久化前自动设置时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createTime == null) {
            createTime = now;
        }
        if (updateTime == null) {
            updateTime = now;
        }
    }

    /**
     * JPA 更新前自动刷新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
