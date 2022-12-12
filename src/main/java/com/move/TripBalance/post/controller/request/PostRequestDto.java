package com.move.TripBalance.post.controller.request;

import com.move.TripBalance.post.domain.Media;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String title;
    private String local;
    private String localdetail;
    private int pet;
    private String content;
    private List<Media> mediaList;

}
