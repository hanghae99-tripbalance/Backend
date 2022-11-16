package com.move.TripBalance.mypage;

import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping(value = "/mypage/posts")
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request) { return myPageService.getMyPosts(request); }

    @GetMapping(value = "/mypage/hearts")
    public ResponseEntity<PrivateResponseBody> getMyHearts(HttpServletRequest request) { return myPageService.getMyHeartPosts(request); }

}
