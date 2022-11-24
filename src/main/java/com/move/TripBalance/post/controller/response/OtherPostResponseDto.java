package com.move.TripBalance.post.controller.response;

import com.move.TripBalance.post.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtherPostResponseDto {

    //PostId
    private Long postId;

    // 닉네임
    private String nickName;

    // 프로필 사진
    private String profileImg;

    // 제목
    private String title;

    // 지역
    private String local;

    //이미지
    private String img;

}
