package com.move.TripBalance.mypage;

import com.move.TripBalance.mainpage.service.MainPageService;
import com.move.TripBalance.shared.exception.controller.response.ResponseDto;
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
