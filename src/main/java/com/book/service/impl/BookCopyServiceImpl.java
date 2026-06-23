package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.domain.dto.BatchCopyRequest;
import com.book.domain.dto.BookCopyRequest;
import com.book.domain.dto.BookCopyVo;
import com.book.domain.entity.BookCopy;
import com.book.domain.entity.BookInfo;
import com.book.mapper.BookCopyMapper;
import com.book.mapper.BookInfoMapper;
import com.book.service.BookCopyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyMapper bookCopyMapper;
    private final BookInfoMapper bookInfoMapper;

    public BookCopyServiceImpl(BookCopyMapper bookCopyMapper, BookInfoMapper bookInfoMapper) {
        this.bookCopyMapper = bookCopyMapper;
        this.bookInfoMapper = bookInfoMapper;
    }

    @Override
    public List<BookCopyVo> listCopies(Long bookId, Integer status, String keyword) {
        AuthContext.require();
        LambdaQueryWrapper<BookCopy> wrapper = new LambdaQueryWrapper<BookCopy>().orderByDesc(BookCopy::getId);
        if (bookId != null) {
            wrapper.eq(BookCopy::getBookId, bookId);
        }
        if (status != null) {
            wrapper.eq(BookCopy::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BookCopy::getCopyCode, keyword).or().like(BookCopy::getLocation, keyword));
        }
        return bookCopyMapper.selectList(wrapper).stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public BookCopy create(BookCopyRequest request) {
        AuthContext.requireManager();
        ensureBookExists(request.getBookId());
        ensureCopyCodeUnique(request.getCopyCode());
        BookCopy copy = new BookCopy();
        copy.setBookId(request.getBookId());
        copy.setCopyCode(request.getCopyCode());
        copy.setLocation(request.getLocation());
        copy.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        copy.setRemark(request.getRemark());
        copy.setIsDeleted(0);
        bookCopyMapper.insert(copy);
        return copy;
    }

    @Override
    public List<BookCopy> createBatch(BatchCopyRequest request) {
        AuthContext.requireManager();
        ensureBookExists(request.getBookId());
        List<BookCopy> copies = new ArrayList<>();
        for (String copyCode : request.getCopyCodes()) {
            ensureCopyCodeUnique(copyCode);
            BookCopy copy = new BookCopy();
            copy.setBookId(request.getBookId());
            copy.setCopyCode(copyCode);
            copy.setLocation(request.getLocation());
            copy.setStatus(1);
            copy.setIsDeleted(0);
            bookCopyMapper.insert(copy);
            copies.add(copy);
        }
        return copies;
    }

    private void ensureBookExists(Long bookId) {
        BookInfo book = bookInfoMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
    }

    private void ensureCopyCodeUnique(String copyCode) {
        Long count = bookCopyMapper.selectCount(new LambdaQueryWrapper<BookCopy>().eq(BookCopy::getCopyCode, copyCode));
        if (count > 0) {
            throw new BusinessException(409, "馆藏编号已存在");
        }
    }

    private BookCopyVo toVo(BookCopy copy) {
        BookCopyVo vo = new BookCopyVo();
        vo.setId(copy.getId());
        vo.setBookId(copy.getBookId());
        BookInfo book = bookInfoMapper.selectById(copy.getBookId());
        vo.setBookName(book == null ? null : book.getBookName());
        vo.setCopyCode(copy.getCopyCode());
        vo.setLocation(copy.getLocation());
        vo.setStatus(copy.getStatus());
        vo.setRemark(copy.getRemark());
        return vo;
    }
}
