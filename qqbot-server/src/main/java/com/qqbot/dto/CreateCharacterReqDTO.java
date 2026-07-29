package com.qqbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建 AI 角色请求 DTO
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Data
public class CreateCharacterReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称最多64个字符")
    private String name;

    /** 角色描述 */
    @Size(max = 256, message = "角色描述最多256个字符")
    private String description;

    /** 系统 Prompt */
    @NotBlank(message = "系统 Prompt 不能为空")
    private String systemPrompt;
}
