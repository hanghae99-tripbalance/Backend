package com.move.TripBalance.result.controller;

import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.result.Blog;
import com.move.TripBalance.result.service.ResultService;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tb")
public class ResultController {

    private final ResultService resultService;

    // 밸런스 게임 결과에 따른 블로그 리스트
    @ResponseBody
    @GetMapping("/blog")
    public ResponseEntity<PrivateResponseBody> getAllBlog(String query) throws ParseException{
        return resultService.getGameBlog(query);
    }

    // 밸런스 게임 결과에 따른 호텔 리스트
    @ResponseBody
    @GetMapping("/hotel/{gameId}")
    public ResponseEntity<PrivateResponseBody> crawHotel(@PathVariable Long gameId) {
        return resultService.getGameHotel(gameId);
    }

}
