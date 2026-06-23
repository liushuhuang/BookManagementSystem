package com.book.domain.dto;

import lombok.Data;

@Data
public class BookCopyVo {
    private Long id;
    private Long bookId;
    private String bookName;
    private String copyCode;
    private String location;
    private Integer status;
    private String remark;
}
