package com.move.TripBalance.controller;

import com.move.TripBalance.configuration.SwaggerAnnotation;
import com.move.TripBalance.controller.request.PostRequestDto;
import com.move.TripBalance.domain.UserDetailsImpl;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.repository.PostRepository;
import com.move.TripBalance.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class PostController {

    private final PostService postService;
//    private final ImageUpload imageUpload;

    // 게시글 작성 (미디어 포함)
    @ResponseBody
    @PostMapping(value = "/posts", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PrivateResponseBody> createPost(
            @RequestBody PostRequestDto postRequestDto, // 게시글 작성을 위한 기입 정보들
            HttpServletRequest request) throws IOException { // 현재 로그인한 유저의 인증 정보를 확인하기 위한 HttpServletRequest
        return postService.createPost(postRequestDto, request);
    }

    //게시글 목록 조회
    @ResponseBody
    @GetMapping("/posts")
    public ResponseEntity<PrivateResponseBody> getAllPost() {
        return postService.getAllPost();
    }

    //게시글 상세 조회
    @ResponseBody
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PrivateResponseBody> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId, userDetails);
    }

    //게시글 수정 (미디어 포함)
    @SwaggerAnnotation
    @ResponseBody
//    @PutMapping(value = "/posts/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
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

    // 게시글 조회수
    @GetMapping("/posts/read/{memberId}")
    public ResponseEntity<PrivateResponseBody> read(@PathVariable Long memberId) {
        return postService.updateView(memberId);
    }

//    // 이미지 업로드
//    @ResponseBody
//    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PrivateResponseBody> mediaUpload(
//            @RequestPart(value = "file") MultipartFile multipartFiles){ // 처음 등록된 이미지, 업데이트할 이미지
//
//        return new ResponseEntity<>(new PrivateResponseBody(
//                StatusCode.OK,imageUpload.fileUpload(multipartFiles)), HttpStatus.OK);
//    }
}
