package com.qqbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 角色 DTO
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCharacterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色 ID */
    private Long id;

    /** 角色名称 */
    private String name;

    /** 角色描述 */
    private String description;

    /** 系统 Prompt */
    private String systemPrompt;

    /** 是否为默认角色 */
    private Boolean isDefault;

    /** 是否启用 */
    private Boolean enabled;

    /** 创建时间 */
    private LocalDateTime createTime;
}
