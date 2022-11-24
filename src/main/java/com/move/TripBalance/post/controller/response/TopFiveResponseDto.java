package com.move.TripBalance.post.controller.response;


import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopFiveResponseDto {
    private int heartNum;
    private Long postId;
    private String title;
    private String img;
}