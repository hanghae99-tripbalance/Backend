package com.move.TripBalance.mypage.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class SNSRequestDto {
    private String insta;
    private String facebook;
    private String youtube;
    private String blog;
}
