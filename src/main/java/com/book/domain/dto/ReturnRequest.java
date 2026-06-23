package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReturnRequest {

    @NotNull(message = "借阅记录不能为空")
    private Long borrowId;

    private Integer copyStatus = 1;
}
