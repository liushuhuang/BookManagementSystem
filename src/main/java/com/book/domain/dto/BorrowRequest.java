package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BorrowRequest {

    @NotNull(message = "读者不能为空")
    private Long userId;

    @NotNull(message = "图书不能为空")
    private Long bookId;

    private Long copyId;
}
