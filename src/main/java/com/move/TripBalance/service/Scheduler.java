package com.move.TripBalance.service;

import com.move.TripBalance.repository.ResultRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {
    private final ResultRepository resultRepository;
    private final ApiService apiService;

    @Scheduled(cron = "0 0 23 1 * ?")
    public void mapResult() throws IOException, ParseException {
        apiService.getResultList();
    }

}
