package com.book.controller;

import com.book.common.Result;
import com.book.domain.dto.DashboardVo;
import com.book.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public Result<DashboardVo> dashboard() {
        return Result.success(statisticsService.dashboard());
    }
}
