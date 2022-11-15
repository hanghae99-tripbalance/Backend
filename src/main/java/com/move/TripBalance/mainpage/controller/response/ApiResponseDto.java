package com.move.TripBalance.mainpage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponseDto<T> {
    private T apiData;
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>( data);
    }

}
