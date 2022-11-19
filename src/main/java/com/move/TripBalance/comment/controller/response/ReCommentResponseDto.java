package com.move.TripBalance.comment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentResponseDto {

    //대댓글id
    private Long recommentId;

    //작성자
    private String author;

    //대댓글
    private String content;

    //프로필 사진
    private String profileImg;
}
