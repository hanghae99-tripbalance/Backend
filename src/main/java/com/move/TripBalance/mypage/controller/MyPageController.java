package com.move.TripBalance.mypage.controller;

import com.move.TripBalance.mypage.controller.request.MyPageRequestDto;
import com.move.TripBalance.mypage.controller.response.MyPageResponseDto;
import com.move.TripBalance.mypage.service.MyPageService;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MyPageController {
    private final MyPageService myPageService;

    // 내 정보 확인
    @GetMapping("/mypage/info")
    public ResponseEntity<PrivateResponseBody> getMyInfo(HttpServletRequest request){
        return myPageService.myInfo(request);}

    // 내 정보 수정
    @PutMapping("/mypage/setinfo")
    public MyPageResponseDto setMyInfo(@RequestBody MyPageRequestDto requestDto, HttpServletRequest request){
        return myPageService.setMyInfo(requestDto, request);}

    // 나의 밸런스 게임 여행지 통계
    @GetMapping("/mypage/tripdb")
    public ResponseEntity<PrivateResponseBody> getMyTrip(HttpServletRequest request){
        return myPageService.myTrip(request);}

    // 전체 밸런스 게임 여행지 통계
    @GetMapping("/mypage/totaldb")
    public ResponseEntity<PrivateResponseBody> getTotalTrip(){
        return myPageService.totalGame();}

    // 전체 중 상위 10개 밸런스 게임 여행지 통계
    @GetMapping("/mypage/totalten")
    public ResponseEntity<PrivateResponseBody> getTotalDB(){
        return myPageService.totalTenGame();
    }

        // 내가 작성한 글 목록
    @GetMapping(value = "/mypage/posts")
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request) {
        return myPageService.getMyPosts(request); }

    // 내가 좋아요한 글 목록
    @GetMapping(value = "/mypage/hearts")
    public ResponseEntity<PrivateResponseBody> getMyHearts(HttpServletRequest request) {
        return myPageService.getMyHeartPosts(request); }

    // 회원의 개인정보 확인
    @GetMapping(value = "/memberinfo/{memberId}")
    public ResponseEntity<PrivateResponseBody> getMemberInfo(@PathVariable Long memberId){
        return myPageService.getMemberInfo(memberId); }

    // 회원의 밸런스 게임 여행지 통계
    @GetMapping(value = "/memberinfo/tripdb/{memberId}")
    public ResponseEntity<PrivateResponseBody> getMemberTrip(@PathVariable Long memberId){
        return myPageService.getMemberTrip(memberId);
    }

    // 회원의 작성한 글 목록
    @GetMapping(value = "/memberinfo/posts/{memberId}")
    public ResponseEntity<PrivateResponseBody> getMemberPosts(@PathVariable Long memberId){
        return myPageService.getMemberPosts(memberId);}

    // 회원의 좋아요한 글 목록
    @GetMapping(value = "/memberinfo/hearts/{memberId}")
    public ResponseEntity<PrivateResponseBody> getMemberHearts(@PathVariable Long memberId){
        return myPageService.getMemberHeartPosts(memberId);}
}
