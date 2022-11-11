package com.move.TripBalance.service;

import com.move.TripBalance.apiDB.ResultAge;
import com.move.TripBalance.apiDB.ResultComp;
import com.move.TripBalance.apiDB.ResultGender;
import com.move.TripBalance.controller.request.LocationRequestDto;
import com.move.TripBalance.domain.Result;
import com.move.TripBalance.repository.ResultRepository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

import java.util.*;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class ApiService {
    //sk api 서비스를 호출하기 위한 appkey
    @Value(value = "${sk.api.appkey}")
    String appkey;

    // 법정지역코드를 불러오기 위한 api key
    @Value(value = "${region.api.code}")
    String regionCode;

    private final ResultRepository resultRepository;

    private final MapService mapService;
    private final WeatherService weatherService;

   /* public void getGraph() {
        String result = "서울특별시 종로구";
        if (resultRepository.findByLocation(result).size() != 0) {
            Result result1 = new Result();
        }
    }*/

    public String getLawCode(LocationRequestDto requestDto) throws IOException, ParseException {

        String result = mapService.mapCode(requestDto);

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

        return lawCode;
    }

    public JSONObject getPeopleNum(LocationRequestDto requestDto) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient();

        List<String> genGrp = new ArrayList<>();
        genGrp.add("male");
        genGrp.add("female");
        genGrp.add("all");

        List<String> ageGrp = new ArrayList<>();
        ageGrp.add("10");
        ageGrp.add("20");
        ageGrp.add("30");
        ageGrp.add("40");
        ageGrp.add("50");
        ageGrp.add("60_over");
        ageGrp.add("all");

        List<String> companion = new ArrayList<>();
        companion.add("family");
        companion.add("not_family");
        companion.add("family_w_child");
        companion.add("all");

        List<Result> resultList = new ArrayList<>();
        List<ResultGender> resultGenderList = new ArrayList<>();

        // 그래프를 그리기 위해 JSONObject 형태로 담아서 클라이언트로 전송
        JSONObject jsonObject = new JSONObject();

        Request request = new Request.Builder()
                .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                        getLawCode(requestDto) +
                        "?gender=all&ageGrp=all&companionType=all")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("appkey", appkey)
                .build();

        Response response = client.newCall(request).execute();
        String resString = response.body().string();

        //복잡한 JSON 파일 파싱
        JSONParser jsonParser = new JSONParser();
        JSONObject resultJson = (JSONObject) jsonParser.parse(resString);
        JSONObject resContents = (JSONObject) resultJson.get("contents");
        //지역 이름 추출
        String districtName = (String) resContents.get("districtName");
        JSONObject jsonRaw = (JSONObject) resContents.get("raw");
        //방문객 수 추출
        Long results;


        JSONArray gen_arr = new JSONArray();

        // 성별을 기준으로 정보 출력
        for (String gender : genGrp){
            JSONObject genData = new JSONObject();

            // 이미 저장된 내역이 있다면 repository 에서 불러오기
            List<Result> repoResult = resultRepository.findByLocation(districtName);
            Result genResult = resultRepository.findByLocationAndGender(districtName, gender);
            if (genResult != null) {
                gender = genResult.getGender();
                results = genResult.getPeopleCnt();
                ResultGender resultGender = new ResultGender();
                resultGender.setLocation(districtName);
                resultGender.setGender(gender);
                resultGender.setPeopleCnt(results);
                resultGenderList.add(resultGender);
                resultList.add(genResult);
                genData.put("Gender", resultGender);
                System.out.println("성별 리스트: " + resultGenderList);
                System.out.println("최종리스트: " + resultList);

                gen_arr.add(genData);
                jsonObject.put("GenderData", gen_arr);

            } else if (genResult == null) {
                // 최초로 그 지역의 정보를 불러오는 거라면 새로 추출
                Request requestGen = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                getLawCode(requestDto) +
                                "?gender=" +
                                gender + "&ageGrp=all&companionType=all")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("appkey", appkey)
                        .build();

                Response responseGen = client.newCall(requestGen).execute();
                String genString = responseGen.body().string();

                //복잡한 JSON 파일 파싱
                JSONParser parser = new JSONParser();
                JSONObject resJson = (JSONObject) parser.parse(genString);
                JSONObject contents = (JSONObject) resJson.get("contents");
                //지역 이름 추출
                String genDistrictName = (String) contents.get("districtName");
                JSONObject jsonRow = (JSONObject) contents.get("raw");
                //방문객 수 추출
                Long genResults = (Long) jsonRow.get("travelerCount");

                ResultGender resultGender = new ResultGender();
                resultGender.setPeopleCnt(genResults);
                resultGender.setGender(gender);
                Result genderResults = new Result();
                genderResults.setPeopleCnt(genResults);
                genderResults.setGender(gender);
                genderResults.setLocation(genDistrictName);

                resultRepository.save(genderResults);
                resultGenderList.add(resultGender);
                resultList.add(genderResults);
                genData.put("Gender", resultGender);
                System.out.println("새로운 성별 리스트: " + resultGenderList);
                System.out.println("새로운 최종리스트: " + resultList);
                gen_arr.add(genData);
                jsonObject.put("GenderData", gen_arr);
            }
        }
        List<ResultAge> resultAgeList = new ArrayList<>();
        JSONArray age_arr = new JSONArray();

        // 연령대를 기준으로 정보 추출하기
        for (String age : ageGrp) {
            JSONObject ageData = new JSONObject();
            // 이미 저장된 내역이 있다면 repository 에서 불러오기
            List<Result> repoResult = resultRepository.findByLocation(districtName);
            Result ageResult = resultRepository.findByLocationAndAge(districtName, age);

            if (ageResult != null) {
                age = ageResult.getAge();
                results = ageResult.getPeopleCnt();
                ResultAge resultAge = new ResultAge();
                resultAge.setLocation(districtName);
                resultAge.setAge(age);
                resultAge.setPeopleCnt(results);
                resultAgeList.add(resultAge);
                resultList.add(ageResult);
                ageData.put("Age", resultAge);
                System.out.println("나이대별 리스트: " + resultAgeList);
                System.out.println("최종리스트: " + resultList);
                age_arr.add(ageData);
                jsonObject.put("AgeData", age_arr);

            } else if (ageResult == null) {

                Request requestAge = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                getLawCode(requestDto) +
                                "?gender=all&ageGrp=" + age +
                                "&companionType=all")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("appkey", appkey)
                        .build();

                Response responseAge = client.newCall(requestAge).execute();
                String ageString = responseAge.body().string();

                JSONParser parser = new JSONParser();
                JSONObject resJson = (JSONObject) parser.parse(ageString);

                JSONObject arr = (JSONObject) resJson.get("contents");
                String ageDistrictName = (String) arr.get("districtName");
                JSONObject jsonRow = (JSONObject) arr.get("raw");
                Long ageResults = (Long) jsonRow.get("travelerCount");

                // 최초로 그 지역의 정보를 불러오는 거라면 새로 추출
                ResultAge resultAge = new ResultAge();
                resultAge.setPeopleCnt(ageResults);
                resultAge.setAge(age);
                Result ageRes = new Result();
                ageRes.setPeopleCnt(ageResults);
                ageRes.setAge(age);
                ageRes.setLocation(ageDistrictName);

                resultRepository.save(ageRes);
                resultAgeList.add(resultAge);
                resultList.add(ageRes);
                ageData.put("Age", resultAge);
                System.out.println("새로운 나이대별 리스트: " + resultAgeList);
                System.out.println("새로운 최종리스트: " + resultList);
                age_arr.add(ageData);
                jsonObject.put("AgeData", age_arr);
            }
        }
        List<ResultComp> resultCompList = new ArrayList<>();
        JSONArray com_arr = new JSONArray();
        for (String comp : companion) {

            JSONObject comData = new JSONObject();

            // 이미 저장된 내역이 있다면 repository 에서 불러오기
            List<Result> repoResult = resultRepository.findByLocation(districtName);
            Result compResult = resultRepository.findByLocationAndType(districtName, comp);

            if (compResult != null) {
                comp = compResult.getType();
                results = compResult.getPeopleCnt();
                ResultComp resultComp = new ResultComp();
                resultComp.setLocation(districtName);
                resultComp.setType(comp);
                resultComp.setPeopleCnt(results);
                resultCompList.add(resultComp);
                resultList.add(compResult);
                comData.put("Type", resultComp);
                System.out.println("타입별 리스트: " + resultCompList);
                System.out.println("최종리스트: " + resultList);
                com_arr.add(comData);
                jsonObject.put("CompData", com_arr);

            } else if (compResult == null) {
                // 최초로 그 지역의 정보를 불러오는 거라면 새로 추출

                Request requestComp = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                getLawCode(requestDto) +
                                "?gender=all&ageGrp=all&companionType=" + comp)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("appkey", appkey)
                        .build();

                Response responseComp = client.newCall(requestComp).execute();
                String compString = responseComp.body().string();

                JSONParser parser = new JSONParser();
                JSONObject resJson = (JSONObject) parser.parse(compString);

                JSONObject arr = (JSONObject) resJson.get("contents");
                String comDistrictName = (String) arr.get("districtName");
                JSONObject jsonRow = (JSONObject) arr.get("raw");
                Long comResults = (Long) jsonRow.get("travelerCount");

                ResultComp resultComp = new ResultComp();
                resultComp.setPeopleCnt(comResults);
                resultComp.setType(comp);
                Result compResults = new Result();
                compResults.setPeopleCnt(comResults);
                compResults.setType(comp);
                compResults.setLocation(comDistrictName);
                resultRepository.save(compResults);
                resultCompList.add(resultComp);
                resultList.add(compResults);
                comData.put("Type", resultComp);
                System.out.println("새로운 타입별 리스트: " + resultCompList);
                System.out.println("새로운 최종리스트: " + resultList);
                com_arr.add(comData);
                jsonObject.put("CompData", com_arr);
            }
        }
        return jsonObject;
    }
    public JSONObject mapResult(LocationRequestDto requestDto) throws IOException, ParseException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("peopleCnt", getPeopleNum(requestDto));
        resultObj.put("weather", weatherService.getWeather(requestDto));
        return  resultObj;
    }
}