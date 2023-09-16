package com.example.coinserver.business.controller;

import com.example.coinserver.business.service.StatisticsService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.response.LeaderboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/leaderboard")
    public GenericResponse<LeaderboardResponse> getLeaderboard() {
        return new GenericResponse<>(0, "SUCCESS", statisticsService.getLeaderboard());
    }
}
