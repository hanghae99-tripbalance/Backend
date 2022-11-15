package com.move.TripBalance.controller.response;

import com.move.TripBalance.domain.Media;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopFiveResponseDto {
    private String title;
    private String img;
    private Long heartNum;
    private boolean heartYn = false;
}
