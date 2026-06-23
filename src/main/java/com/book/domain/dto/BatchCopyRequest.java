package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchCopyRequest {

    @NotNull(message = "图书不能为空")
    private Long bookId;

    @NotEmpty(message = "馆藏编号不能为空")
    private List<String> copyCodes;

    private String location;
}
