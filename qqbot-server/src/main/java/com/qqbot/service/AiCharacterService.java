package com.qqbot.service;

import com.qqbot.common.SystemPromptConstants;
import com.qqbot.common.exception.BusinessException;
import com.qqbot.dto.AiCharacterDTO;
import com.qqbot.dto.CreateCharacterReqDTO;
import com.qqbot.model.entity.AiCharacter;
import com.qqbot.repository.AiCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 角色管理服务
 *
 * <p>提供 AI 角色（人格模拟）的 CRUD 操作。每个角色定义了机器人的
 * System Prompt 和行为风格。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCharacterService {

    private final AiCharacterRepository aiCharacterRepository;

    /**
     * 获取所有启用的 AI 角色列表
     *
     * @return AI 角色 DTO 列表
     */
    public List<AiCharacterDTO> listCharacters() {
        List<AiCharacter> characters = aiCharacterRepository.findByEnabledTrueOrderByCreateTimeDesc();
        return characters.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 创建新的 AI 角色
     *
     * @param req 创建请求
     * @return 创建后的角色 DTO
     * @throws BusinessException 如果角色名已存在
     */
    @Transactional
    public AiCharacterDTO createCharacter(CreateCharacterReqDTO req) {
        if (aiCharacterRepository.existsByName(req.getName())) {
            throw new BusinessException(400, "角色名称已存在：" + req.getName());
        }

        AiCharacter character = AiCharacter.builder()
                .name(req.getName())
                .description(req.getDescription())
                .systemPrompt(req.getSystemPrompt())
                .isDefault(false)
                .enabled(true)
                .build();

        AiCharacter saved = aiCharacterRepository.save(character);
        log.info("创建 AI 角色成功: id={}, name={}", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    /**
     * 删除 AI 角色（逻辑删除，设为禁用）
     *
     * @param id 角色 ID
     * @throws BusinessException 如果角色不存在
     */
    @Transactional
    public void deleteCharacter(Long id) {
        AiCharacter character = aiCharacterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "角色不存在: id=" + id));

        character.setEnabled(false);
        aiCharacterRepository.save(character);
        log.info("禁用 AI 角色: id={}, name={}", character.getId(), character.getName());
    }

    /**
     * 获取默认角色的 System Prompt
     *
     * @return System Prompt 文本，如果没有默认角色则返回内置默认 Prompt
     */
    public String getDefaultSystemPrompt() {
        return aiCharacterRepository.findByIsDefaultTrueAndEnabledTrue()
                .map(AiCharacter::getSystemPrompt)
                .orElse(SystemPromptConstants.DEFAULT_SYSTEM_PROMPT);
    }

    /**
     * Entity 转 DTO
     */
    private AiCharacterDTO toDTO(AiCharacter entity) {
        return AiCharacterDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .systemPrompt(entity.getSystemPrompt())
                .isDefault(entity.getIsDefault())
                .enabled(entity.getEnabled())
                .createTime(entity.getCreateTime())
                .build();
    }
}
