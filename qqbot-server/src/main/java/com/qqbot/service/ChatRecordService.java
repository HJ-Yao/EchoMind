package com.qqbot.service;

import com.qqbot.dto.ChatRecordDTO;
import com.qqbot.model.entity.MessageRecord;
import com.qqbot.repository.MessageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 聊天记录服务
 *
 * <p>提供聊天记录的查询和持久化功能。每次 QQ 消息交互（用户发送 + AI 回复）
 * 均通过此服务持久化到 PostgreSQL。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRecordService {

    private final MessageRecordRepository messageRecordRepository;

    /**
     * 分页查询聊天记录（按时间倒序）
     *
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 聊天记录分页结果
     */
    public Page<ChatRecordDTO> getRecords(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<MessageRecord> recordPage = messageRecordRepository.findAll(pageRequest);
        return recordPage.map(this::toDTO);
    }

    /**
     * 保存用户消息
     *
     * @param userId      用户 ID
     * @param qqId        QQ 号
     * @param nickname    用户昵称
     * @param content     消息内容
     * @param messageType 消息类型（private/group）
     * @param groupId     群号（群聊时传入）
     * @return 保存后的消息记录
     */
    public MessageRecord saveUserMessage(Long userId, Long qqId, String nickname,
                                          String content, String messageType, Long groupId) {
        MessageRecord record = MessageRecord.builder()
                .userId(userId)
                .qqId(qqId)
                .nickname(nickname)
                .role("user")
                .content(content)
                .messageType(messageType)
                .groupId(groupId)
                .build();
        return messageRecordRepository.save(record);
    }

    /**
     * 保存 AI 回复
     *
     * @param userId      用户 ID
     * @param qqId        QQ 号
     * @param nickname    用户昵称
     * @param content     AI 回复内容
     * @param messageType 消息类型（private/group）
     * @param groupId     群号（群聊时传入）
     * @return 保存后的消息记录
     */
    public MessageRecord saveAiReply(Long userId, Long qqId, String nickname,
                                      String content, String messageType, Long groupId) {
        MessageRecord record = MessageRecord.builder()
                .userId(userId)
                .qqId(qqId)
                .nickname(nickname)
                .role("assistant")
                .content(content)
                .messageType(messageType)
                .groupId(groupId)
                .build();
        return messageRecordRepository.save(record);
    }

    /**
     * Entity 转 DTO
     *
     * @param record 消息记录实体
     * @return 聊天记录 DTO
     */
    private ChatRecordDTO toDTO(MessageRecord record) {
        return ChatRecordDTO.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .qqId(record.getQqId())
                .nickname(record.getNickname())
                .role(record.getRole())
                .content(record.getContent())
                .messageType(record.getMessageType())
                .groupId(record.getGroupId())
                .createTime(record.getCreateTime())
                .build();
    }
}
