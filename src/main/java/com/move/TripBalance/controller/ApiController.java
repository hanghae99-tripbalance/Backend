package com.move.TripBalance.controller;

import com.move.TripBalance.controller.request.LocationRequestDto;
import com.move.TripBalance.service.ApiService;
import com.move.TripBalance.service.MapService;
import com.move.TripBalance.service.WeatherService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class ApiController {

    private final ApiService apiService;

    private final MapService mapService;

    private final WeatherService weatherService;

    @GetMapping("/apitest")
    public JSONObject getApi(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }
    @PostMapping("/apitest")
    public JSONObject getLocation(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }

   /* @GetMapping("/kakao")
    public String getMap(@RequestBody LocationRequestDto requestDto) throws ParseException {
        return mapService.mapCode(requestDto);
    }

    @GetMapping("/weather")
    public JSONObject getWeather(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return weatherService.getWeather(requestDto);
    }*/
}
