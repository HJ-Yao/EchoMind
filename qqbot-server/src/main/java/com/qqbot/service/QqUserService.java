package com.qqbot.service;

import com.qqbot.model.entity.QqUser;
import com.qqbot.repository.QqUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * QQ 用户服务
 *
 * <p>管理 QQ 用户的创建和查询。用户首次发消息时自动注册，
 * 后续消息更新昵称等信息。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QqUserService {

    private final QqUserRepository qqUserRepository;

    /**
     * 查找或创建用户
     *
     * <p>如果用户已存在则更新昵称，否则创建新用户记录。</p>
     *
     * @param qqId     QQ 号
     * @param nickname 用户昵称
     * @return 用户实体
     */
    @Transactional
    public QqUser findOrCreateUser(Long qqId, String nickname) {
        Optional<QqUser> existing = qqUserRepository.findByQqId(qqId);

        if (existing.isPresent()) {
            QqUser user = existing.get();
            // 更新昵称（如果发生了变化）
            if (nickname != null && !nickname.isBlank()
                    && !nickname.equals(user.getNickname())) {
                user.setNickname(nickname);
                user = qqUserRepository.save(user);
            }
            return user;
        }

        QqUser newUser = QqUser.builder()
                .qqId(qqId)
                .nickname(nickname != null && !nickname.isBlank() ? nickname : "未知用户")
                .build();

        QqUser saved = qqUserRepository.save(newUser);
        log.info("新用户注册: id={}, qqId={}, nickname={}", saved.getId(), saved.getQqId(), saved.getNickname());
        return saved;
    }
}
