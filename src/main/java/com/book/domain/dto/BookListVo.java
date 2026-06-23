package com.book.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookListVo {
    private Long id;
    private String isbn;
    private String bookName;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer pages;
    private Integer status;
    private Long totalCopyCount;
    private Long availableCount;
    private Long borrowedCount;
}
