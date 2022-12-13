package com.move.TripBalance.post.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseDto {

    private Boolean isLastPage;
    private List<PostResponseDto> postResponseDtoList;
}
