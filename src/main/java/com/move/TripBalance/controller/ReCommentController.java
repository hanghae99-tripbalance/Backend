package com.move.TripBalance.controller;

import com.move.TripBalance.controller.request.ReCommentRequestDto;
import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.service.ReCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
@Validated
@RequiredArgsConstructor
@RestController
public class ReCommentController {
    private final ReCommentService reCommentService;
//  대댓글 생성
    @RequestMapping(value = "/tb/recomments", method = RequestMethod.POST)
    public ResponseDto<?> createComment(@RequestBody ReCommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return reCommentService.createReComment(requestDto, request);
    }
//  대댓글 수정
    @RequestMapping(value = "/tb/recomments/{id}", method = RequestMethod.POST)
    public ResponseDto<?> updateReComment(
            @PathVariable Long id,
            @RequestBody ReCommentRequestDto requestDto,
            HttpServletRequest request) {
        return reCommentService.updateReComment(id, requestDto, request);
    }
//  대댓글 삭제
    @RequestMapping(value = "/tb/recomments/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> createComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return reCommentService.deleteReComment(id, request);
    }
//    대댓글 보기
//    @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.GET)
//    public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
//        return reCommentService.getAllReCommentByMember(request);
//    }
}
