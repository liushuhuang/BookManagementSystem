package com.book.service;

import com.book.domain.dto.BookListVo;
import com.book.domain.dto.BookRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookInfo;

import java.util.List;

public interface BookInfoService {

    List<BookListVo> listBooks(String keyword, Long categoryId, Boolean onlyAvailable);

    BookInfo create(BookRequest request);

    BookInfo update(Long id, BookRequest request);

    BookInfo updateStatus(Long id, StatusRequest request);
}
