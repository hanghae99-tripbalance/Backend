package com.move.TripBalance.mainpage;

import com.move.TripBalance.mainpage.repository.ResultRepository;
import com.move.TripBalance.mainpage.service.ApiService;
import com.move.TripBalance.result.repository.BlogRepository;
import com.move.TripBalance.result.repository.HotelRepository;
import com.move.TripBalance.result.service.ResultService;
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
    private final ResultService resultService;
    private final HotelRepository hotelRepository;
    private final BlogRepository blogRepository;

    // 매달 2일 오전 5시에 저번달의 인구 통계를 가져오는 스케쥴러
    @Scheduled(cron = "0 0 5 2 * ?", zone = "Asia/Seoul")
    public void mapResult() throws IOException, ParseException {
        apiService.getResultList();
    }

    // 매일 오전 4시에 메인페이지의 호텔, 블로그 정보 크롤링하는 스케줄러
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul")
    public void saveResult() throws ParseException {
        hotelRepository.deleteAll();
        blogRepository.deleteAll();
        resultService.saveHotels();
        resultService.saveBlogs();
    }
}
