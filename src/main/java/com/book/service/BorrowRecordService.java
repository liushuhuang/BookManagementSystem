package com.book.service;

import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.BorrowRequest;
import com.book.domain.entity.BorrowRecord;

import java.util.List;

public interface BorrowRecordService {

    BorrowRecord borrow(BorrowRequest request);

    List<BorrowRecordVo> listRecords(Integer status, String keyword);
}
