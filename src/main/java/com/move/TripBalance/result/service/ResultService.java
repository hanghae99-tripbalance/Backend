package com.move.TripBalance.result.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
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
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class ResultService {

    private final GameChoiceRepository gameChoiceRepository;

    // 다음 크롤링 하기 위한 API 키
    @Value("${kakao.key}")
    private String key;

    // 다음 블로그 크롤링 주소
    private String blogUrl = "https://dapi.kakao.com/v2/search/blog";

    // 숙소 크롤링 주소
    private String hotelUrl = "https://www.goodchoice.kr/product/result?keyword=";

    // 다음 블로그 크롤링
    public ResponseEntity daumCraw(Long gameId)  {

        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 게임 결과를 검색어에 포함하는 객체 부여
        String keyword = gameResult.getGameResult();

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

    // 크롤링한 결과값 담아서 보내기
    public ResponseEntity<PrivateResponseBody> getAllPost(Long gameId) throws ParseException {

        // 결과값 JSON 파싱
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(daumCraw(gameId).getBody().toString());

        // documents만 도출
        JSONArray docuArray = (JSONArray) jsonObject.get("documents");

        // 리스트에 담아서 반환
        List<Blog> blogList = new ArrayList<>();

        for(int i = 0; i< docuArray.size(); i++){
            JSONObject docuObject = (JSONObject) docuArray.get(i);

            // 블로그 객체 생성
            Blog blog = new Blog();

            // 블로그 이름
            blog.setBlogName(docuObject.get("blogname").toString());

            // 연결되는 URL
            blog.setUrl(docuObject.get("url").toString());

            // 게시글 제목 태그 제거 후 넣기
            blog.setTitle(docuObject.get("title").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;]",""));

            // 게시글 내용 태그 제거 후 넣기
            blog.setContents(docuObject.get("contents").toString().replaceAll("[<b></b>]", "").replaceAll("[&#39;|&#map;]",""));

            // 게시글 첫 이미지
            blog.setThumbnail(docuObject.get("thumbnail").toString());

            // 반환할 리스트에 블로그 정보 추가
            blogList.add(blog);
        }
        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, blogList), HttpStatus.OK);
    }

    // 호텔 리스트
    public ResponseEntity<PrivateResponseBody> hotel(Long gameId) {

        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 게임 결과를 검색어에 포함하는 객체 부여
        String keyword = gameResult.getGameResult();

        // 호텔 검색 결과를 담을 리스트 생성
        List<Hotel> hotels = new ArrayList<>();

        // 검색 키워드에 지역이름 + 호텔 추가
        String url = hotelUrl + keyword + " 호텔";

        // 필요한 정보 파싱
        try {
            Document doc = Jsoup.connect(url).get();
            Elements stockTableBody = doc.select("div.list_wrap li");

            for (int i =0; i < 4; i++) {
                Hotel hotel = new Hotel();

                // 호텔 이름
                String text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                // 호텔 이미지
                String img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                // 호텔 URL
                String URL = stockTableBody.get(i).select("a").attr("href");

                // 호텔 결과값 만들기
                hotel.setTitle(text);
                hotel.setImg(img);
                hotel.setURL(URL);
                hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, hotels), HttpStatus.OK);
    }

    //게임 아이디를 통해 여행지 확인
    @Transactional(readOnly = true)
    public GameResult isPresentGame(Long id) {
        Optional<GameResult> optionalGameResult = gameChoiceRepository.findById(id);
        return optionalGameResult.orElse(null);
    }
}
