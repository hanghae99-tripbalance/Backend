package com.move.TripBalance.mainpage.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherService {

    // 공공데이터 포털 키
    @Value(value = "${{ secrets.WEATHER_KEY }")
    String key;

    // 기상청 api를 통해 날씨 정보 불러오기
    public JSONObject getWeather(LocationRequestDto requestDto) throws IOException, ParseException {
        // api에 맞는 날짜 포맷 변환
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        String time = "0200";

        // 오전 2시보다 이른 시간이면 어제의 마지막 데이터를 반환
        if(hour <= 2){
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            time = "2000";
        }else if(hour > 2){
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        // 최대한 현재 시간과 가까운 시간의 예보 정보 불러오기
        if(2 < hour && hour <= 5 ){ time = "0200"; }
        if(5 < hour && hour <= 8 ){ time = "0500"; }
        if(8 < hour && hour <= 11 ){ time = "0800"; }
        if(11 < hour && hour <= 14 ){ time = "1100"; }
        if(14 < hour && hour <= 17 ){ time = "1400"; }
        if(17 < hour && hour <= 20 ){ time = "1700"; }
        if(20 < hour ){ time = "2000"; }

        // 받아온 위도와 경도를 앞 2,3자리 숫자만 가져오기
        String latRes = requestDto.getLat().substring(0, 2);
        String lonRes = requestDto.getLng().substring(0, 3);

        // 기상청 api 주소에서 정보 추출하기
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("12", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*매일 당일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8")); /*05시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(latRes, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(lonRes, "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        line = sb.toString();

        // REST API return 데이터 추출

        JSONParser jsonParser = new JSONParser();

        //JSON데이터를 넣어 JSON Object 로 만들어 준다.
        JSONObject obj = (JSONObject) jsonParser.parse(line);
        JSONObject parse_response = (JSONObject) obj.get("response");
        JSONObject parse_body = (JSONObject) parse_response.get("body");// response 로 부터 body 찾아오기
        JSONObject itemObj = (JSONObject) parse_body.get("items");
        JSONArray arr = (JSONArray) itemObj.get("item");

        JSONObject valueObj;
        String category;
        JSONObject datalist = new JSONObject();
        for (int i = 0; i < arr.size(); i++) {
            // 해당 item을 가져오기
            valueObj = (JSONObject) arr.get(i);
            Object obsrValue = valueObj.get("fcstValue");
            // 해당 category를 가져오기
            category = (String) valueObj.get("category");

            // 꼭 필요한 정보에 대해 영어 코드를 한글로 변환해서 클라이언트에 반환
            if(Objects.equals(category, "POP")){
               category = "강수확률";
            } else if (Objects.equals(category, "SKY")) {
                category = "하늘상태";
                if(Objects.equals(obsrValue, "1")){
                    obsrValue = "맑음";
                } else if (Objects.equals(obsrValue, "3")) {
                    obsrValue = "구름 많음";
                } else if (Objects.equals(obsrValue, "4")) {
                    obsrValue = "흐림";
                }
            } else if (Objects.equals(category, "TMP")) {
                category = "현재 기온";
            } else if (Objects.equals(category, "REH")) {
                category = "습도";
            }
            // 검색한 카테고리와 일치하는 변수에 문자형으로 데이터를 저장
            datalist.put(category, obsrValue);
        }
        return datalist;
    }
}


