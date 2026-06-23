package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.BatchCopyRequest;
import com.book.domain.dto.BookCopyRequest;
import com.book.domain.dto.BookCopyVo;
import com.book.domain.entity.BookCopy;
import com.book.service.BookCopyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public Result<List<BookCopyVo>> list(@RequestParam(required = false) Long bookId,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String keyword) {
        return Result.success(bookCopyService.listCopies(bookId, status, keyword));
    }

    @PostMapping
    public Result<BookCopy> create(@RequestBody @Validated BookCopyRequest request) {
        return Result.success(bookCopyService.create(request));
    }

    @PostMapping("/batch")
    public Result<List<BookCopy>> createBatch(@RequestBody @Validated BatchCopyRequest request) {
        return Result.success(bookCopyService.createBatch(request));
    }
}
