package com.move.TripBalance.result.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
import com.move.TripBalance.mainpage.Location;
import com.move.TripBalance.mainpage.controller.request.LocationRequestDto;
import com.move.TripBalance.mainpage.repository.LocationRepository;
import com.move.TripBalance.result.Blog;
import com.move.TripBalance.result.Hotel;
import com.move.TripBalance.result.repository.BlogCustomRepositoryImpl;
import com.move.TripBalance.result.repository.BlogRepository;
import com.move.TripBalance.result.repository.HotelCustomRepositoryImpl;
import com.move.TripBalance.result.repository.HotelRepository;
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
import java.util.*;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class  ResultService {

    private final GameChoiceRepository gameChoiceRepository;

    private final LocationRepository locationRepository;
    private final HotelCustomRepositoryImpl hotelRepository;

    private final HotelRepository hotelRepo;
    private final BlogCustomRepositoryImpl blogRepository;

    private final BlogRepository blogRepo;

    // 다음 크롤링 하기 위한 API 키
    @Value("${kakao.key}")
    private String key;

    // 다음 블로그 크롤링 주소
    private String blogUrl = "https://dapi.kakao.com/v2/search/blog";

    // 숙소 크롤링 주소
    private String hotelUrl = "https://www.goodchoice.kr/product/result?keyword=";


    // 다음 블로그 크롤링
    public ResponseEntity daumCraw(String keyword) {

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
                .queryParam("query", keyword + " 여행")
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity response= restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);

        return response; //내용 반환
    }

    // 블로그 크롤링 결과 세팅
    public List<Blog> setBlogCraw(String query) throws ParseException {

        // 결과값 JSON 파싱
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(daumCraw(query).getBody().toString());

        // documents만 도출
        JSONArray docuArray = (JSONArray) jsonObject.get("documents");

        // 리스트에 담아서 반환
        List<Blog> blogList = new ArrayList<>();

        // 6개씩만 저장
        for (int i = 0; i < 6; i++) {

            JSONObject docuObject = (JSONObject) docuArray.get(i);

            // 썸네일 사진이 있는 결과만 추출
            if (!docuObject.get("thumbnail").toString().equals("")) {

                // 블로그 객체 생성
                Blog blog = new Blog();

                // 블로그 아이디 할당
                blog.setId(i);

                // 블로그 이름
                blog.setBlogName(docuObject.get("blogname").toString());

                // 연결되는 URL
                blog.setUrl(docuObject.get("url").toString());

                // 게시글 제목 태그 제거 후 넣기
                blog.setTitle(docuObject.get("title").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;|&amp;|&lt;|&gt;]", ""));

                // 게시글 내용 태그 제거 후 넣기
                blog.setContents(docuObject.get("contents").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;|&amp;|&lt;|&gt;]", ""));

                // 게시글 첫 이미지
                blog.setThumbnail(docuObject.get("thumbnail").toString());

                // 지역명
                blog.setLocation(query);

                // 반환할 리스트에 블로그 정보 추가
                blogList.add(blog);
            }
        }
        return blogList;
    }


    // 블로그 크롤링한 결과값 저장
    public void saveBlogs() throws ParseException {

        // 저장된 지역 정보 전부 불러오기
        List<Location> locationList = locationRepository.findAll();

        // 지역 하나씩 꺼내오기
        for(Location location : locationList) {

            // 지역 이름 추출
            String keyword = location.getResult();

            // 블로그 크롤링한 결과값
            List<Blog> blogList = setBlogCraw(keyword);

            // 블로그 DB에 저장
            blogRepo.saveAll(blogList);
        }
    }

    // 밸런스 게임 결과페이지를 위해 크롤링한 호텔 정보 파싱 - 4개
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
                hotel.setLocation(keyword);
                hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotels;
    }

    // 숙소 정보 저장 - 8개
    public void saveHotels(){

        // 저장된 지역 정보 전부 불러오기
        List<Location> locationList = locationRepository.findAll();

        // 지역 하나씩 꺼내오기
        for(Location location : locationList){

            // 지역 이름 추출
            String keyword = location.getResult();

            // 검색 키워드에 지역이름 추가
            String url = hotelUrl + keyword;

            // 필요한 정보 파싱
            try {
                Document doc = Jsoup.connect(url).get();
                Elements stockTableBody = doc.select("div.list_wrap li");

                // 8개 저장
                for (int i =0; i < 8; i++) {
                    Hotel hotel = new Hotel();

                    // 호텔 이름
                    String text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                    // 호텔 이미지
                    String img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                    // 호텔 URL
                    String URL = stockTableBody.get(i).select("a").attr("href");

                    // 호텔 결과값 만들기
                    //hotel.setId(id);
                    hotel.setTitle(text);
                    hotel.setImg(img);
                    hotel.setURL(URL);
                    hotel.setLocation(keyword);

                    // 호텔 정보 저장
                    hotelRepo.save(hotel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 지도의 지역 정보 넘겨서 DB에 저장된 블로그 정보 불러오기
    public JSONArray getMapBlog(LocationRequestDto requestDto) {

        // 지역 정보 가져오기
        String location = requestDto.getLocation();

        // 최종 결과값에 넣기 위한 JSON 파싱
        JSONArray blogJson = new JSONArray();

        // 지역 정보에 맞는 블로그 DB 에서 가져오기
        blogJson.add(blogRepository.findAllByLocation(location));

        return blogJson;
    }

    // 지도의 지역 정보 넘겨서 DB에 저장된 숙소 정보 불러오기
    public JSONArray getMapHotel(LocationRequestDto requestDto){

        // 지역 정보 가져오기
        String location = requestDto.getLocation();

        // 최종 결과값에 넣기 위한 JSON 파싱
        JSONArray hotelJson = new JSONArray();

        // 지역 정보에 맞는 숙소 DB 에서 가져오기
        List<Hotel> hotelList = hotelRepository.findAllByLocation(location);

        // JSONArray 에 블로그 크롤링 결과 담기
        hotelJson.add(hotelList);

        return hotelJson;
    }

    // 호텔 리스트 정보를 위해 gameId 에서 여행지 결과 도출
    public String hotel(Long gameId) {

        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 게임 결과를 검색어에 포함하여 결과 도출
        return gameResult.getGameResult();
    }

    // 게임 결과를 통해 블로그 정보 크롤링
    public ResponseEntity<PrivateResponseBody> getGameBlog(String query) throws ParseException{
        List<Blog> gameBlog = setBlogCraw(query);
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
