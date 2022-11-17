package com.move.TripBalance.mypage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyHeartResponseDto {
    private Long postId;
    private String title;
    private String nickName;
    private String profileImg;
    private String img;
}
