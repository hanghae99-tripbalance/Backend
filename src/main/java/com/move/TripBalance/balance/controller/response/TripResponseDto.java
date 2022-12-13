package com.move.TripBalance.balance.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {
    private String Trip;
    private String Tripcontent;
}
