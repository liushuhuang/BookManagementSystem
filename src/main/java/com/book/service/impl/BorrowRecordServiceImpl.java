package com.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.book.common.AuthContext;
import com.book.common.BusinessException;
import com.book.common.LoginUser;
import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.BorrowRequest;
import com.book.domain.entity.BookCopy;
import com.book.domain.entity.BookInfo;
import com.book.domain.entity.BorrowRecord;
import com.book.domain.entity.SysUser;
import com.book.mapper.BookCopyMapper;
import com.book.mapper.BookInfoMapper;
import com.book.mapper.BorrowRecordMapper;
import com.book.mapper.SysUserMapper;
import com.book.service.BorrowRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private static final List<Integer> ACTIVE_STATUSES = Arrays.asList(0, 2);

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookCopyMapper bookCopyMapper;
    private final BookInfoMapper bookInfoMapper;
    private final SysUserMapper sysUserMapper;

    public BorrowRecordServiceImpl(BorrowRecordMapper borrowRecordMapper,
                                   BookCopyMapper bookCopyMapper,
                                   BookInfoMapper bookInfoMapper,
                                   SysUserMapper sysUserMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookCopyMapper = bookCopyMapper;
        this.bookInfoMapper = bookInfoMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord borrow(BorrowRequest request) {
        LoginUser operator = AuthContext.requireManager();
        SysUser reader = sysUserMapper.selectById(request.getUserId());
        if (reader == null || reader.getStatus() == null || reader.getStatus() != 1) {
            throw new BusinessException(422, "读者状态不可借阅");
        }
        BookInfo book = bookInfoMapper.selectById(request.getBookId());
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException(422, "图书不可借阅");
        }
        Long activeCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getUserId, reader.getId())
                        .in(BorrowRecord::getStatus, ACTIVE_STATUSES)
        );
        Integer maxBorrowCount = reader.getMaxBorrowCount() == null ? 5 : reader.getMaxBorrowCount();
        if (activeCount >= maxBorrowCount) {
            throw new BusinessException(422, "超过最大借阅数量");
        }
        Long sameBookCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getUserId, reader.getId())
                        .eq(BorrowRecord::getBookId, book.getId())
                        .in(BorrowRecord::getStatus, ACTIVE_STATUSES)
        );
        if (sameBookCount > 0) {
            throw new BusinessException(422, "同一图书存在未归还记录");
        }
        BookCopy copy = selectBorrowCopy(book.getId(), request.getCopyId());
        int updated = bookCopyMapper.update(
                null,
                new LambdaUpdateWrapper<BookCopy>()
                        .set(BookCopy::getStatus, 2)
                        .eq(BookCopy::getId, copy.getId())
                        .eq(BookCopy::getStatus, 1)
        );
        if (updated == 0) {
            throw new BusinessException(422, "馆藏副本不可借");
        }
        LocalDateTime now = LocalDateTime.now();
        BorrowRecord record = new BorrowRecord();
        record.setBorrowNo(generateBorrowNo(now));
        record.setUserId(reader.getId());
        record.setBookId(book.getId());
        record.setCopyId(copy.getId());
        record.setBorrowTime(now);
        record.setDueTime(now.plusDays(30));
        record.setStatus(0);
        record.setOperatorId(operator.getId());
        borrowRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<BorrowRecordVo> listRecords(Integer status, String keyword) {
        AuthContext.requireManager();
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<BorrowRecord>().orderByDesc(BorrowRecord::getId);
        if (status != null) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }
        return borrowRecordMapper.selectList(wrapper).stream()
                .map(this::toVo)
                .filter(vo -> !StringUtils.hasText(keyword) || matches(vo, keyword))
                .collect(Collectors.toList());
    }

    private BookCopy selectBorrowCopy(Long bookId, Long copyId) {
        if (copyId != null) {
            BookCopy copy = bookCopyMapper.selectById(copyId);
            if (copy == null || !bookId.equals(copy.getBookId()) || copy.getStatus() == null || copy.getStatus() != 1) {
                throw new BusinessException(422, "馆藏副本不可借");
            }
            return copy;
        }
        BookCopy copy = bookCopyMapper.selectOne(
                new LambdaQueryWrapper<BookCopy>()
                        .eq(BookCopy::getBookId, bookId)
                        .eq(BookCopy::getStatus, 1)
                        .last("limit 1")
        );
        if (copy == null) {
            throw new BusinessException(422, "馆藏副本不可借");
        }
        return copy;
    }

    private String generateBorrowNo(LocalDateTime now) {
        return "B" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    BorrowRecordVo toVo(BorrowRecord record) {
        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(record.getId());
        vo.setBorrowNo(record.getBorrowNo());
        vo.setUserId(record.getUserId());
        SysUser user = sysUserMapper.selectById(record.getUserId());
        vo.setUsername(user == null ? null : user.getUsername());
        vo.setRealName(user == null ? null : user.getRealName());
        vo.setBookId(record.getBookId());
        BookInfo book = bookInfoMapper.selectById(record.getBookId());
        vo.setBookName(book == null ? null : book.getBookName());
        vo.setCopyId(record.getCopyId());
        BookCopy copy = bookCopyMapper.selectById(record.getCopyId());
        vo.setCopyCode(copy == null ? null : copy.getCopyCode());
        vo.setBorrowTime(record.getBorrowTime());
        vo.setDueTime(record.getDueTime());
        vo.setReturnTime(record.getReturnTime());
        vo.setStatus(record.getStatus());
        return vo;
    }

    private boolean matches(BorrowRecordVo vo, String keyword) {
        String text = (vo.getBorrowNo() + " " + vo.getUsername() + " " + vo.getRealName() + " "
                + vo.getBookName() + " " + vo.getCopyCode()).toLowerCase();
        return text.contains(keyword.toLowerCase());
    }
}
