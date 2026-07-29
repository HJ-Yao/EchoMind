package com.qqbot.controller;

import com.qqbot.common.Result;
import com.qqbot.dto.ChatRecordDTO;
import com.qqbot.service.ChatRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天记录 Controller
 *
 * <p>提供聊天记录的分页查询接口，供管理后台聊天记录页面使用。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRecordService chatRecordService;

    /**
     * 分页查询聊天记录
     *
     * @param page 页码（默认第 1 页）
     * @param size 每页条数（默认 20，最大 100）
     * @return 聊天记录分页数据
     */
    @GetMapping("/records")
    public Result<Object> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        // 限制最大每页条数，防止一次性查询过多数据
        if (size > 100) {
            size = 100;
        }
        if (page < 1) {
            page = 1;
        }

        Page<ChatRecordDTO> recordPage = chatRecordService.getRecords(page, size);
        return Result.success(recordPage);
    }
}
