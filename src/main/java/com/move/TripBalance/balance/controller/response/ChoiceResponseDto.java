package com.move.TripBalance.balance.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceResponseDto {

    private Long gameId;

    private Long answer1;

    private Long answer2;

    private Long answer3;

    private Long answer4;

    private Long answer5;

    private String trip;

}
