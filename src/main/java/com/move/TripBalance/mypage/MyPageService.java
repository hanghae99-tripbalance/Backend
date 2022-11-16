package com.move.TripBalance.mypage;

import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.post.controller.response.PostResponseDto;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.exception.PrivateException;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request){
        // 로그인 한 멤버 정보 추출
        Member member = validateMember(request);
        // 내가 작성한 포스트 repo에서 추출
        List<Post> myPosts = postRepository.findAllByMember(member);
        // 내가 작성한 포스트가 없을 때 메시지 반환
        if(myPosts.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    notPosts), HttpStatus.OK);
        }

        List<PostResponseDto> myPostList = new ArrayList<>();

        // 내가 작성한 포스트 목록 반환
        for(Post post : myPosts){
            Long heartNum = heartRepository.countByPost(post);
            myPostList.add(PostResponseDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .nickName(post.getNickName())
                    .local(post.getLocal().toString())
                    .localdetail(post.getLocalDetail().toString())
                    .pet(post.getPet())
                    .content(post.getContent())
                    .heartNum(heartNum)
                    .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                myPostList), HttpStatus.OK);
    }

    // 내가 좋아요 한 게시물 목록
    @Transactional
    public ResponseEntity<PrivateResponseBody> getMyHeartPosts(HttpServletRequest request) {
        // 로그인 한 멤버 정보 추출
        Member member = validateMember(request);
        // 내가 좋아요 한 게시물 repo에서 추출
        List<Heart> heartList = heartRepository.findAllByMember(member);
        // 내가 좋아요 한 글이 없을 때 메시지 반환
        if (heartList.isEmpty()) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noHearts), HttpStatus.OK);
        }
        List<PostResponseDto> postHeartList = new ArrayList<>();

        // 내가 좋아요 한 게시물 목록 반환
        for (Heart heart : heartList) {
            Post post = heart.getPost();
            Long heartNum = heartRepository.countByPost(post);

            postHeartList.add(
                    PostResponseDto.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .nickName(post.getNickName())
                            .local(post.getLocal().toString())
                            .localdetail(post.getLocalDetail().toString())
                            .pet(post.getPet())
                            .content(post.getContent())
                            .heartNum(heartNum)
                            .build());

        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postHeartList), HttpStatus.OK
        );
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
