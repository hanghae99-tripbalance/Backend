package com.move.TripBalance.comment.service;


import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.ReComment;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.comment.controller.response.ReCommentResponseDto;
import com.move.TripBalance.comment.repository.ReCommentRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Component
public class ReCommentService {

    private final ReCommentRepository reCommentRepository;
    private final TokenProvider tokenProvider;
    private final CommentService commentService;

    //대댓글 생성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createReComment(
            ReCommentRequestDto requestDto,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        ReComment reComment = ReComment.builder()
                .comment(comment)
                .member(member)
                .content(requestDto.getContent())
                .build();
        reCommentRepository.save(reComment);
//객체 담기
        ReCommentResponseDto reCommentResponseDto = ReCommentResponseDto.builder()
                .recommentId(reComment.getRecommentId())
                .nickName(member.getNickName())
                .content(reComment.getContent())
                .build();

        // 객체 Return
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                reCommentResponseDto),HttpStatus.OK);
    }

    //대댓글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updateReComment(
            Long id,
            ReCommentRequestDto requestDto,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_RECOMMENT, null), HttpStatus.OK);
        }

        if (reComment.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        reComment.update(requestDto);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                ReCommentResponseDto.builder()
                        .recommentId(reComment.getRecommentId())
                        .nickName(member.getNickName())
                        .content(reComment.getContent())
                        .build()),HttpStatus.OK);

    }

    //대댓글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deleteReComment(
            Long id,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }

        Comment comment = commentService.isPresentComment(id);
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_RECOMMENT, null), HttpStatus.OK);
        }

        if (reComment.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }

        reCommentRepository.delete(reComment);

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK,"대댓글 삭제 완료"),HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long id) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(id);
        return optionalReComment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
