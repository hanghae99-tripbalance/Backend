package com.move.TripBalance.result.controller;

import com.move.TripBalance.result.Blog;
import com.move.TripBalance.result.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tb")
@RestController
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    //블로그 목록 조회
    @ResponseBody
    @GetMapping("/blog")
    public List<Blog> getAllBlog(@RequestParam("query") String query)throws ParseException {
        return blogService.getAllPost(query);
    }
}
