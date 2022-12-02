package com.move.TripBalance.mainpage.controller;

import com.move.TripBalance.mainpage.Result;
import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MainPageController {

    private final ApiService apiService;

    // sk api로 인구 통계 불러오기
    @GetMapping("/apimap")
    public Result getApi() throws IOException, ParseException {
        return apiService.getResultList();
    }

    // 위도, 경도 정보를 받아서 인구통계와 날씨 정보, 블로그, 숙소 정보 반환
    @PostMapping("/apimap")
    public JSONObject getLocation(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }

    // 서울의 정보를 메인페이지에 기본으로 띄워주기
    @GetMapping("/apimap/seoul")
    public JSONObject getDefaultMap() throws IOException, ParseException {
        return apiService.seoulResult();
    }

}
