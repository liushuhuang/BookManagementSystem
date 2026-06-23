package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.common.AuthContext;
import com.book.domain.dto.DashboardVo;
import com.book.domain.entity.BookCopy;
import com.book.domain.entity.BookInfo;
import com.book.domain.entity.BorrowRecord;
import com.book.domain.entity.FineRecord;
import com.book.mapper.BookCopyMapper;
import com.book.mapper.BookInfoMapper;
import com.book.mapper.BorrowRecordMapper;
import com.book.mapper.FineRecordMapper;
import com.book.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final BookInfoMapper bookInfoMapper;
    private final BookCopyMapper bookCopyMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final FineRecordMapper fineRecordMapper;

    public StatisticsServiceImpl(BookInfoMapper bookInfoMapper,
                                 BookCopyMapper bookCopyMapper,
                                 BorrowRecordMapper borrowRecordMapper,
                                 FineRecordMapper fineRecordMapper) {
        this.bookInfoMapper = bookInfoMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.fineRecordMapper = fineRecordMapper;
    }

    @Override
    public DashboardVo dashboard() {
        AuthContext.requireManager();
        DashboardVo vo = new DashboardVo();
        vo.setBookCount(bookInfoMapper.selectCount(new LambdaQueryWrapper<BookInfo>()));
        vo.setCopyCount(bookCopyMapper.selectCount(new LambdaQueryWrapper<BookCopy>()));
        vo.setAvailableCopyCount(bookCopyMapper.selectCount(new LambdaQueryWrapper<BookCopy>().eq(BookCopy::getStatus, 1)));
        vo.setBorrowedCopyCount(bookCopyMapper.selectCount(new LambdaQueryWrapper<BookCopy>().eq(BookCopy::getStatus, 2)));
        vo.setBorrowingRecordCount(borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 0)));
        vo.setOverdueRecordCount(borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 2)));
        BigDecimal unpaid = fineRecordMapper.selectList(new LambdaQueryWrapper<FineRecord>().eq(FineRecord::getStatus, 0))
                .stream()
                .map(FineRecord::getFineAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setUnpaidFineAmount(unpaid);
        return vo;
    }
}
