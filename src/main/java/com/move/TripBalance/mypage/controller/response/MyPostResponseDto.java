package com.move.TripBalance.mypage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPostResponseDto {
    private Long postId;
    private String title;
    private String nickName;
    private String img;
    private LocalDateTime createdAt;
}
