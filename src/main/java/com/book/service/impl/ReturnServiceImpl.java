package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.ReturnRequest;
import com.book.domain.entity.BookCopy;
import com.book.domain.entity.BorrowRecord;
import com.book.mapper.BookCopyMapper;
import com.book.mapper.BorrowRecordMapper;
import com.book.service.ReturnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnServiceImpl implements ReturnService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookCopyMapper bookCopyMapper;
    private final BorrowRecordServiceImpl borrowRecordService;

    public ReturnServiceImpl(BorrowRecordMapper borrowRecordMapper,
                             BookCopyMapper bookCopyMapper,
                             BorrowRecordServiceImpl borrowRecordService) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.borrowRecordService = borrowRecordService;
    }

    @Override
    public List<BorrowRecordVo> listPending(String keyword) {
        AuthContext.requireManager();
        return borrowRecordMapper.selectList(
                        new LambdaQueryWrapper<BorrowRecord>()
                                .in(BorrowRecord::getStatus, Arrays.asList(0, 2))
                                .orderByDesc(BorrowRecord::getId)
                )
                .stream()
                .map(borrowRecordService::toVo)
                .filter(vo -> !StringUtils.hasText(keyword) || matches(vo, keyword))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord returnBook(ReturnRequest request) {
        AuthContext.requireManager();
        BorrowRecord record = borrowRecordMapper.selectById(request.getBorrowId());
        if (record == null) {
            throw new BusinessException(404, "借阅记录不存在");
        }
        if (record.getStatus() == null || (record.getStatus() != 0 && record.getStatus() != 2)) {
            throw new BusinessException(422, "借阅记录不可归还");
        }
        record.setStatus(1);
        record.setReturnTime(LocalDateTime.now());
        borrowRecordMapper.updateById(record);
        Integer copyStatus = request.getCopyStatus() == null ? 1 : request.getCopyStatus();
        bookCopyMapper.update(
                null,
                new LambdaUpdateWrapper<BookCopy>()
                        .set(BookCopy::getStatus, copyStatus)
                        .eq(BookCopy::getId, record.getCopyId())
        );
        return record;
    }

    private boolean matches(BorrowRecordVo vo, String keyword) {
        String text = (vo.getBorrowNo() + " " + vo.getUsername() + " " + vo.getRealName() + " "
                + vo.getBookName() + " " + vo.getCopyCode()).toLowerCase();
        return text.contains(keyword.toLowerCase());
    }
}
