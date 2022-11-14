package com.move.TripBalance.service;

import com.move.TripBalance.controller.response.PostResponseDto;
import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import com.move.TripBalance.exception.PrivateException;
import com.move.TripBalance.exception.StatusCode;
import com.move.TripBalance.jwt.TokenProvider;
import com.move.TripBalance.repository.HeartRepository;
import com.move.TripBalance.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
@PropertySource("classpath:/message.properties")
public class MyPageService {

    private final PostRepository postRepository;

    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;

    // 내가 작성한 포스트가 없을 때 메시지
    @Value(value = "${mypage.posts.notfound}")
    String notPosts;

    // 내가 좋아요 한 포스트가 없을 때 메시지
    @Value(value = "${mypage.heart.notfound}")
    String noHearts;

    // 내가 작성한 글 목록
    public ResponseDto<?> getMyPosts(HttpServletRequest request){
        Member member = validateMember(request);
        List<Post> myPosts = postRepository.findAllByMember(member);
        if(myPosts.isEmpty()){
            return ResponseDto.success(notPosts);
        }

        List< PostResponseDto> myPostList = new ArrayList<>();

        for(Post post : myPosts){
            Long heartNum = heartRepository.countByPost(post);
            myPostList.add(PostResponseDto.builder()
                            .id(post.getPostId())
                    .title(post.getTitle())
                    .nickName(post.getNickName())
                    .local(post.getLocal().toString())
                    .pet(post.getPet())
                    .content(post.getContent())
                    .heartNum(heartNum)
                    .build());
        }
        return ResponseDto.success(myPostList);
    }

    // 내가 좋아요 한 게시물 목록
    @Transactional
    public ResponseDto<?> getMyHeartPosts(HttpServletRequest request){
        Member member = validateMember(request);

       List<Heart> heartList = heartRepository.findAllByMember(member);

       if(heartList.isEmpty()){
           return ResponseDto.success(noHearts);
       }

       List<PostResponseDto> postHeartList = new ArrayList<>();

       for(Heart heart : heartList){
           Post post = heart.getPost();
           Long heartNum = heartRepository.countByPost(post);

           postHeartList.add(
                   PostResponseDto.builder()
                           .id(post.getPostId())
                           .title(post.getTitle())
                           .nickName(post.getNickName())
                           .local(post.getLocal().toString())
                           .pet(post.getPet())
                           .content(post.getContent())
                           .heartNum(heartNum)
                           .build());

       }
     return ResponseDto.success(postHeartList);
    }

    public Member validateMember(HttpServletRequest request) {

        // Access 토큰 유효성 확인
        if (request.getHeader("Authorization") == null) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Refresh 토큰 유요성 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Access, Refresh 토큰 유효성 검증이 완료되었을 경우 인증된 유저 정보 저장
        Member member = tokenProvider.getMemberFromAuthentication();

        // 인증된 유저 정보 반환
        return member;
    }
}
