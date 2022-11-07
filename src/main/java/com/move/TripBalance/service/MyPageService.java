package com.move.TripBalance.service;

import com.move.TripBalance.controller.response.PostResponseDto;
import com.move.TripBalance.controller.response.ResponseDto;
import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import com.move.TripBalance.jwt.TokenProvider;
import com.move.TripBalance.repository.HeartRepository;
import com.move.TripBalance.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class MyPageService {

    private final PostRepository postRepository;

    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;

    @Value(value = "${mypage.posts.notfound}")
    String notPosts;

    @Value(value = "${mypage.heart.notfound}")
    String noHearts;

    public ResponseDto<?> getMyPosts(HttpServletRequest request){
        Member member = validateMember(request);
        List<Post> myPosts = postRepository.findAllByMemberId(member.getMemberId());
        if(myPosts.isEmpty()){
            return ResponseDto.success(notPosts);
        }

        List< PostResponseDto> myPostList = new ArrayList<>();

        for(Post post : myPosts){
            myPostList.add(PostResponseDto.builder()
                            .id(post.getPostId())
                            .author(post.getAuthor())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .pet(post.getPet())
                            .heartCount(post.getHearts().size())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build());
        }
        return ResponseDto.success(myPostList);
    }

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

           postHeartList.add(
                   PostResponseDto.builder()
                           .id(post.getPostId())
                           .author(post.getAuthor())
                           .title(post.getTitle())
                           .content(post.getContent())
                           .pet(post.getPet())
                           .heartCount(post.getHearts().size())
                           .myHeart(post.getMyHeart())
                           .build()
           );
       }
     return ResponseDto.success(postHeartList);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
