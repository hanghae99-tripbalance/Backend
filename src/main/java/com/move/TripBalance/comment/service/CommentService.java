package com.move.TripBalance.comment.service;

import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.ReComment;
import com.move.TripBalance.comment.controller.request.CommentRequestDto;
import com.move.TripBalance.comment.controller.response.CommentResponseDto;
import com.move.TripBalance.comment.controller.response.ReCommentResponseDto;
import com.move.TripBalance.comment.repository.CommentRepository;
import com.move.TripBalance.comment.repository.ReCommentRepository;
import com.move.TripBalance.post.service.PostService;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Component
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    private final TokenProvider tokenProvider;
    private final PostService postService;

    //댓글 작성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
        //멤버 확인
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }
        //게시글 확인
        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }
        //레포에 저장
        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(requestDto.getContent())
                .author(member.getNickName())
                .build();
        commentRepository.save(comment);
        //response에 담아서 보내주기
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                CommentResponseDto.builder()
                        .commentId(comment.getCommentId())
                        .author(comment.getMember().getNickName())
                        .content(comment.getContent())
                        .profileImg(member.getProfileImg())
                        .authorId(comment.getMember().getMemberId())
                        .build()), HttpStatus.OK);
    }

    //게시글 별 코멘트 보기
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getAllCommentsByPost(Long postId) {
        //post 확인
        Post post = postService.isPresentPost(postId);
        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }
        //댓글 확인하기
        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
            List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();
            // 해당 댓글에 달린 대댓글 담기
            for (ReComment reComment : reCommentList) {
                reCommentResponseDtoList.add(ReCommentResponseDto.builder()
                        .commentId(reComment.getComment().getCommentId())
                        .recommentId(reComment.getRecommentId())
                        .author(reComment.getMember().getNickName())
                        .content(reComment.getContent())
                        .profileImg(reComment.getMember().getProfileImg())
                        .authorId(reComment.getMember().getMemberId())
                        .build());
            }
            // commentResponseDto에 여러 댓글 담기
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .commentId(comment.getCommentId())
                            .author(comment.getAuthor())
                            .content(comment.getContent())
                            .reComments(reCommentResponseDtoList)
                            .profileImg(comment.getMember().getProfileImg())
                            .authorId(comment.getMember().getMemberId())
                            .build()
            );
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                commentResponseDtoList), HttpStatus.OK);
    }

    //댓글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updateComment(Long commentId, CommentRequestDto requestDto, HttpServletRequest request) {
        //멤버 확인
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }
        //게시글 확인
        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }
        //댓글 확인
        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        //댓글 업댓완료
        comment.update(requestDto);

        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
        List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();
        for (ReComment reComment : reCommentList) {
            reCommentResponseDtoList.add(
                    ReCommentResponseDto.builder()
                            .recommentId(reComment.getRecommentId())
                            .content(reComment.getContent())
                            .author(reComment.getMember().getNickName())
                            .authorId(reComment.getMember().getMemberId())
                            .build()
            );
        }
        //댓글 결과 리턴
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                CommentResponseDto.builder()
                        .commentId(comment.getCommentId())
                        .author(comment.getMember().getNickName())
                        .content(comment.getContent())
                        .reComments(reCommentResponseDtoList)
                        .profileImg(member.getProfileImg())
                        .authorId(comment.getMember().getMemberId())
                        .build()), HttpStatus.OK);
    }

    //댓글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deleteComment(Long commentId, HttpServletRequest request) {
        //멤버 확인
        Member member = validateMember(request);
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.LOGIN_EXPIRED_JWT_TOKEN, null), HttpStatus.OK);
        }
        //댓글확인
        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST_COMMENT, null), HttpStatus.OK);
        }
        //댓글 멤버 확인
        if (comment.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        //댓글 삭제하기
        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
        for (ReComment reComment : reCommentList) {
            reCommentRepository.delete(reComment);
        }
        //댓글 삭제하기
        commentRepository.delete(comment);
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "댓글 삭제 완료"), HttpStatus.OK);
    }

    //id 별 확인
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    //멤버 확인
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
