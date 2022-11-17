package com.move.TripBalance.mypage.service;

import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.member.SNS;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.member.repository.SNSRepository;
import com.move.TripBalance.mypage.controller.request.MyImgRequestDto;
import com.move.TripBalance.mypage.controller.request.SNSRequestDto;
import com.move.TripBalance.mypage.controller.response.MyHeartResponseDto;
import com.move.TripBalance.mypage.controller.response.MyPageResponseDto;
import com.move.TripBalance.mypage.controller.response.MyPostResponseDto;
import com.move.TripBalance.post.Media;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.exception.PrivateException;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

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
    private final MemberRepository memberRepository;
    private final SNSRepository snsRepository;
    private final MediaRepository mediaRepository;


    // 내가 작성한 포스트가 없을 때 메시지
    @Value(value = "${mypage.posts.notfound}")
    String notPosts;

    // 내가 좋아요 한 포스트가 없을 때 메시지
    @Value(value = "${mypage.heart.notfound}")
    String noHearts;

    // 프로필 사진 수정
    public ResponseEntity<PrivateResponseBody> myImg(JSONObject img, HttpServletRequest request){
        Member member = validateMember(request);
        Optional<Member> mem = memberRepository.findById(member.getMemberId());
        mem.get().updateProfileImg(img.get("img").toString());

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                "프로필사진 수정 완료"), HttpStatus.OK);
    }

    // 자기소개 수정
    public ResponseEntity<PrivateResponseBody> mySelf(JSONObject self, HttpServletRequest request){
        Member member = validateMember(request);
        Optional<Member> mem = memberRepository.findById(member.getMemberId());
        mem.get().updateSelf(self.get("self").toString());
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                "자기소개 수정 완료"), HttpStatus.OK);
    }

    // SNS 링크 저장
    public ResponseEntity<PrivateResponseBody> mySns(SNSRequestDto requestDto, HttpServletRequest request){

        Member member = validateMember(request);
        SNS sns = snsRepository.findByMember(member);

         //각각의 sns 계정 값이 비어있지 않을 때 도메인과 함께 저장

        // 인스타그램
        if(requestDto.getInsta()!= null){
            SNSRequestDto snsRequestDto = SNSRequestDto.builder()
                    .insta(requestDto.getInsta())
                    .build();
        sns.updateinsts(snsRequestDto);}

        // 페이스북
        if (requestDto.getFacebook()!= null) {
            SNSRequestDto snsRequestDto = SNSRequestDto.builder()
                    .facebook(requestDto.getFacebook())
                    .build();
            sns.updatefacebook(snsRequestDto);}
        // 유투브
        if (requestDto.getYoutube()!= null) {
            SNSRequestDto snsRequestDto = SNSRequestDto.builder()
                    .youtube(requestDto.getYoutube())
                    .build();
            sns.updateyoutube(snsRequestDto);}

        // 네이버 블로그
        if (requestDto.getBlog()!= null) {
            SNSRequestDto snsRequestDto = SNSRequestDto.builder()
                    .blog(requestDto.getBlog())
                    .build();
            sns.updateblog(snsRequestDto);}

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                "SNS 주소 저장 완료"), HttpStatus.OK);
    }

    // 회원 프로필 정보 불러오기
    public ResponseEntity<PrivateResponseBody> getMemberInfo(Long id){
        Optional<Member> member = memberRepository.findById(id);

        // sns 정보 불러오기
        List<String> snsList = new ArrayList<>();
        snsList.add(member.get().getSns().getInsta());
        snsList.add(member.get().getSns().getFacebook());
        snsList.add(member.get().getSns().getYoutube());
        snsList.add(member.get().getSns().getBlog());

        // 멤버 정보 빌드
        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .memberId(member.get().getMemberId())
                .email(member.get().getEmail())
                .nickName(member.get().getNickName())
                .profileImg(member.get().getProfileImg())
                .self(member.get().getSelf())
                .sns(snsList)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                responseDto ), HttpStatus.OK);
    }

    // 내가 작성한 글 목록
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request, int page){

        // 로그인 한 멤버 정보 추출
        Member member = validateMember(request);

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 5개씩 보여주기, 작성 시간을 기준으로 내림차순 정렬
        Pageable pageable =  PageRequest.of(page, 5, Sort.by("createdAt").descending());

        // 내가 작성한 포스트 repo에서 추출
        Page<Post> myPosts = postRepository.findAllByMember(member, pageable);

        // 내가 작성한 포스트가 없을 때 메시지 반환
        if(myPosts.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    notPosts), HttpStatus.OK);
        }

        List<MyPostResponseDto> myPostList = new ArrayList<>();

        // 내가 작성한 포스트 목록 반환
        for(Post post : myPosts){

            // 이미지 파일 넣어주기
            Media img = mediaRepository.findFirstByPost(post).get(0);
            myPostList.add(MyPostResponseDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .img(img.getImgURL())
                    .createdAt(post.getCreatedAt())
                    .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                myPostList), HttpStatus.OK);
    }

    // 내가 좋아요 한 게시물 목록
    @Transactional
    public ResponseEntity<PrivateResponseBody> getMyHeartPosts(HttpServletRequest request, int page) {

        // 로그인 한 멤버 정보 추출
        Member member = validateMember(request);

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 5개씩 보여주기, 좋아요 누른 시간을 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 5, Sort.by("modifiedAt").descending());

        // 내가 좋아요 한 게시물 repo에서 추출
        Page<Heart> heartList = heartRepository.findAllByMember(member, pageable);

        // 내가 좋아요 한 글이 없을 때 메시지 반환
        if (heartList.isEmpty()) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noHearts), HttpStatus.OK);
        }
        List<MyHeartResponseDto> postHeartList = new ArrayList<>();

        // 내가 좋아요 한 게시물 목록 반환
        for (Heart heart : heartList) {
            Post post = heart.getPost();

            // 이미지 파일 넣어주기
            Media img = mediaRepository.findFirstByPost(post).get(0);
            postHeartList.add(
                    MyHeartResponseDto.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .img(img.getImgURL())
                            .nickName(post.getNickName())
                            .profileImg(member.getProfileImg())
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
