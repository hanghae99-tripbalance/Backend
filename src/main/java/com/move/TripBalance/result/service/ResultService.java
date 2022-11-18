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

    // Blog List
    @Value("${kakao.key}")
    private String key;

    private String url = "https://dapi.kakao.com/v2/search/blog";

    public ResponseEntity daumCraw(String query){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + key); //Authorization 설정
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders); //엔티티로 만들기
        URI targetUrl = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("query", query)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();
        ResponseEntity<Blog> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Blog.class);
        ResponseEntity response= restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);
        return response; //내용 반환
    }

    public List<Blog> getAllPost(String query)throws ParseException {
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(daumCraw(query).getBody().toString());
        JSONParser docuParser = new JSONParser();

        // documents만 도출
        JSONArray docuArray = (JSONArray) jsonObject.get("documents");

        List<Blog> blogList = new ArrayList<>();
        for(int i = 0; i< docuArray.size(); i++){
            JSONObject docuObject = (JSONObject) docuArray.get(i);
            Blog blog = new Blog();
            blog.setBlogName(docuObject.get("blogname").toString());
            blog.setUrl(docuObject.get("url").toString());
            blog.setTitle(docuObject.get("title").toString());
            blog.setContents(docuObject.get("contents").toString());
            blog.setThumbnail(docuObject.get("thumbnail").toString());

            blogList.add(blog);
        }
        return blogList;
    }

    // Hotel List
    public ResponseEntity<PrivateResponseBody> hotel(Long gameId) {
        // 게임 아이디 확인
        GameResult gameResult = isPresentGame(gameId);

        // 객체 부여
        String keyword = gameResult.getGameResult();

        List<Hotel> hotels = new ArrayList<>();
        String url = "https://www.goodchoice.kr/product/result?keyword=" + keyword;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements stockTableBody = doc.select("div.list_wrap li");

            for (int i =0; i < 4; i++) {
                Hotel hotel = new Hotel();

                String text;
                text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                String img;
                img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                String URL;
                URL = stockTableBody.get(i).select("a").attr("href");

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

    //게임 아이디
    @Transactional(readOnly = true)
    public GameResult isPresentGame(Long id) {
        Optional<GameResult> optionalGameResult = gameChoiceRepository.findById(id);
        return optionalGameResult.orElse(null);
    }
}
