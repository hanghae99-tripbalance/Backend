package com.move.TripBalance.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private int pet;
  //  private List<CommentResponseDto> commentResponseDtoList;
    private int heartCount;
    private int myHeart;
 //   private PostCategory postCategory;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}