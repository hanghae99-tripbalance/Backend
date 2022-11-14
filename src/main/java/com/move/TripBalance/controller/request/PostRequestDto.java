package com.move.TripBalance.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PostRequestDto {

    private String title;
    private String local;
    private int pet;
    private String content;

}
