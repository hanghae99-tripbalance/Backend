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
public class PostResponseDto {

    // 고유 번호
    private Long postId;
    // 제목
    private String title;
    // 닉네임
    private String nickName;
    // 프로필 사진
    private String profileImg;
    // 작성자
    private String author;
    // 지역
    private String local;
    // 지역 세부사항
    private String localdetail;
    //반려동물
    private int pet;
    //내용
    private String content;
    //미디어
    private List<String> mediaList;
    //하나의사진
    private List<Media> image;
    //하트개수
    private Long heartNum;
    //하트 여부
    private boolean heartYn = false;
    //작성자 ID
    private Long authorId;
}
