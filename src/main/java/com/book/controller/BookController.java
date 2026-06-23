package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.BookListVo;
import com.book.domain.dto.BookRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookInfo;
import com.book.service.BookInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookInfoService bookInfoService;

    public BookController(BookInfoService bookInfoService) {
        this.bookInfoService = bookInfoService;
    }

    @GetMapping
    public Result<List<BookListVo>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) Boolean onlyAvailable) {
        return Result.success(bookInfoService.listBooks(keyword, categoryId, onlyAvailable));
    }

    @PostMapping
    public Result<BookInfo> create(@RequestBody @Validated BookRequest request) {
        return Result.success(bookInfoService.create(request));
    }

    @PutMapping("/{id}")
    public Result<BookInfo> update(@PathVariable Long id, @RequestBody @Validated BookRequest request) {
        return Result.success(bookInfoService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public Result<BookInfo> updateStatus(@PathVariable Long id, @RequestBody @Validated StatusRequest request) {
        return Result.success(bookInfoService.updateStatus(id, request));
    }
}
