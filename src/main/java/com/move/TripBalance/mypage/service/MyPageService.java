package com.move.TripBalance.mypage.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
import com.move.TripBalance.balance.repository.QuestionRepository;
import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.member.SNS;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.member.repository.SNSRepository;
import com.move.TripBalance.mypage.controller.request.MyPageRequestDto;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
@PropertySource(value = "classpath:/messages.properties", encoding = "")
public class MyPageService {

    private final PostRepository postRepository;
    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final SNSRepository snsRepository;
    private final MediaRepository mediaRepository;
    private final GameChoiceRepository gameChoiceRepository;

    private final QuestionRepository questionRepository;

    // 내가 작성한 포스트가 없을 때 메시지
    @Value(value = "${mypage.posts.notfound}")
    String notPosts;

    // 내가 좋아요 한 포스트가 없을 때 메시지
    @Value(value = "${mypage.heart.notfound}")
    String noHearts;

    //내가 실행한 게임이 없을 때 메시지
    @Value(value = "${mypage.games.notfound}")
    String noGames;

    // 회원이 작성한 포스트가 없을 때 메시지
    @Value(value = "${member.posts.notfound}")
    String noMemberPosts;
    // 회원이 좋아요 한 포스트가 없을 때 메시지
    @Value(value = "${member.hearts.notfound}")
    String noMemberHearts;
    // 회원이 실행한 게임이 없을 때 메시지
    @Value(value = "${member.games.notfound}")
    String noMemberGames;

