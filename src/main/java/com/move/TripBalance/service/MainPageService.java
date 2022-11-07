package com.move.TripBalance.service;

import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.repository.PostRepository;
import io.swagger.annotations.Api;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class MainPageService {

    private final PostRepository postRepository;

    private final ApiService apiService;

    @Transactional
    public ResponseDto<?> getTop5Posts(){
        return ResponseDto.success(postRepository.findTop5ByHearts(LocalDateTime.now()));
    }

    @Transactional
    public ResponseDto<?> getPeopleData() throws IOException, ParseException {

        return ResponseDto.success(apiService.getPeopleNum());
    }

}
