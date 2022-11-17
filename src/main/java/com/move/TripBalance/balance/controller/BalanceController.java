package com.move.TripBalance.balance.controller;

import com.move.TripBalance.balance.service.BalanceService;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class BalanceController {

    private final BalanceService balanceService;

    // 게임 시작
    @GetMapping("/game/start")
    public ResponseEntity<PrivateResponseBody> start(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return balanceService.start(userDetails);
    }

    //게임 다음 문제
    @GetMapping("/game/{gameId}/{questionId}")
    public ResponseEntity<PrivateResponseBody> choice(@PathVariable Long gameId, @PathVariable Long questionId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return balanceService.choice(gameId, questionId, userDetails);
    }

    // 게임결과를 통해 여행지 및 전체 선택지 저장
    @ResponseBody
    @PostMapping("/game/result/{gameId}/{lastId}")
    public ResponseEntity<PrivateResponseBody> questionResult(@PathVariable Long gameId, @PathVariable Long lastId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return balanceService.questionResult(gameId ,lastId, userDetails);
    }
  }