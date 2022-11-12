package com.move.TripBalance.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.move.TripBalance.controller.request.LocationRequestDto;
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
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@Service
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherService {

    @Value(value = "${weather.key}")
    String key;

    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    public JSONObject getWeather(LocationRequestDto requestDto) throws IOException, ParseException {

        //받아온 위도와 경도를 앞 2,3자리 숫자만 가져오기
        String latRes = requestDto.getLat().substring(0, 2);
        String lonRes = requestDto.getLng().substring(0, 3);

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("12", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*매일 당일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*05시 발표(정시단위) */
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

        /**
         * REST API return 데이터 추출
         */
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
            valueObj = (JSONObject) arr.get(i); // 해당 item을 가져옵니다.
            Object obsrValue = valueObj.get("fcstValue");
            category = (String) valueObj.get("category");// item에서 카테고리를 검색해옵니다.
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
            // 검색한 카테고리와 일치하는 변수에 문자형으로 데이터를 저장합니다.
            datalist.put(category, obsrValue);
        }
        return datalist;
    }
}


