package com.example.coinserver.common.response;

import com.example.coinserver.common.dto.UserScore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaderboardResponse {

    private List<UserScore> userScoreList;
}
