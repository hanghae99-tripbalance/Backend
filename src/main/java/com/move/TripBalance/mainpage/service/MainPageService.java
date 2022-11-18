package com.move.TripBalance.mainpage.service;

import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.mainpage.controller.response.LocalResponseDto;
import com.move.TripBalance.post.Local;
import com.move.TripBalance.post.Media;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class MainPageService {

    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final ApiService apiService;
    private final HeartRepository heartRepository;
    private final MediaRepository mediaRepository;



    // 지역 별 글 목록
    @Transactional
    public ResponseEntity<PrivateResponseBody> getLocalPost(Long local){

        // enum으로 나눈 지역 코드 불러오기
        Local localEnum = Local.partsValue(Math.toIntExact(local));
        // 최신순으로 지역별 포스트 불러오기
        List<Post> localPostList = postRepository.findAllByLocalOrderByCreatedAtDesc(localEnum);
        List<LocalResponseDto> localList = new ArrayList<>();
        for(Post post : localPostList){
            // 미디어 파일 추출 및 할당
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            String img = oneimage.get(0).getImgURL();

            localList.add(LocalResponseDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .img(img)
                    .localdetail(post.getLocalDetail().toString())
                    .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                localList), HttpStatus.OK);
    }
}
