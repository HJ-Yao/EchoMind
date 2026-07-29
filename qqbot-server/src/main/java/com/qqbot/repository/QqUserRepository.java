package com.qqbot.repository;

import com.qqbot.model.entity.QqUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * QQ 用户 Repository
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Repository
public interface QqUserRepository extends JpaRepository<QqUser, Long> {

    /**
     * 根据 QQ 号查询用户
     *
     * @param qqId QQ 号
     * @return 用户 Optional
     */
    Optional<QqUser> findByQqId(Long qqId);

    /**
     * 判断指定 QQ 号的用户是否存在
     *
     * @param qqId QQ 号
     * @return true 表示已存在
     */
    boolean existsByQqId(Long qqId);

    /**
     * 统计用户总数
     *
     * @return 用户数量
     */
    long count();
}
