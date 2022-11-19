package com.move.TripBalance.comment.controller;

import javax.servlet.http.HttpServletRequest;

import com.move.TripBalance.comment.service.CommentService;
import com.move.TripBalance.comment.controller.request.CommentRequestDto;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tb")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글작성
    @PostMapping(value = "/comments")
    public ResponseEntity<PrivateResponseBody> createComment(@RequestBody CommentRequestDto requestDto,
                                                             HttpServletRequest request) {
        return commentService.createComment(requestDto, request);
    }

    //댓글 전체 가지고 오기
    @GetMapping(value = "/comments/{postId}")
    public ResponseEntity<PrivateResponseBody> getAllComments(@PathVariable Long postId) {
        return commentService.getAllCommentsByPost(postId);
    }

    //댓글 수정하기
    @PutMapping(value = "/comments/{commentId}")
    public ResponseEntity<PrivateResponseBody> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return commentService.updateComment(commentId, requestDto, request);
    }
    //댓글 삭제하기
    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseEntity<PrivateResponseBody> deleteComment(@PathVariable Long commentId,
                                        HttpServletRequest request) {
        return commentService.deleteComment(commentId, request);
    }


}