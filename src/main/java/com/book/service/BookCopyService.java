package com.book.service;

import com.book.domain.dto.BatchCopyRequest;
import com.book.domain.dto.BookCopyRequest;
import com.book.domain.dto.BookCopyVo;
import com.book.domain.entity.BookCopy;

import java.util.List;

public interface BookCopyService {

    List<BookCopyVo> listCopies(Long bookId, Integer status, String keyword);

    BookCopy create(BookCopyRequest request);

    List<BookCopy> createBatch(BatchCopyRequest request);
}
