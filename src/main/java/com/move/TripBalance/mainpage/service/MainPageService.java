package com.move.TripBalance.mainpage.service;

import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.mainpage.controller.response.LocalResponseDto;
import com.move.TripBalance.mainpage.controller.response.TopFiveResponseDto;
import com.move.TripBalance.post.Local;
import com.move.TripBalance.post.Media;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.controller.response.ResponseDto;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 좋아요 순으로 포스트 5개
    @Transactional
    public ResponseDto<?> getTop5Posts() {
        List<TopFiveResponseDto> topFiveList = new ArrayList<>();
        List<Heart> hearts = heartRepository.findAll();
        List<Post> fivePostList = postRepository.findTop5ByHeartsIn(hearts);

        for (Post post : fivePostList) {
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            String img = oneimage.get(0).getImgURL();

            topFiveList.add(TopFiveResponseDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .img(img)
                    .heartNum((long) post.getHearts().size())
                    .build());
        }
        return ResponseDto.success(topFiveList);
    }

    // 지역 별 글 목록
    @Transactional
    public ResponseDto<?> getLocalPost(Long local){
        Local localEnum = Local.partsValue(Math.toIntExact(local));
        List<Post> localPostList = postRepository.findAllByLocalOrderByCreatedAtDesc(localEnum);
        List<LocalResponseDto> localList = new ArrayList<>();
        for(Post post : localPostList){
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
        return ResponseDto.success(localList);
    }
}
