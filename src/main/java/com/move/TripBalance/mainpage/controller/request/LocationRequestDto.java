package com.move.TripBalance.mainpage.controller.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequestDto {
    private String lat;
    private String lng;
    private String location;
}
