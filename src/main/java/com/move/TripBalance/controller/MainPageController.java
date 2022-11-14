package com.move.TripBalance.controller;

import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MainPageController {

    private final MainPageService mainPageService;
    @GetMapping("/tb/bestfive")
    public ResponseDto<?> getBestTen(){
        return mainPageService.getTop5Posts();
    }

}
