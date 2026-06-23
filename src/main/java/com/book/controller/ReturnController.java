package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.BorrowRecordVo;
import com.book.domain.dto.ReturnRequest;
import com.book.domain.entity.BorrowRecord;
import com.book.service.ReturnService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @GetMapping("/pending")
    public Result<List<BorrowRecordVo>> pending(@RequestParam(required = false) String keyword) {
        return Result.success(returnService.listPending(keyword));
    }

    @PostMapping
    public Result<BorrowRecord> returnBook(@RequestBody @Validated ReturnRequest request) {
        return Result.success(returnService.returnBook(request));
    }
}
