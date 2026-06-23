package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.domain.dto.CategoryRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookCategory;
import com.book.mapper.BookCategoryMapper;
import com.book.service.BookCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper bookCategoryMapper;

    public BookCategoryServiceImpl(BookCategoryMapper bookCategoryMapper) {
        this.bookCategoryMapper = bookCategoryMapper;
    }

    @Override
    public List<BookCategory> listCategories() {
        AuthContext.require();
        return bookCategoryMapper.selectList(
                new LambdaQueryWrapper<BookCategory>().orderByAsc(BookCategory::getSortOrder).orderByAsc(BookCategory::getId)
        );
    }

    @Override
    public BookCategory create(CategoryRequest request) {
        AuthContext.requireManager();
        if (existsCode(request.getCategoryCode(), null)) {
            throw new BusinessException(409, "分类编码已存在");
        }
        BookCategory category = new BookCategory();
        category.setCategoryName(request.getCategoryName());
        category.setCategoryCode(request.getCategoryCode());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        category.setDescription(request.getDescription());
        category.setStatus(1);
        category.setIsDeleted(0);
        bookCategoryMapper.insert(category);
        return category;
    }

    @Override
    public BookCategory update(Long id, CategoryRequest request) {
        AuthContext.requireManager();
        BookCategory category = getExisting(id);
        if (existsCode(request.getCategoryCode(), id)) {
            throw new BusinessException(409, "分类编码已存在");
        }
        category.setCategoryName(request.getCategoryName());
        category.setCategoryCode(request.getCategoryCode());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        category.setDescription(request.getDescription());
        bookCategoryMapper.updateById(category);
        return category;
    }

    @Override
    public BookCategory updateStatus(Long id, StatusRequest request) {
        AuthContext.requireManager();
        if (request.getStatus() != 0 && request.getStatus() != 1) {
            throw new BusinessException(400, "状态参数错误");
        }
        BookCategory category = getExisting(id);
        category.setStatus(request.getStatus());
        bookCategoryMapper.updateById(category);
        return category;
    }

    private BookCategory getExisting(Long id) {
        BookCategory category = bookCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    private boolean existsCode(String code, Long excludeId) {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getCategoryCode, code);
        if (excludeId != null) {
            wrapper.ne(BookCategory::getId, excludeId);
        }
        return bookCategoryMapper.selectCount(wrapper) > 0;
    }
}
