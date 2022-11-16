package com.move.TripBalance.mainpage.service;

import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class MapService {

    // 카카오 앱키
    @Value("${kakao.key}")
    private String key;
    // 카카오맵 api url
    private String url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?";

    // 카카오맵 api를 통해 위도, 경도로 지역 정보 추출
    public ResponseEntity kakaoMap(LocationRequestDto requestDto) {

        // 클라이언트에서 넘겨받은 위도 경도
        String latRes = requestDto.getLat();
        String lonRes = requestDto.getLng();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + key); //Authorization 설정
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders); //엔티티로 만들기
        URI targetUrl = UriComponentsBuilder
                .fromUriString(url) //기본 url
                .queryParam("x", lonRes) //인자
                .queryParam("y", latRes) //인자
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        //GetForObject는 헤더를 정의할 수 없음
        ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);
        ResponseEntity response= restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);
        return response; //내용 반환
    }

    // 법정동 코드 추출을 위해 지역 정보 반환
    public String mapCode(LocationRequestDto requestDto) throws ParseException {

        // kakaoMap 메소드에서 받은 정보 json parsing
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(kakaoMap(requestDto).getBody().toString());
        // documents만 도출
        JSONArray docuArray = (JSONArray) jsonObject.get("documents");
        // 첫번째 배열만 도출
        JSONObject docuObject = (JSONObject) docuArray.get(0);
        // 법정동 주소의 첫번째, 두번째 구역만 도출
        String regionOne = docuObject.get("region_1depth_name").toString();
        String regionTwo = docuObject.get("region_2depth_name").toString();
        // 넘겨줘야 할 법정동 주소
        String regionName = regionOne + " " + regionTwo;

        return regionName;
    }


}