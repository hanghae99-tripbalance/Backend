package com.move.TripBalance.mypage.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Nullable
public class MyImgRequestDto {
    private String profileImg;
}
