package com.move.TripBalance.controller;

import com.move.TripBalance.controller.response.ApiResponseDto;
import com.move.TripBalance.domain.Result;
import com.move.TripBalance.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ApiService apiService;

    @GetMapping("/tb/apitest")
    public ApiResponseDto<?> getApi() throws IOException, ParseException {
        return ApiResponseDto.success(apiService.getPeopleNum());
    }

}
