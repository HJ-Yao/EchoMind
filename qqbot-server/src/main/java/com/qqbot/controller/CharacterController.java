package com.qqbot.controller;

import com.qqbot.common.Result;
import com.qqbot.dto.AiCharacterDTO;
import com.qqbot.dto.CreateCharacterReqDTO;
import com.qqbot.service.AiCharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 角色管理 Controller
 *
 * <p>提供 AI 角色（人格模拟）的 CRUD 接口，供管理后台角色管理页面使用。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@RestController
@RequestMapping("/character")
@RequiredArgsConstructor
public class CharacterController {

    private final AiCharacterService aiCharacterService;

    /**
     * 获取所有 AI 角色列表
     *
     * @return AI 角色列表
     */
    @GetMapping("/list")
    public Result<List<AiCharacterDTO>> listCharacters() {
        List<AiCharacterDTO> characters = aiCharacterService.listCharacters();
        return Result.success(characters);
    }

    /**
     * 创建新的 AI 角色
     *
     * @param req 创建请求
     * @return 创建后的角色信息
     */
    @PostMapping("/create")
    public Result<AiCharacterDTO> createCharacter(@Valid @RequestBody CreateCharacterReqDTO req) {
        AiCharacterDTO character = aiCharacterService.createCharacter(req);
        return Result.success("创建成功", character);
    }

    /**
     * 删除（禁用）AI 角色
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<Void> deleteCharacter(@RequestParam Long id) {
        aiCharacterService.deleteCharacter(id);
        return Result.success("删除成功", null);
    }
}
