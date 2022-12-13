package com.move.TripBalance.mypage.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class MyPageRequestDto {
    private String nickName;
    private String profileImg;
    private String self;
    private String insta;
    private String facebook;
    private String youtube;
    private String blog;
}
