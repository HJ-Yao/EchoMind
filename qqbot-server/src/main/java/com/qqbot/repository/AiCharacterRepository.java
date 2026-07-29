package com.qqbot.repository;

import com.qqbot.model.entity.AiCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 角色 Repository
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Repository
public interface AiCharacterRepository extends JpaRepository<AiCharacter, Long> {

    /**
     * 根据名称查询角色
     *
     * @param name 角色名称
     * @return 角色 Optional
     */
    Optional<AiCharacter> findByName(String name);

    /**
     * 查询所有启用的角色
     *
     * @return 启用的角色列表
     */
    List<AiCharacter> findByEnabledTrueOrderByCreateTimeDesc();

    /**
     * 查询默认角色
     *
     * @return 默认角色 Optional
     */
    Optional<AiCharacter> findByIsDefaultTrueAndEnabledTrue();

    /**
     * 判断名称是否已存在
     *
     * @param name 角色名称
     * @return true 表示已存在
     */
    boolean existsByName(String name);
}
