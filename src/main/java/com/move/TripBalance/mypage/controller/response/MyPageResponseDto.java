package com.move.TripBalance.mypage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<String> sns;
}
