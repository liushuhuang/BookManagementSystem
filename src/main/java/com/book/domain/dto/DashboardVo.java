package com.book.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVo {
    private Long bookCount;
    private Long copyCount;
    private Long availableCopyCount;
    private Long borrowedCopyCount;
    private Long borrowingRecordCount;
    private Long overdueRecordCount;
    private BigDecimal unpaidFineAmount;
}
