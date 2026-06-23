package com.book.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequest {

    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String bookName;

    @NotBlank(message = "作者不能为空")
    private String author;

    @NotBlank(message = "出版社不能为空")
    private String publisher;

    private LocalDate publishDate;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String coverUrl;

    private String description;

    private BigDecimal price;

    private Integer pages;
}
