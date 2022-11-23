package com.move.TripBalance.result.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.repository.LocationRepository;
import com.move.TripBalance.mainpage.service.MapService;
import com.move.TripBalance.result.Blog;
import com.move.TripBalance.result.Hotel;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class  ResultService {

    private final GameChoiceRepository gameChoiceRepository;

    private final LocationRepository locationRepository;

    private final MapService mapService;

    // 다음 크롤링 하기 위한 API 키
    @Value("${kakao.key}")
    private String key;

    // 다음 블로그 크롤링 주소
    private String blogUrl = "https://dapi.kakao.com/v2/search/blog";

    // 숙소 크롤링 주소
    private String hotelUrl = "https://www.goodchoice.kr/product/result?keyword=";


    // 다음 블로그 크롤링
    public ResponseEntity daumCraw(String query) {

        // 크롤링 결과값 추출
        RestTemplate restTemplate = new RestTemplate();

        // 헤더에 필요한 값 넣기
        HttpHeaders httpHeaders = new HttpHeaders();

        //Authorization 설정, 발급받은 API 키 넣기
        httpHeaders.set("Authorization", "KakaoAK " + key);

        //엔티티로 만들기
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        // 전송할 최종 URL 만들기 및 인코딩
        URI targetUrl = UriComponentsBuilder
                .fromUriString(blogUrl)
                .queryParam("query", query + " 여행")
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity response= restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);

        return response; //내용 반환
    }

    // 블로그 크롤링한 결과값 담아서 보내기
    public List<Blog> getAllBlog(String query) throws ParseException {

        // 결과값 JSON 파싱
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(daumCraw(query).getBody().toString());

        // documents만 도출
        JSONArray docuArray = (JSONArray) jsonObject.get("documents");

        // 리스트에 담아서 반환
        List<Blog> blogList = new ArrayList<>();

        for(int i = 0; i< docuArray.size(); i++){

            JSONObject docuObject = (JSONObject) docuArray.get(i);
            if(!docuObject.get("thumbnail").toString().equals("")){

                // 블로그 객체 생성
                Blog blog = new Blog();

                // 블로그 아이디 할당
                blog.setId(i);

                // 블로그 이름
                blog.setBlogName(docuObject.get("blogname").toString());

                // 연결되는 URL
                blog.setUrl(docuObject.get("url").toString());

                // 게시글 제목 태그 제거 후 넣기
                blog.setTitle(docuObject.get("title").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;|&amp;|&lt;|&gt;]",""));

                // 게시글 내용 태그 제거 후 넣기
                blog.setContents(docuObject.get("contents").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;|&amp;|&lt;|&gt;]",""));

                // 게시글 첫 이미지
                blog.setThumbnail(docuObject.get("thumbnail").toString());

                // 반환할 리스트에 블로그 정보 추가
                blogList.add(blog);
            }
        }
        return blogList;
    }

    // 호텔 리스트 정보를 위해 gameId 에서 여행지 결과 도출
    public String hotel(Long gameId) {

        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 게임 결과를 검색어에 포함하여 결과 도출
        return gameResult.getGameResult();
    }

    // 크롤링한 호텔 정보 파싱 - 4개
    public List<Hotel> getHotel(String keyword){

        // 호텔 검색 결과를 담을 리스트 생성
        List<Hotel> hotels = new ArrayList<>();

        // 검색 키워드에 지역이름 추가
        String url = hotelUrl + keyword;

        // 필요한 정보 파싱
        try {
            Document doc = Jsoup.connect(url).get();
            Elements stockTableBody = doc.select("div.list_wrap li");

            for (int i =0; i < 4; i++) {
                Hotel hotel = new Hotel();

                // 호텔 정보 아이디 할당
                int id = i;

                // 호텔 이름
                String text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                // 호텔 이미지
                String img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                // 호텔 URL
                String URL = stockTableBody.get(i).select("a").attr("href");

                // 호텔 결과값 만들기
                hotel.setId(id);
                hotel.setTitle(text);
                hotel.setImg(img);
                hotel.setURL(URL);
                hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotels;
    }

    // 메인페이지용 숙소 8개
    public List<Hotel> getMoreHotel(String keyword){
        // 호텔 검색 결과를 담을 리스트 생성
        List<Hotel> hotels = new ArrayList<>();

        // 검색 키워드에 지역이름 추가
        String url = hotelUrl + keyword;

        // 필요한 정보 파싱
        try {
            Document doc = Jsoup.connect(url).get();
            Elements stockTableBody = doc.select("div.list_wrap li");

            for (int i =0; i < 8; i++) {
                Hotel hotel = new Hotel();

                // 호텔 정보 아이디 할당
                int id = i;

                // 호텔 이름
                String text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                // 호텔 이미지
                String img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                // 호텔 URL
                String URL = stockTableBody.get(i).select("a").attr("href");

                // 호텔 결과값 만들기
                hotel.setId(id);
                hotel.setTitle(text);
                hotel.setImg(img);
                hotel.setURL(URL);
                hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotels;
    }

    // 지도의 지역 정보 넘겨서 블로그 정보 크롤링
    public JSONArray getMapBlog(LocationRequestDto requestDto) throws ParseException {

        // 지역 정보 가져오기
        String location = requestDto.getLocation();

        // 공백 기준으로 두번째 단어 출력
        String[] strArr = location.split(" ");
        String locResult = strArr[1];

        // 최종 결과값에 넣기 위한 JSON 파싱
        JSONArray blogJson = new JSONArray();

        // JSONArray 에 블로그 크롤링 결과 담기
        blogJson.add(getAllBlog(locResult));

        return blogJson;
    }

    // 지도의 지역 정보 넘겨서 숙소 정보 크롤링
    public JSONArray getMapHotel(LocationRequestDto requestDto){

        // 지역 정보 가져오기
        String location = requestDto.getLocation();

        // 공백 기준으로 두번째 단어 출력
        String[] strArr = location.split(" ");
        String locResult = strArr[1];

        // 최종 결과값에 넣기 위한 JSON 파싱
        JSONArray blogJson = new JSONArray();

        // JSONArray 에 블로그 크롤링 결과 담기
        blogJson.add(getMoreHotel(locResult));

        return blogJson;
    }

    // 게임 결과를 통해 블로그 정보 크롤링
    public ResponseEntity<PrivateResponseBody> getGameBlog(String query) throws ParseException {
        List<Blog> gameBlog = getAllBlog(query);
        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, gameBlog), HttpStatus.OK);
    }

    // 게임 아이디를 통해 숙소 정보 가져오기
    public ResponseEntity<PrivateResponseBody> getGameHotel(Long gameId){
        String keyword = hotel(gameId);
        List<Hotel> gameHotel = getHotel(keyword);
        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, gameHotel), HttpStatus.OK);
    }

    //게임 아이디를 통해 여행지 확인
    @Transactional(readOnly = true)
    public GameResult isPresentGame(Long id) {
        Optional<GameResult> optionalGameResult = gameChoiceRepository.findById(id);
        return optionalGameResult.orElse(null);
    }
}
