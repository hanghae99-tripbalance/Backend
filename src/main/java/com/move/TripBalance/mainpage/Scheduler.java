package com.move.TripBalance.mainpage;

import com.move.TripBalance.mainpage.repository.ResultRepository;
import com.move.TripBalance.mainpage.service.ApiService;
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

    // 매달 1일 23시에 저번달의 인구 통계를 가져오는 스케쥴러
    @Scheduled(cron = "0 0 23 1 * ?")
    public void mapResult() throws IOException, ParseException {
        apiService.getResultList();
    }

}
