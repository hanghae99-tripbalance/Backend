package com.move.TripBalance.mainpage.controller;

import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.service.ApiService;
import com.move.TripBalance.mainpage.service.MainPageService;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.controller.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class MainPageController {

    private final MainPageService mainPageService;
    private final ApiService apiService;
    @GetMapping("/tb/bestfive")
    public ResponseDto<?> getBestFive(UserDetailsImpl userDetails){
        return mainPageService.getTop5Posts(userDetails);
    }
    @GetMapping("/tb/apimap")
    public void getApi() throws IOException, ParseException {
        apiService.getResultList();
    }
    @PostMapping("/tb/apimap")
    public JSONObject getLocation(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }
    @GetMapping("/tb/localpost/{local}")
    public ResponseDto<?> getLocalPostList(@PathVariable Long local){
        return mainPageService.getLocalPost(local);
    }
}
