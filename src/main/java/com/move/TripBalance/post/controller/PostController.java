package com.move.TripBalance.post.controller;

import com.move.TripBalance.post.service.PostService;
import com.move.TripBalance.post.controller.request.PostRequestDto;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @ResponseBody
    @PostMapping(value = "/posts")
    public ResponseEntity<PrivateResponseBody> createPost(@RequestBody PostRequestDto postRequestDto,HttpServletRequest request){
        return postService.createPost(postRequestDto, request);
    }

    //게시글 목록 조회
    @ResponseBody
    @GetMapping("/posts/list/{page}")
    public ResponseEntity<PrivateResponseBody> getAllPost(@PathVariable int page) {
        return postService.getAllPost(page);
    }

    //게시글 상세 조회
    @ResponseBody
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId, userDetails);
    }

    //게시글 수정
    @ResponseBody
    @PutMapping(value = "/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> updatePost(
            @PathVariable Long postId, // 수정하고자 하는 게시글의 고유 ID
            @RequestBody PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest
        return postService.updatePost(postId, postRequestDto, request);
    }

    // 게시글 삭제
    @ResponseBody
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> deletePost(
            @PathVariable Long postId, HttpServletRequest request) {
        return postService.deletePost(postId, request);
    }

    // 게시글 검색
    @ResponseBody
    @GetMapping("/posts/search/{page}")
    public ResponseEntity<PrivateResponseBody> search(@RequestParam(value = "keyword") String keyword, @PathVariable int page){
        return postService.searchPosts(keyword, page);
    }

    // 카테고리별 게시글 검색
    @ResponseBody
    @GetMapping("/posts/search/{local}/{page}")
    public ResponseEntity<PrivateResponseBody> searchLocal(@PathVariable Long local, @RequestParam(value = "keyword") String keyword, @PathVariable int page){
        return postService.searchLocalPosts(local, keyword, page);
    }

    // 좋아요가 가장 많은 게시글 5개
    @GetMapping("/posts/bestfive")
    public ResponseEntity<PrivateResponseBody> getBestFive(){
        return postService.getTop5Posts();
    }

    //~님의 다른 글 보기
    @GetMapping(value = "/posts/otherpost/{memberId}")
    public ResponseEntity<PrivateResponseBody> getOtherPosts(@PathVariable Long memberId) {
        return postService.getOtherPosts(memberId);
    }

}