    // 나의 프로필 정보 불러오기
    public ResponseEntity<PrivateResponseBody> myInfo(HttpServletRequest request){

        // 회원 정보 불러오기
        Member member = validateMember(request);

        SNS sns = snsRepository.findByMember(member);

        // sns 정보 불러오기
        List<String> snsList = new ArrayList<>();
        snsList.add(sns.getInsta());
        snsList.add(sns.getFacebook());
        snsList.add(sns.getYoutube());
        snsList.add(sns.getBlog());

        // 멤버 정보 빌드
        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .profileImg(member.getProfileImg())
                .self(member.getSelf())
                .sns(snsList)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                responseDto ), HttpStatus.OK);
    }


    // 나의 밸런스게임 선택지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> myBalance(HttpServletRequest request){

        // 나의 밸런스 게임 결과 가져오기
        Member member = validateMember(request);
        List<GameResult> gameResults= gameChoiceRepository.findAllByMember(member);

        // 선택지 횟수를 세기 위한 List
        List<String> resultList = new ArrayList<>();
        List<Long> ansList = new ArrayList<>();

        // 중복값을 제거하기 위해 Set 사용
        Set<String> leftSet = new HashSet<>();
        Set<String> rightSet = new HashSet<>();

        // 선택지와 선택 횟수 매칭한 결과값
        Map<String, Long> countAns = new HashMap<>();

        // 나의 게임 결과값 가져오기
        for(GameResult results : gameResults) {
            ansList.add(results.getAnswer1());
            ansList.add(results.getAnswer2());
            ansList.add(results.getAnswer3());
            ansList.add(results.getAnswer4());
            ansList.add(results.getAnswer5());

            // 짝수번째 번호면 leftId 를 통해서 선택지 가져오기
            List<Long> leftAns = ansList.stream().filter(a -> a % 2 == 0).collect(Collectors.toList());

            for (Long ans : leftAns) {
                String leftResult = questionRepository.findByLeftId(ans).getLeftAnswer();
                resultList.add(leftResult);

                // 중복값 제거
                leftSet.add(leftResult);

                // 리스트 안의 문자열 포함 횟수를 세어주는 frequecy 메소드 사용
                Long count = Long.valueOf(Collections.frequency(resultList, leftResult));
                countAns.put(leftResult, count);
            }

            // 홀수번째 번호면 rightId 를 통해서 선택지 가져오기
            List<Long> rightAns = ansList.stream().filter(a -> a % 2 == 1).collect(Collectors.toList());

            for (Long ans : rightAns) {
                String rightResult = questionRepository.findByRightId(ans).getRightAnswer();
                resultList.add(rightResult);

                // 중복값 제거
                rightSet.add(rightResult);

                // 리스트 안의 문자열 포함 횟수를 세어주는 frequecy 메소드 사용
                Long count = Long.valueOf(Collections.frequency(resultList, rightResult));
                countAns.put(rightResult, count);
            }

        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                countAns), HttpStatus.OK);
    }

    // 나의 밸런스 게임 여행지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> myTrip(HttpServletRequest request) {

        // 나의 밸런스 게임 결과 가져오기
        Member member = validateMember(request);
        List<GameResult> gameResults = gameChoiceRepository.findAllByMember(member);

        // 선택지 횟수를 세기 위한 List
        List<String> tripList = new ArrayList<>();

        // 중복값을 제거하기 위해 Set 사용
        Set<String> tripSet = new HashSet<>();

        // 여행지와 선택 횟수 매칭한 결과값
        Map<String, Long> countTrip = new HashMap<>();

        // 나의 게임 결과값 가져오기
        for (GameResult results : gameResults) {

            // 선택한 여행지 결과값 리스트에 넣어주기
            String trip = results.getGameResult();
            tripList.add(trip);
            tripSet.add(trip);
            Long count = Long.valueOf(Collections.frequency(tripList, trip));
            countTrip.put(trip, count);

        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                countTrip), HttpStatus.OK);
    }

    // 내 정보 수정하기
    public ResponseEntity<PrivateResponseBody> setMyInfo(MyPageRequestDto requestDto, HttpServletRequest request){

        // 로그인 한 회원 정보 추출
        Member member = validateMember(request);

        // SNS 정보 찾기
        SNS sns = snsRepository.findByMember(member);

        // 회원정보 찾기
        Optional<Member> mem = memberRepository.findById(member.getMemberId());

        // 회원정보 업데이트
        mem.get().updateInfo(requestDto);

        //각각의 sns 계정 값이 비어있지 않을 때 도메인과 함께 저장
        // 인스타그램
        if(requestDto.getInsta()!= null){
            MyPageRequestDto snsRequestDto = MyPageRequestDto.builder()
                    .insta(requestDto.getInsta())
                    .build();
            sns.updateinsta(snsRequestDto);}

        // 페이스북
        if (requestDto.getFacebook()!= null) {
            MyPageRequestDto snsRequestDto = MyPageRequestDto.builder()
                    .facebook(requestDto.getFacebook())
                    .build();
            sns.updatefacebook(snsRequestDto);}

        // 유투브
        if (requestDto.getYoutube()!= null) {
            MyPageRequestDto snsRequestDto = MyPageRequestDto.builder()
                    .youtube(requestDto.getYoutube())
                    .build();
            sns.updateyoutube(snsRequestDto);}

        // 네이버 블로그
        if (requestDto.getBlog()!= null) {
            MyPageRequestDto snsRequestDto = MyPageRequestDto.builder()
                    .blog(requestDto.getBlog())
                    .build();
            sns.updateblog(snsRequestDto);}

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                "개인정보 수정 완료"), HttpStatus.OK);
    }

    // 내가 작성한 글 목록
    public ResponseEntity<PrivateResponseBody> getMyPosts(HttpServletRequest request, int page){

        // 로그인 한 멤버 정보 추출
        Member member = validateMember(request);

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 10개씩 보여주기, 작성 시간을 기준으로 내림차순 정렬
        Pageable pageable =  PageRequest.of(page, 10, Sort.by("createdAt").descending());

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

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 10개씩 보여주기, 좋아요 누른 시간을 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by("modifiedAt").descending());

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
                            .nickName(post.getAuthor())
                            .profileImg(member.getProfileImg())
                            .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postHeartList), HttpStatus.OK
        );
    }

    // 회원 프로필 정보 불러오기
    public ResponseEntity<PrivateResponseBody> getMemberInfo(Long id){

        // 회원 정보 불러오기
        Optional<Member> member = memberRepository.findById(id);
        SNS sns = snsRepository.findByMember(member.get());
        // sns 정보 불러오기
        List<String> snsList = new ArrayList<>();
        snsList.add(sns.getInsta());
        snsList.add(sns.getFacebook());
        snsList.add(sns.getYoutube());
        snsList.add(sns.getBlog());

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


    // 회원의 좋아요 한 게시글 목록 불러오기
    @Transactional
    public ResponseEntity<PrivateResponseBody> getMemberHeartPosts(Long memberId, int page) {

        // 멤버 정보 추출
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 10개씩 보여주기, 좋아요 누른 시간을 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by("modifiedAt").descending());

        // 좋아요 한 게시물 repo에서 추출
        Page<Heart> heartList = heartRepository.findAllByMember(memberInfo, pageable);

        // 좋아요 한 글이 없을 때 메시지 반환
        if (heartList.isEmpty()) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberHearts), HttpStatus.OK);
        }
        List<MyHeartResponseDto> postHeartList = new ArrayList<>();

        // 좋아요 한 게시물 목록 반환
        for (Heart heart : heartList) {
            Post post = heart.getPost();

            // 이미지 파일 넣어주기
            Media img = mediaRepository.findFirstByPost(post).get(0);
            postHeartList.add(
                    MyHeartResponseDto.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .img(img.getImgURL())
                            .nickName(post.getAuthor())
                            .profileImg(memberInfo.getProfileImg())
                            .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postHeartList), HttpStatus.OK
        );
    }

    // 회원이 작성한 글 목록
    public ResponseEntity<PrivateResponseBody> getMemberPosts(Long memberId, int page){

        // 멤버 정보 추출
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 10개씩 보여주기, 작성 시간을 기준으로 내림차순 정렬
        Pageable pageable =  PageRequest.of(page, 10, Sort.by("createdAt").descending());

        // 내가 작성한 포스트 repo에서 추출
        Page<Post> memberPosts = postRepository.findAllByMember(memberInfo, pageable);

        // 내가 작성한 포스트가 없을 때 메시지 반환
        if(memberPosts.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberPosts), HttpStatus.OK);
        }

        List<MyPostResponseDto> myPostList = new ArrayList<>();

        // 내가 작성한 포스트 목록 반환
        for(Post post : memberPosts){

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

    // 회원의 밸런스게임 선택지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> getMemberBalance(Long memberId){

        // 회원정보 가져오기
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 회원의 게임 결과 가져오기
        List<GameResult> gameResults= gameChoiceRepository.findAllByMember(memberInfo);

        // 게임 결과값이 없을 때 실행한 게임이 없다는 메시지 반환
        if(gameResults.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberGames), HttpStatus.OK);
        }

        // 선택지 횟수를 세기 위한 List
        List<String> resultList = new ArrayList<>();
        List<Long> ansList = new ArrayList<>();

        // 중복값을 제거하기 위해 Set 사용
        Set<String> leftSet = new HashSet<>();
        Set<String> rightSet = new HashSet<>();

        // 선택지와 선택 횟수 매칭한 결과값
        Map<String, Long> countAns = new HashMap<>();

        // 회원의 게임 결과값 가져오기
        for(GameResult results : gameResults) {
            ansList.add(results.getAnswer1());
            ansList.add(results.getAnswer2());
            ansList.add(results.getAnswer3());
            ansList.add(results.getAnswer4());
            ansList.add(results.getAnswer5());

            // 짝수번째 번호면 leftId 를 통해서 선택지 가져오기
            List<Long> leftAns = ansList.stream().filter(a -> a % 2 == 0).collect(Collectors.toList());

            for (Long ans : leftAns) {
                String leftResult = questionRepository.findByLeftId(ans).getLeftAnswer();
                resultList.add(leftResult);

                // 중복값 제거
                leftSet.add(leftResult);

                // 리스트 안의 문자열 포함 횟수를 세어주는 frequecy 메소드 사용
                Long count = Long.valueOf(Collections.frequency(resultList, leftResult));
                countAns.put(leftResult, count);
            }

            // 홀수번째 번호면 rightId 를 통해서 선택지 가져오기
            List<Long> rightAns = ansList.stream().filter(a -> a % 2 == 1).collect(Collectors.toList());

            for (Long ans : rightAns) {
                String rightResult = questionRepository.findByRightId(ans).getRightAnswer();
                resultList.add(rightResult);

                // 중복값 제거
                rightSet.add(rightResult);

                // 리스트 안의 문자열 포함 횟수를 세어주는 frequecy 메소드 사용
                Long count = Long.valueOf(Collections.frequency(resultList, rightResult));
                countAns.put(rightResult, count);
            }

        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                countAns), HttpStatus.OK);
    }

    // 회원의 밸런스 게임 여행지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> getMemberTrip(Long memberId) {

        // 회원정보 가져오기
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 밸런스 게임 결과 가져오기
        List<GameResult> gameResults = gameChoiceRepository.findAllByMember(memberInfo);

        // 게임 결과값이 없을 때 실행한 게임이 없다는 메시지 반환
        if(gameResults.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberGames), HttpStatus.OK);
        }

        // 선택지 횟수를 세기 위한 List
        List<String> tripList = new ArrayList<>();

        // 중복값을 제거하기 위해 Set 사용
        Set<String> tripSet = new HashSet<>();

        // 여행지와 선택 횟수 매칭한 결과값
        Map<String, Long> countTrip = new HashMap<>();

        // 게임 결과값 가져오기
        for (GameResult results : gameResults) {

            // 선택한 여행지 결과값 리스트에 넣어주기
            String trip = results.getGameResult();
            tripList.add(trip);
            tripSet.add(trip);
            Long count = Long.valueOf(Collections.frequency(tripList, trip));
            countTrip.put(trip, count);

        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                countTrip), HttpStatus.OK);
    }


    // 로그인 한 회원 정보 확인
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
