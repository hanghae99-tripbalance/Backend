package com.move.TripBalance.comment.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    //게시글 id
    private Long postId;

    //댓글
    private String content;
}
