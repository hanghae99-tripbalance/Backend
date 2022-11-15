package com.move.TripBalance.mainpage.controller.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalResponseDto {
    private String title;
    private String content;
    private String localdetail;
    private String img;
}
