package com.book.service;

import com.book.domain.dto.CategoryRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookCategory;

import java.util.List;

public interface BookCategoryService {

    List<BookCategory> listCategories();

    BookCategory create(CategoryRequest request);

    BookCategory update(Long id, CategoryRequest request);

    BookCategory updateStatus(Long id, StatusRequest request);
}
