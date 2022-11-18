package com.move.TripBalance.result.controller;

import com.move.TripBalance.result.Blog;
import com.move.TripBalance.result.service.ResultService;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tb")
public class ResultController {

    private final ResultService resultService;

    // Blog List
    @ResponseBody
    @GetMapping("/blog")
    public List<Blog> getAllBlog(@RequestParam("query") String query)throws ParseException {
        return resultService.getAllPost(query);
    }

    // Hotel List
    @ResponseBody
    @GetMapping("/hotel/{gameId}")
    public ResponseEntity<PrivateResponseBody> crawHotel(@PathVariable Long gameId) {
        return resultService.hotel(gameId);
    }

}