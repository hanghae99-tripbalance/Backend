package com.move.TripBalance.comment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    //댓글 id
    private Long commentId;

    //작성자
    private String author;

    //댓글
    private String content;

    //대댓글
    private List<ReCommentResponseDto> reComments;

    //사용자 이미지
    private String profileImg;
}

