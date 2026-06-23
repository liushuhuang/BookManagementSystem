package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.CategoryRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookCategory;
import com.book.service.BookCategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book-categories")
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    public BookCategoryController(BookCategoryService bookCategoryService) {
        this.bookCategoryService = bookCategoryService;
    }

    @GetMapping
    public Result<List<BookCategory>> list() {
        return Result.success(bookCategoryService.listCategories());
    }

    @PostMapping
    public Result<BookCategory> create(@RequestBody @Validated CategoryRequest request) {
        return Result.success(bookCategoryService.create(request));
    }

    @PutMapping("/{id}")
    public Result<BookCategory> update(@PathVariable Long id, @RequestBody @Validated CategoryRequest request) {
        return Result.success(bookCategoryService.update(id, request));
    }

    @PutMapping("/{id}/status")
    public Result<BookCategory> updateStatus(@PathVariable Long id, @RequestBody @Validated StatusRequest request) {
        return Result.success(bookCategoryService.updateStatus(id, request));
    }
}
