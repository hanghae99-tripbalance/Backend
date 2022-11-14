package com.move.TripBalance.controller;

import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping(value = "/tb/mypage/posts")
    public ResponseDto<?> getMyPosts(HttpServletRequest request) { return myPageService.getMyPosts(request); }

    @GetMapping(value = "/tb/mypage/hearts")
    public ResponseDto<?> getMyHearts(HttpServletRequest request) { return myPageService.getMyHeartPosts(request); }

}
