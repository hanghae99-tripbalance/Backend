package com.move.TripBalance.mainpage.controller;

import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.service.ApiService;
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


    @GetMapping("/apitest")
    public void getApi() throws IOException, ParseException {
        apiService.getResultList();
    }
    @PostMapping("/apitest")
    public JSONObject getLocation(@RequestBody LocationRequestDto requestDto) throws IOException, ParseException {
        return apiService.mapResult(requestDto);
    }
}
