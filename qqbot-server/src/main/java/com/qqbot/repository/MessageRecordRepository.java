package com.qqbot.repository;

import com.qqbot.model.entity.MessageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 消息记录 Repository
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Repository
public interface MessageRecordRepository extends JpaRepository<MessageRecord, Long> {

    /**
     * 分页查询所有消息记录（按时间倒序）
     *
     * @param pageable 分页参数
     * @return 消息分页数据
     */
    Page<MessageRecord> findAllByOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 根据用户 ID 分页查询消息记录
     *
     * @param userId   用户 ID
     * @param pageable 分页参数
     * @return 消息分页数据
     */
    Page<MessageRecord> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 统计今日消息总数
     *
     * @param startOfDay 今日起始时间
     * @param endOfDay   今日结束时间
     * @return 消息数量
     */
    @Query("SELECT COUNT(m) FROM MessageRecord m WHERE m.createTime >= :start AND m.createTime < :end")
    long countTodayMessages(@Param("start") LocalDateTime startOfDay, @Param("end") LocalDateTime endOfDay);

    /**
     * 统计今日机器人回复数（即 AI 调用次数）
     *
     * @param startOfDay 今日起始时间
     * @param endOfDay   今日结束时间
     * @return AI 调用次数
     */
    @Query("SELECT COUNT(m) FROM MessageRecord m WHERE m.role = 'assistant' AND m.createTime >= :start AND m.createTime < :end")
    long countTodayAiCalls(@Param("start") LocalDateTime startOfDay, @Param("end") LocalDateTime endOfDay);

    /**
     * 统计今日活跃用户数
     *
     * @param startOfDay 今日起始时间
     * @param endOfDay   今日结束时间
     * @return 活跃用户数
     */
    @Query("SELECT COUNT(DISTINCT m.userId) FROM MessageRecord m WHERE m.createTime >= :start AND m.createTime < :end")
    long countTodayActiveUsers(@Param("start") LocalDateTime startOfDay, @Param("end") LocalDateTime endOfDay);

    /**
     * 统计总消息数
     *
     * @return 总消息数
     */
    long count();
}
