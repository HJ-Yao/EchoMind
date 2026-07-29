package com.qqbot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 角色/人格配置实体
 *
 * <p>定义机器人的 AI 角色（人格模拟），包含角色名称、描述和 System Prompt。
 * 不同角色可配置不同的回复风格，如"温柔女友"、"毒舌朋友"、"心理咨询助手"等。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_character")
public class AiCharacter {

    /**
     * 主键 ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称（唯一）
     */
    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    /**
     * 角色描述（简要说明角色特点）
     */
    @Column(name = "description", length = 256)
    private String description;

    /**
     * 系统 Prompt（定义 AI 的行为风格和回复模式）
     */
    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    private String systemPrompt;

    /**
     * 是否为默认角色（0=否，1=是）
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * 是否启用（0=禁用，1=启用）
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

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
        if (isDefault == null) {
            isDefault = false;
        }
        if (enabled == null) {
            enabled = true;
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
