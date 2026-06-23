package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.domain.dto.BookListVo;
import com.book.domain.dto.BookRequest;
import com.book.domain.dto.StatusRequest;
import com.book.domain.entity.BookCategory;
import com.book.domain.entity.BookCopy;
import com.book.domain.entity.BookInfo;
import com.book.mapper.BookCategoryMapper;
import com.book.mapper.BookCopyMapper;
import com.book.mapper.BookInfoMapper;
import com.book.service.BookInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookInfoServiceImpl implements BookInfoService {

    private final BookInfoMapper bookInfoMapper;
    private final BookCategoryMapper bookCategoryMapper;
    private final BookCopyMapper bookCopyMapper;

    public BookInfoServiceImpl(BookInfoMapper bookInfoMapper,
                               BookCategoryMapper bookCategoryMapper,
                               BookCopyMapper bookCopyMapper) {
        this.bookInfoMapper = bookInfoMapper;
        this.bookCategoryMapper = bookCategoryMapper;
        this.bookCopyMapper = bookCopyMapper;
    }

    @Override
    public List<BookListVo> listBooks(String keyword, Long categoryId, Boolean onlyAvailable) {
        AuthContext.require();
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<BookInfo>().orderByDesc(BookInfo::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BookInfo::getBookName, keyword)
                    .or()
                    .like(BookInfo::getIsbn, keyword)
                    .or()
                    .like(BookInfo::getAuthor, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(BookInfo::getCategoryId, categoryId);
        }
        List<BookInfo> books = bookInfoMapper.selectList(wrapper);
        Map<Long, BookCategory> categories = bookCategoryMapper.selectList(new LambdaQueryWrapper<BookCategory>())
                .stream()
                .collect(Collectors.toMap(BookCategory::getId, Function.identity(), (a, b) -> a));
        return books.stream()
                .map(book -> toVo(book, categories.get(book.getCategoryId())))
                .filter(vo -> !Boolean.TRUE.equals(onlyAvailable) || vo.getAvailableCount() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public BookInfo create(BookRequest request) {
        AuthContext.requireManager();
        if (bookInfoMapper.selectCount(new LambdaQueryWrapper<BookInfo>().eq(BookInfo::getIsbn, request.getIsbn())) > 0) {
            throw new BusinessException(409, "ISBN已存在");
        }
        ensureEnabledCategory(request.getCategoryId());
        BookInfo book = new BookInfo();
        copyRequest(request, book);
        book.setStatus(1);
        book.setIsDeleted(0);
        bookInfoMapper.insert(book);
        return book;
    }

    @Override
    public BookInfo update(Long id, BookRequest request) {
        AuthContext.requireManager();
        BookInfo book = getExisting(id);
        Long duplicate = bookInfoMapper.selectCount(
                new LambdaQueryWrapper<BookInfo>().eq(BookInfo::getIsbn, request.getIsbn()).ne(BookInfo::getId, id)
        );
        if (duplicate > 0) {
            throw new BusinessException(409, "ISBN已存在");
        }
        ensureEnabledCategory(request.getCategoryId());
        copyRequest(request, book);
        bookInfoMapper.updateById(book);
        return book;
    }

    @Override
    public BookInfo updateStatus(Long id, StatusRequest request) {
        AuthContext.requireManager();
        if (request.getStatus() != 0 && request.getStatus() != 1) {
            throw new BusinessException(400, "状态参数错误");
        }
        BookInfo book = getExisting(id);
        book.setStatus(request.getStatus());
        bookInfoMapper.updateById(book);
        return book;
    }

    private BookInfo getExisting(Long id) {
        BookInfo book = bookInfoMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return book;
    }

    private void ensureEnabledCategory(Long categoryId) {
        BookCategory category = bookCategoryMapper.selectById(categoryId);
        if (category == null || category.getStatus() == null || category.getStatus() != 1) {
            throw new BusinessException(422, "图书必须绑定有效分类");
        }
    }

    private void copyRequest(BookRequest request, BookInfo book) {
        book.setIsbn(request.getIsbn());
        book.setBookName(request.getBookName());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setPublishDate(request.getPublishDate());
        book.setCategoryId(request.getCategoryId());
        book.setCoverUrl(request.getCoverUrl());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setPages(request.getPages());
    }

    private BookListVo toVo(BookInfo book, BookCategory category) {
        BookListVo vo = new BookListVo();
        vo.setId(book.getId());
        vo.setIsbn(book.getIsbn());
        vo.setBookName(book.getBookName());
        vo.setAuthor(book.getAuthor());
        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPublishDate());
        vo.setCategoryId(book.getCategoryId());
        vo.setCategoryName(category == null ? null : category.getCategoryName());
        vo.setPrice(book.getPrice());
        vo.setPages(book.getPages());
        vo.setStatus(book.getStatus());
        vo.setTotalCopyCount(countCopies(book.getId(), null));
        vo.setAvailableCount(countCopies(book.getId(), 1));
        vo.setBorrowedCount(countCopies(book.getId(), 2));
        return vo;
    }

    private Long countCopies(Long bookId, Integer status) {
        LambdaQueryWrapper<BookCopy> wrapper = new LambdaQueryWrapper<BookCopy>().eq(BookCopy::getBookId, bookId);
        if (status != null) {
            wrapper.eq(BookCopy::getStatus, status);
        }
        return bookCopyMapper.selectCount(wrapper);
    }
}
