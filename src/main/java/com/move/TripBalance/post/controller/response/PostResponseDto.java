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
    private Long postId;
    private String title;
    private String nickName;
    private String local;
    private String localdetail;
    private int pet;
    private String content;
    private List<String> mediaList;
    private List<Media> image;
    private Long heartNum;
    private boolean heartYn = false;

}
