package com.move.TripBalance.controller.request;

import com.move.TripBalance.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@Getter
public class MediaRequestDto {

    private String imgURL;

}
