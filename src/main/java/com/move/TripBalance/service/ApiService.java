package com.move.TripBalance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import lombok.Getter;
import lombok.Setter;

import nonapi.io.github.classgraph.json.JSONUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
@Setter
@Service
public class ApiService {
    @Value(value = "${sk.api.appkey}")
    String appkey;

    @Value(value = "${region.api.code}")
    String regionCode;
    public String getLawCode() throws IOException, ParseException {
        String result = "강원도 강릉시";

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + regionCode); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*호출문서(xml, json) default : xml*/
        urlBuilder.append("&" + URLEncoder.encode("locatadd_nm", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8")); /*지역주소명*/
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



        JSONArray arr = (JSONArray) obj.get("StanReginCd");
        JSONObject stanReginCd = (JSONObject) arr.get(1);

        JSONArray jsonRow = (JSONArray) stanReginCd.get("row");
        JSONObject results = (JSONObject) jsonRow.get(0);
      //  JSONObject code = (JSONObject) results.get("region_cd");
        String lawCode = (String) results.get("region_cd");

        String localName = (String) results.get("locallow_nm");

        return lawCode;

    }

    public String getPeopleNum() throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient();

        List<String> genGrp = new ArrayList<>();
        genGrp.add("male");
        genGrp.add("female");
        genGrp.add("all");

        List<String> ageGrp = new ArrayList<>();
        ageGrp.add("10"); ageGrp.add("20"); ageGrp.add("30"); ageGrp.add("40");ageGrp.add("50"); ageGrp.add("60_over"); ageGrp.add("all");

        List<String> companion = new ArrayList<>();
        companion.add("family"); companion.add("not_family"); companion.add("family_w_child"); companion.add("all");

        System.out.println(getLawCode());

        for (String gender : genGrp) {
            for(String age : ageGrp){
                for (String comp : companion){
                    Request request = new Request.Builder()
                            .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                    getLawCode() +
                                    "?gender=" +
                                    gender +
                                    "&ageGrp=" +
                                    age +
                                    "&companionType=" +
                                    comp)
                            .get()
                            .addHeader("accept", "application/json")
                            .addHeader("appkey", appkey)
                            .build();

                    Response response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    String jsonText = body.toString();

                    Gson gson = new Gson();
                    Map<String, Object> jsonObject = gson.fromJson(jsonText, new TypeToken<Map<String, Object>>(){}.getType());

                    List<Map<String, Object>> jsonList = (List) jsonObject.get("");
                    body.close();
                    System.out.println(jsonText);


                    System.out.println();
                    //배열 추출
                    System.out.println("저번달" + getLawCode() + "의" + gender +" 성별(male:남성, female:여성)"+ age +"대"+ comp + " 가족구성원 방문객 수는" );
                }
            }

        }
        return getPeopleNum();
    }

}
