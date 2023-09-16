package com.example.coinserver.business.service;

import com.example.coinserver.common.dto.BalanceInfo;
import com.example.coinserver.common.response.LeaderboardResponse;
import com.example.coinserver.common.dto.UserScore;
import com.example.coinserver.db.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BalanceService balanceService;

    private final UserRepository userRepository;

    public LeaderboardResponse getLeaderboard() {
        var usersScore = userRepository.findAll().parallelStream()
                .map(user -> new UserLoginWithBalance(user.getLogin(), balanceService.getBalance(user)))
                .map(user -> new UserScore(user.getLogin(), user.getBalance().getChangeCost()))
                .toList();
        return new LeaderboardResponse(usersScore);
    }

    @Data
    @AllArgsConstructor
    private static class UserLoginWithBalance {

        private String login;

        private BalanceInfo balance;
    }
}
