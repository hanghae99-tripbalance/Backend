package com.move.TripBalance.mainpage.service;

import com.move.TripBalance.mainpage.controller.response.LocalResponseDto;
import com.move.TripBalance.mainpage.controller.response.TopFiveResponseDto;
import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.post.Local;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.controller.response.ResponseDto;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Transactional
    public ResponseDto<?> getTop5Posts(UserDetailsImpl userDetails) {
        List<TopFiveResponseDto> topFiveList = new ArrayList<>();
        List<Heart> hearts = heartRepository.findAll();
        List<Post> fivePostList = postRepository.findTop5ByHeartsIn(hearts);


        for (Post post : fivePostList) {
            TopFiveResponseDto topFiveResponseDto = new TopFiveResponseDto();

            boolean heartYn = false;
            if(userDetails != null) {
                Member member = userDetails.getMember();
                Optional<Heart> heart = heartRepository.findByMemberAndPost(member, post);
                if(heart.isPresent()) {
                    heartYn = true;
                }
            }
            topFiveResponseDto.setTitle(post.getTitle());
            topFiveResponseDto.setImg(post.getImgURL().get(0).toString());
            topFiveResponseDto.setHeartNum((long) post.getHearts().size());
            topFiveResponseDto.setHeartYn(heartYn);

            topFiveList.add(topFiveResponseDto);
        }
        return ResponseDto.success(topFiveList);
    }

    @Transactional
    public ResponseDto<?> getLocalPost(Long local){
        Local localEnum = Local.partsValue(Math.toIntExact(local));
        List<Post> localPostList = postRepository.findAllByLocalOrderByCreatedAtDesc(localEnum);
        List<LocalResponseDto> localList = new ArrayList<>();
        for(Post post : localPostList){
            LocalResponseDto localResponseDto = new LocalResponseDto();
            localResponseDto.setTitle(post.getTitle());
            localResponseDto.setContent(post.getContent());
            localResponseDto.setLocaldetail(post.getLocalDetail().toString());
            localResponseDto.setImg(post.getImgURL().get(0).toString());
            localList.add(localResponseDto);
        }
        return ResponseDto.success(localList);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
