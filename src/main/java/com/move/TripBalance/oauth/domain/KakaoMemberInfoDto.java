package com.move.TripBalance.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoMemberInfoDto {

    // 아이디
    private Long id;

    // 카카오 계정 닉네임
    private String nickName;

    // 카카오 계정 이메일
    private String email;

    // 카카오 계정 프로필 이미지
    private String profileImg;
}
