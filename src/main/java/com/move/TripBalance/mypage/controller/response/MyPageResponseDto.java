package com.move.TripBalance.mypage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponseDto {
    private Long memberId;
    private String email;
    private String nickName;
    private String profileImg;
    private String self;
    private String facebook;
    private String youtube;
    private String blog;
    private String insta;
    private Long postCnt = 0L;
    private Long commentCnt = 0L;
    private Long gameCnt = 0L;
}
