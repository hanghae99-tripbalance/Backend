package com.move.TripBalance.controller;

import com.move.TripBalance.service.ApiService;
import com.move.TripBalance.service.MapService;
import com.move.TripBalance.service.WeatherService;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/tb")
@RestController
public class ApiController {

    private final ApiService apiService;

    private final MapService mapService;

    private final WeatherService weatherService;

    @GetMapping("/apitest")
    public String getApi(@RequestParam Double lon, @RequestParam Double lat) throws IOException, ParseException {
        return apiService.getLawCode(lon,lat);
    }

    @GetMapping("/kakao")
    public String getMap(@RequestParam Double lon, @RequestParam Double lat) throws ParseException {
        return mapService.mapCode(lon, lat);
    }

    @GetMapping("/weather")
    public JSONObject getWeather(@RequestParam String lat, @RequestParam String lon) throws IOException, ParseException {
        return weatherService.getWeather(lat, lon);
    }
}
