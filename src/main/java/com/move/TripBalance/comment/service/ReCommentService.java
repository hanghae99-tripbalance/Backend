package com.move.TripBalance.comment.service;


import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.ReComment;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.comment.controller.response.ReCommentResponseDto;
import com.move.TripBalance.comment.repository.ReCommentRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.exception.PrivateException;
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
        //멤버 확인
        Member member = authorizeToken(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }
        //댓글 확인
        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        //대댓글repo에 저장
        ReComment reComment = ReComment.builder()
                .comment(comment)
                .member(member)
                .content(requestDto.getContent())
                .author(member.getNickName())
                .build();
        reCommentRepository.save(reComment);
        //객체 담기
        ReCommentResponseDto reCommentResponseDto = ReCommentResponseDto.builder()
                .commentId(reComment.getComment().getCommentId())
                .recommentId(reComment.getRecommentId())
                .author(reComment.getAuthor())
                .content(reComment.getContent())
                .profileImg(member.getProfileImg())
                .authorId(reComment.getMember().getMemberId())
                .build();

        // 객체 Return
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                reCommentResponseDto),HttpStatus.OK);
    }

    //대댓글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updateReComment(
            Long reCommentId,
            ReCommentRequestDto requestDto,
            HttpServletRequest request
    ) {
        Member member = authorizeToken(request);
        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        ReComment reComment = isPresentReComment(reCommentId);
        //멤버 확인
        if (reComment.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        //댓글 확인
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        //대댓글 수정
        reComment.update(requestDto);
        //확인 되면 리턴
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                ReCommentResponseDto.builder()
                        .commentId(reComment.getComment().getCommentId())
                        .recommentId(reComment.getRecommentId())
                        .author(reComment.getAuthor())
                        .content(reComment.getContent())
                        .profileImg(member.getProfileImg())
                        .authorId(reComment.getMember().getMemberId())
                        .build()),HttpStatus.OK);

    }

    //대댓글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deleteReComment(
            Long reCommentId,
            HttpServletRequest request
    ) {
        //멤버확인
        Member member = authorizeToken(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }
        //대댓글 id 확인
        ReComment reComment = isPresentReComment(reCommentId);
        if (null == reComment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_RECOMMENT, null), HttpStatus.OK);
        }
        //대댓글 멤버 확인
        if (reComment.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }

        //대댓글 삭제
        reCommentRepository.delete(reComment);

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK,"대댓글 삭제 완료"),HttpStatus.OK);
    }

    //id 별 확인
    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long id) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(id);
        return optionalReComment.orElse(null);
    }
    // 토큰 확인 여부
    @Transactional
    public Member authorizeToken(HttpServletRequest request) {

        // Access 토큰 유효성 확인
        if (request.getHeader("Authorization") == null) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Refresh 토큰 유요성 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Access, Refresh 토큰 유효성 검증이 완료되었을 경우 인증된 유저 정보 저장
        Member member = tokenProvider.getMemberFromAuthentication();

        // 인증된 유저 정보 반환
        return member;
    }
}
