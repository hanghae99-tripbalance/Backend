package com.move.TripBalance.mypage.controller;

import com.move.TripBalance.mypage.controller.request.MyImgRequestDto;
import com.move.TripBalance.mypage.service.MyPageService;
import com.move.TripBalance.mypage.controller.request.SNSRequestDto;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MyPageController {
    private final MyPageService myPageService;

    // 소개글 수정
    @PostMapping("/mypage/myself")
    public ResponseEntity<PrivateResponseBody> setMySelf(@RequestBody JSONObject self, HttpServletRequest request){
        return myPageService.mySelf(self, request);}

    // 프로필 이미지 수정
    @PostMapping("/mypage/myimg")
    public ResponseEntity<PrivateResponseBody> setMyImg(@RequestBody MyImgRequestDto requestDto, HttpServletRequest request){
        return myPageService.myImg(requestDto, request);}

    // sns 주소 등록
    @PostMapping("/mypage/mysns")
    public ResponseEntity<PrivateResponseBody> setMySns(@RequestBody SNSRequestDto requestDto, HttpServletRequest request){
        return myPageService.mySns(requestDto, request);}

    // 내가 작성한 글 목록
    @GetMapping(value = "/mypage/posts/{page}")
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request, @PathVariable int page) {
        return myPageService.getMyPosts(request, page); }

    // 내가 좋아요한 글 목록
    @GetMapping(value = "/mypage/hearts/{page}")
    public ResponseEntity<PrivateResponseBody> getMyHearts(HttpServletRequest request, @PathVariable int page) {
        return myPageService.getMyHeartPosts(request, page); }

    // 회원의 개인정보 확인
    @GetMapping(value = "/members/info/{memberId}")
    public ResponseEntity<PrivateResponseBody> getMemberInfo(@PathVariable Long memberId){
        return myPageService.getMemberInfo(memberId); }
}
