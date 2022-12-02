package com.move.TripBalance.mainpage.service;

import com.move.TripBalance.mainpage.Location;
import com.move.TripBalance.mainpage.Result;
import com.move.TripBalance.mainpage.apiDB.ResultAge;
import com.move.TripBalance.mainpage.apiDB.ResultComp;
import com.move.TripBalance.mainpage.apiDB.ResultGender;
import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.repository.LocationRepository;
import com.move.TripBalance.mainpage.repository.ResultRepository;
import com.move.TripBalance.result.service.ResultService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class ApiService {


    // application.properties 에서 appkey 정보 추출을 위한 import
    @Inject
    private Environment environment;
    private final ResultRepository resultRepository;
    private final LocationRepository locationRepository;

    private final MapService mapService;
    private final WeatherService weatherService;

    private final ResultService resultService;

    @Transactional
    public void getRepo() {
        System.out.println(resultRepository.findAllByLocationAndGender("전라남도 보성군", "male"));
    }

    // sk API 를 통해 받아온 인구 통계 결과를 DB에 저장하기
    @Transactional
    public Result getResultList() throws IOException, ParseException {

//        // 지난 달의 정보를 지워준다
//        resultRepository.deleteAll();
        Result result = new Result();

        OkHttpClient client = new OkHttpClient();

        // 성별 그룹
        List<String> genGrp = new ArrayList<>();
        genGrp.add("male");
        genGrp.add("female");

        // 연령대별 그룹
        List<String> ageGrp = new ArrayList<>();
        ageGrp.add("10");
        ageGrp.add("20");
        ageGrp.add("30");
        ageGrp.add("40");
        ageGrp.add("50");
        ageGrp.add("60_over");

        // 방문 형태별 그룹
        List<String> companion = new ArrayList<>();
        companion.add("family");
        companion.add("not_family");
        companion.add("family_w_child");

        // 저장되어있는 장소에서 위도 경도 추출
        List<Location> locationList = new ArrayList<>();

        // API 호출횟수 제한때문에 지역을 8개씩 끊어서 호출
        for (int j = 0; j < 3; j ++) {

        //sk api 서비스를 호출하기 위한 appkey 를 application.properties 에서 불러오기
        String appkey = environment.getProperty("sk.api.appkey." + j);

        // 새로운 페이지의 리스트를 위해 비워주기
        locationList.clear();

        // 0페이지부터, 8개씩, id를 기준으로 오름차순 페이징
        Pageable pageable = PageRequest.of(2, 8, Sort.by("id").ascending());

        // 새로운 페이지의 리스트 담아주기
        locationList.addAll(locationRepository.findAll(pageable).toList());


        for (int i = 2; i < locationList.size(); i++) {

            String regionCode = locationList.get(i).getCode();

            // 성별을 기준으로 정보 저장
            for (String gender : genGrp) {
                Request requestGen = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                regionCode +
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

                Result genderResults = Result.builder()
                        .gender(gender)
                        .location(genDistrictName)
                        .peopleCnt(genResults)
                        .build();
                resultRepository.saveAndFlush(genderResults);
                System.out.println(genderResults);
                System.out.println(resultRepository.findAllByLocationAndGender(genDistrictName, gender));
            }

            // 연령대를 기준으로 정보 저장
            for (String age : ageGrp) {
                Request requestAge = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                regionCode +
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

                Result ageRes = Result.builder()
                        .age(age)
                        .peopleCnt(ageResults)
                        .location(ageDistrictName)
                        .build();
                resultRepository.saveAndFlush(ageRes);
                System.out.println(ageRes);
                System.out.println(resultRepository.findAllByLocationAndAge(ageDistrictName, age));
            }
            // 가족 형태를 기준으로 정보 저장
            for (String comp : companion) {
                Request requestComp = new Request.Builder()
                        .url("https://apis.openapi.sk.com/puzzle/traveler-count/raw/monthly/districts/" +
                                regionCode +
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

                Result compResults = Result.builder()
                        .type(comp)
                        .location(comDistrictName)
                        .peopleCnt(comResults)
                        .build();

                resultRepository.saveAndFlush(compResults);
                System.out.println(compResults);
                System.out.println(resultRepository.findAllByLocationAndType(comDistrictName, comp));
            }
        }
        }return result;
    }


    // repo에 저장된 인구 통계를 바탕으로 그래프를 그릴 정보를 추출
    public JSONArray getPeopleNum(LocationRequestDto requestDto) {

        // 성별 그룹
        List<String> genGrp = new ArrayList<>();
        genGrp.add("male");
        genGrp.add("female");

        // 연령별 그룹
        List<String> ageGrp = new ArrayList<>();
        ageGrp.add("10");
        ageGrp.add("20");
        ageGrp.add("30");
        ageGrp.add("40");
        ageGrp.add("50");
        ageGrp.add("60_over");

        // 방문 형태별 그룹
        List<String> companion = new ArrayList<>();
        companion.add("family");
        companion.add("not_family");
        companion.add("family_w_child");

        // 그래프를 그리기 위해 JSONObject 형태로 담아서 클라이언트로 전송
        JSONArray peopleCnt = new JSONArray();

        // 지역명 정보 받아오기
        String location = requestDto.getLocation();

        // 지역명으로 저장된 정보 불러오기
        Location loca = locationRepository.findByResult(location);
        String districtName = loca.getResult();

        // 성별을 기준으로 정보 출력
        for (String gender : genGrp) {

            //DB 에서 저장된 정보 불러오기
            Result genResult = resultRepository.findByLocationAndGender(districtName, gender);
            if (genResult != null) {
                gender = genResult.getGender();
                Long results = genResult.getPeopleCnt();
                ResultGender resultGender = new ResultGender();
                resultGender.setLocation(districtName);
                resultGender.setGender(gender);
                resultGender.setPeopleCnt(results);
                peopleCnt.add(resultGender);
            }
        }

        // 연령대를 기준으로 정보 추출하기
        for (String age : ageGrp) {

            //DB 에서 저장된 정보 불러오기
            Result ageResult = resultRepository.findByLocationAndAge(districtName, age);
            if (ageResult != null) {
                age = ageResult.getAge();
                Long results = ageResult.getPeopleCnt();
                ResultAge resultAge = new ResultAge();
                resultAge.setLocation(districtName);
                resultAge.setAge(age);
                resultAge.setPeopleCnt(results);
                peopleCnt.add(resultAge);
            }
        }

        // 가족 형태를 기준으로 정보 추출하기
        for (String comp : companion) {

            //DB 에서 저장된 정보 불러오기
            Result compResult = resultRepository.findByLocationAndType(districtName, comp);

            if (compResult != null) {
                comp = compResult.getType();
                Long results = compResult.getPeopleCnt();
                ResultComp resultComp = new ResultComp();
                resultComp.setLocation(districtName);
                resultComp.setType(comp);
                resultComp.setPeopleCnt(results);
                peopleCnt.add(resultComp);
            }
        }
        return peopleCnt;
    }

    // 인구 통계와 날씨 정보를 클라이언트에 넘겨줌
    public JSONObject mapResult(LocationRequestDto requestDto) throws IOException, ParseException {

        JSONObject resultObj = new JSONObject();

        // 날씨와 인구 데이터 불러오기
        resultObj.put("cnt", getPeopleNum(requestDto));
        resultObj.put("weather", weatherService.getWeather(requestDto));
        resultObj.put("blog", resultService.getMapBlog(requestDto));
        resultObj.put("hotel", resultService.getMapHotel(requestDto));
        return resultObj;
    }

    // 기본 메인페이지에 서울 정보 띄워주기
    public JSONObject seoulResult() throws IOException, ParseException {

        JSONObject resultObj = new JSONObject();

        // 서울의 위도와 경도 정보를 넘겨주기
        String lat = "37.584009";
        String lng = "126.970626";
        String location = "서울특별시 종로구";

        // 위도와 경도 정보를 API에 넣기
        LocationRequestDto requestDto = LocationRequestDto.builder()
                .lat(lat)
                .lng(lng)
                .location(location)
                .build();

        // 날씨와 인구 데이터 불러오기
        resultObj.put("cnt", getPeopleNum(requestDto));
        resultObj.put("weather", weatherService.getWeather(requestDto));
        resultObj.put("blog", resultService.getMapBlog(requestDto));
        resultObj.put("hotel", resultService.getMapHotel(requestDto));

        return resultObj;
    }
}