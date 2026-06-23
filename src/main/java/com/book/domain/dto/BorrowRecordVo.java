package com.book.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordVo {
    private Long id;
    private String borrowNo;
    private Long userId;
    private String username;
    private String realName;
    private Long bookId;
    private String bookName;
    private Long copyId;
    private String copyCode;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status;
}
