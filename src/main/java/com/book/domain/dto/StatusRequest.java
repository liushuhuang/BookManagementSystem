package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatusRequest {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
