package com.book.service;

import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.ReturnRequest;
import com.book.domain.entity.BorrowRecord;

import java.util.List;

public interface ReturnService {

    List<BorrowRecordVo> listPending(String keyword);

    BorrowRecord returnBook(ReturnRequest request);
}
