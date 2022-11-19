package com.move.TripBalance.comment.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentRequestDto {
    //댓글Id
    private Long commentId;

    //대댓글
    private String content;
}
