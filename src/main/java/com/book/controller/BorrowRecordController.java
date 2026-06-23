package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.BorrowRequest;
import com.book.domain.entity.BorrowRecord;
import com.book.service.BorrowRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrow-records")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping
    public Result<List<BorrowRecordVo>> list(@RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) String keyword) {
        return Result.success(borrowRecordService.listRecords(status, keyword));
    }

    @PostMapping
    public Result<BorrowRecord> borrow(@RequestBody @Validated BorrowRequest request) {
        return Result.success(borrowRecordService.borrow(request));
    }
}
