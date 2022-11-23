package com.move.TripBalance.comment.controller;

import com.move.TripBalance.comment.service.ReCommentService;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.shared.configuration.SwaggerAnnotation;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@RequestMapping("/tb")
@RestController
@RequiredArgsConstructor
public class ReCommentController {

    private final ReCommentService reCommentService;

    //대댓글 작성
    @SwaggerAnnotation
    @PostMapping(value = "/recomments")
    public ResponseEntity<PrivateResponseBody> createReComment(@RequestBody ReCommentRequestDto requestDto,
                                                               HttpServletRequest request) {
        return reCommentService.createReComment(requestDto, request);
    }

    //대댓글 수정
    @SwaggerAnnotation
    @PutMapping(value = "/recomments/{reCommentId}")
    public ResponseEntity<PrivateResponseBody> updateReComment(@PathVariable Long reCommentId, @RequestBody ReCommentRequestDto requestDto,
                                          HttpServletRequest request) {
        return reCommentService.updateReComment(reCommentId, requestDto, request);
    }

    //대댓글 삭제
    @SwaggerAnnotation
    @DeleteMapping(value = "/recomments/{reCommentId}")
    public ResponseEntity<PrivateResponseBody> deleteReComment(@PathVariable Long reCommentId,
                                          HttpServletRequest request) {
        return reCommentService.deleteReComment(reCommentId, request);
    }
}
