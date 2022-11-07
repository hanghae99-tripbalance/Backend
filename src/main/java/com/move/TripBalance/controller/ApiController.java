package com.move.TripBalance.controller;

import com.move.TripBalance.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ApiService apiService;

    @GetMapping("/apitest")
    public String getApi() throws IOException, ParseException {
        return apiService.getPeopleNum();
    }

}
