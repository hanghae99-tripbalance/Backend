package com.move.TripBalance.controller;

import com.move.TripBalance.configuration.SwaggerAnnotation;
import com.move.TripBalance.controller.request.PostRequestDto;
import com.move.TripBalance.domain.UserDetailsImpl;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.service.PostService;
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
//    private final ImageUpload imageUpload;

    // 게시글 작성 (미디어 포함)
    @SwaggerAnnotation
    @ResponseBody
    @PostMapping(value = "/posts")
    public ResponseEntity<PrivateResponseBody> createPost(@RequestBody PostRequestDto postRequestDto,HttpServletRequest request){
        return postService.createPost(postRequestDto, request);
    }

    //게시글 목록 조회
    @SwaggerAnnotation
    @ResponseBody
    @GetMapping("/posts")
    public ResponseEntity<PrivateResponseBody> getAllPost() {
        return postService.getAllPost();
    }

    //게시글 상세 조회
    @SwaggerAnnotation
    @ResponseBody
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId, userDetails);
    }

    //게시글 수정
    @SwaggerAnnotation
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
    @GetMapping("/posts/search")
    public ResponseEntity<PrivateResponseBody> search(@RequestParam(value = "keyword") String keyword){
        return postService.searchPosts(keyword);
    }

}
