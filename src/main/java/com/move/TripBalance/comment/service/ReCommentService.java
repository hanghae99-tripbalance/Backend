package com.move.TripBalance.comment.service;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.ReComment;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.comment.controller.response.ReCommentResponseDto;
import com.move.TripBalance.comment.repository.ReCommentRepository;
import com.move.TripBalance.shared.exception.controller.response.ResponseDto;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final ReCommentRepository reCommentRepository;
    private final TokenProvider tokenProvider;
    private final CommentService commentService;

    //대댓글 작성
    @Transactional
    public ResponseDto<?> createReComment(
            ReCommentRequestDto requestDto,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

        ReComment reComment = ReComment.builder()
                .commentId(comment.getId())
                .member(member)
                .content(requestDto.getContent())
                .build();
        reCommentRepository.save(reComment);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .nickName(member.getNickName())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }
////  대댓글 보기
//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllReCommentByMember(HttpServletRequest request) {
//        Member member = validateMember(request);
//        if (null == member) {
//            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
//        }
//
//        List<ReComment> reCommentList = reCommentRepository.findAllByMember(member);
//        List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();
//
//        for (ReComment reComment : reCommentList) {
//            reCommentResponseDtoList.add(
//                    ReCommentResponseDto.builder()
//                            .id(reComment.getId())
//                            .author(reComment.getMember().getNickname())
//                            .content(reComment.getContent())
////                            .likes(countLikesReCommentLike(ReComment))
//                            .createdAt(reComment.getCreatedAt())
//                            .modifiedAt(reComment.getModifiedAt())
//                            .build()
//            );
//        }
//        return ResponseDto.success(reCommentResponseDtoList);
//    }

    //대댓글 수정
    @Transactional
    public ResponseDto<?> updateReComment(
            Long id,
            ReCommentRequestDto requestDto,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "Re comment id is not exist");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "only author can update");
        }

        reComment.update(requestDto);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .nickName(member.getNickName())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    //대댓글 삭제
    @Transactional
    public ResponseDto<?> deleteReComment(
            Long id,
            HttpServletRequest request
    ) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        Comment comment = commentService.isPresentComment(id);
        if (null == comment)
            return ResponseDto.fail("NOT_FOUND", "comment id is not exist");

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "Re comment id is not exist");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "only author can update");
        }

        reCommentRepository.delete(reComment);
        return ResponseDto.success("success");
    }

//    @Transactional(readOnly = true)
//    public int countLikesReCommentLike(ReComment ReComment) {
//        List<ReCommentLike> ReCommentLikeList = ReCommentLikeRepository.findAllByReComment(ReComment);
//        return ReCommentLikeList.size();
//    }

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
