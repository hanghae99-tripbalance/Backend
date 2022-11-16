package com.move.TripBalance.mainpage.controller;

import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.service.ApiService;
import com.move.TripBalance.mainpage.service.MainPageService;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MainPageController {

    private final MainPageService mainPageService;
    private final ApiService apiService;

    // 좋아요가 가장 많은 게시글 5개
    @GetMapping("/bestfive")
    public ResponseEntity<PrivateResponseBody> getBestFive(){
        return mainPageService.getTop5Posts();
    }

    // sk api로 인구 통계 불러오기
    @GetMapping("/apimap")
    public void getApi() throws IOException, ParseException {
        apiService.getResultList();
    }

    // 위도, 경도 정보를 받아서 인구통계와 날씨 정보 반환
    @PostMapping("/apimap")
    public JSONObject getLocation(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }

    // 지역별 포스트 목록 반환
    @GetMapping("/localpost/{local}")
    public ResponseEntity<PrivateResponseBody> getLocalPostList(@PathVariable Long local){
        return mainPageService.getLocalPost(local);
    }
}
