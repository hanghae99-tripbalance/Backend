package com.move.TripBalance.result.service;

import com.move.TripBalance.result.Blog;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class BlogService {

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
}
