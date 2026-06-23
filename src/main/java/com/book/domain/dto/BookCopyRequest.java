package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BookCopyRequest {

    @NotNull(message = "图书不能为空")
    private Long bookId;

    @NotBlank(message = "馆藏编号不能为空")
    private String copyCode;

    private String location;

    private Integer status = 1;

    private String remark;
}
