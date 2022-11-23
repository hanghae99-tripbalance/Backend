package com.move.TripBalance.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMemberResponseDto {
    private String email;
    private String nickName;
}
