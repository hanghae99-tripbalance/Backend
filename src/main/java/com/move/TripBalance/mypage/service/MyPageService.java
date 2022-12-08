package com.move.TripBalance.mypage.service;

import com.move.TripBalance.balance.GameResult;
import com.move.TripBalance.balance.repository.GameChoiceRepository;
import com.move.TripBalance.balance.repository.QuestionRepository;
import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.repository.CommentRepository;
import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.member.SNS;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.member.repository.SNSRepository;
import com.move.TripBalance.member.service.MemberService;
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
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
@Setter
@PropertySource(value = "classpath:/messages.properties")
public class MyPageService {

    private final PostRepository postRepository;
    private final HeartRepository heartRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final SNSRepository snsRepository;
    private final MediaRepository mediaRepository;
    private final GameChoiceRepository gameChoiceRepository;
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;

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

        // 작성 글 갯수 불러오기
        List<Post> postList = postRepository.findAllByMember(member);
        Long postCnt = Long.valueOf(postList.size());

        // 작성 댓글 갯수 불러오기
        List<Comment> commentList = commentRepository.findAllByMember(member);
        Long commentCnt = Long.valueOf(commentList.size());

        // 실행한 게임 횟수 불러오기
        List<GameResult> gameResults = gameChoiceRepository.findAllByMember(member);
        Long gameCnt = Long.valueOf(gameResults.size());

        // 멤버 정보 빌드
        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .profileImg(member.getProfileImg())
                .self(member.getSelf())
                .sns(snsList)
                .postCnt(postCnt)
                .commentCnt(commentCnt)
                .gameCnt(gameCnt)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                responseDto ), HttpStatus.OK);
    }


    // 나의 밸런스 게임 여행지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> myTrip(HttpServletRequest request) {

        // 나의 밸런스 게임 결과 가져오기
        Member member = validateMember(request);
        List<GameResult> allGame = gameChoiceRepository.findAllByMember(member);

        // 게임 결과값이 있는 것만 리스트에 넣기
        List<GameResult> trueGame = new ArrayList<>();
        for(int i = 0; i < allGame.size(); i++){
            if(allGame.get(i).getGameResult()!=null){
                trueGame.add(allGame.get(i));
            }
        }

        // 게임 결과값 세기
        Map<String, Integer> map = new HashMap<>();
        for (GameResult gameResult : trueGame) {
            Integer count = map.get(gameResult.getGameResult());
            if (count == null) {
                map.put(gameResult.getGameResult(), 1);
            } else {
                map.put(gameResult.getGameResult(), count + 1);
            }
        }

        // 정렬을 위한 리스트
        List<String> descList = new ArrayList<>(map.keySet());

        // 횟수를 기준으로 내림차순 정렬
        Collections.sort(descList, (d1, d2) -> (map.get(d2).compareTo(map.get(d1))));

        // 내림차순 한 결과값을 반환
        List<String> result = new ArrayList<>();
        String strResult;
        for(String key : descList){
            strResult = "지역: " + key + ", 값: " + map.get(key);
            result.add(strResult);
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                result), HttpStatus.OK);
    }

    // 내 정보 수정하기
    public ResponseEntity<PrivateResponseBody> setMyInfo(MyPageRequestDto requestDto, HttpServletRequest request){

        // 로그인 한 회원 정보 추출
        Member member = validateMember(request);

        // SNS 정보 찾기
        SNS sns = snsRepository.findByMember(member);

        // 회원정보 찾기
        Optional<Member> mem = memberRepository.findById(member.getMemberId());

        //nickname 체크
        if(!requestDto.getNickName().equals(member.getNickName())) {
            if (null != memberService.isPresentNickName(requestDto.getNickName())) {
                return new ResponseEntity<>(new PrivateResponseBody
                        (StatusCode.DUPLICATED_NICKNAME, null), HttpStatus.OK);
            }
        }

        // 회원정보 업데이트
        mem.get().updateInfo(requestDto);

        // 내가 작성한 글 목록에서 닉네임 업데이트
        // 내가 작성한 포스트 repo에서 추출
        List<Post> myPosts = postRepository.findAllByMember(member);

        // 내가 작성한 포스트에 업데이트 된 정보 반영
        for(Post post : myPosts){
            post.updateMember(mem.get());
        }

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

        List<String> snsList = new ArrayList<>();
        snsList.add(sns.getInsta());
        snsList.add(sns.getFacebook());
        snsList.add(sns.getYoutube());
        snsList.add(sns.getBlog());

        // 작성 글 갯수 불러오기
        List<Post> postList = postRepository.findAllByMember(member);
        Long postCnt = Long.valueOf(postList.size());

        // 작성 댓글 갯수 불러오기
        List<Comment> commentList = commentRepository.findAllByMember(member);
        Long commentCnt = Long.valueOf(commentList.size());

        // 실행한 게임 횟수 불러오기
        List<GameResult> gameResults = gameChoiceRepository.findAllByMember(member);
        Long gameCnt = Long.valueOf(gameResults.size());

        // 멤버 정보 빌드
        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .memberId(mem.get().getMemberId())
                .email(mem.get().getEmail())
                .nickName(mem.get().getNickName())
                .profileImg(mem.get().getProfileImg())
                .self(mem.get().getSelf())
                .sns(snsList)
                .postCnt(postCnt)
                .commentCnt(commentCnt)
                .gameCnt(gameCnt)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, responseDto), HttpStatus.OK);
    }

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

        // 작성 글 갯수 불러오기
        List<Post> postList = postRepository.findAllByMember(member.get());
        Long postCnt = Long.valueOf(postList.size());

        // 작성 댓글 갯수 불러오기
        List<Comment> commentList = commentRepository.findAllByMember(member.get());
        Long commentCnt = Long.valueOf(commentList.size());

        // 실행한 게임 횟수 불러오기
        List<GameResult> gameResults = gameChoiceRepository.findAllByMember(member.get());
        Long gameCnt = Long.valueOf(gameResults.size());


        // 멤버 정보 빌드
        MyPageResponseDto responseDto = MyPageResponseDto.builder()
                .memberId(member.get().getMemberId())
                .email(member.get().getEmail())
                .nickName(member.get().getNickName())
                .profileImg(member.get().getProfileImg())
                .self(member.get().getSelf())
                .sns(snsList)
                .postCnt(postCnt)
                .commentCnt(commentCnt)
                .gameCnt(gameCnt)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                responseDto ), HttpStatus.OK);
    }


    // 회원의 좋아요 한 게시글 목록 불러오기
    @Transactional
    public ResponseEntity<PrivateResponseBody> getMemberHeartPosts(Long memberId) {

        // 멤버 정보 추출
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 좋아요 한 게시물 repo에서 추출
        List<Heart> heartList = heartRepository.findAllByMember(memberInfo);

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
    public ResponseEntity<PrivateResponseBody> getMemberPosts(Long memberId){

        // 멤버 정보 추출
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 회원이 작성한 포스트 repo에서 추출
        List<Post> memberPosts = postRepository.findAllByMember(memberInfo);

        // 회원이 작성한 포스트가 없을 때 메시지 반환
        if(memberPosts.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberPosts), HttpStatus.OK);
        }

        List<MyPostResponseDto> myPostList = new ArrayList<>();

        // 회원이 작성한 포스트 목록 반환
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

    // 회원의 밸런스 게임 여행지 통계 불러오기
    public ResponseEntity<PrivateResponseBody> getMemberTrip(Long memberId) {

        // 회원정보 가져오기
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 밸런스 게임 결과 가져오기
        List<GameResult> allGame = gameChoiceRepository.findAllByMember(memberInfo);

        // 게임 결과값이 있는 것만 리스트에 넣기
        List<GameResult> trueGame = new ArrayList<>();
        for(int i = 0; i < allGame.size(); i++){
            if(allGame.get(i).getGameResult()!=null){
                trueGame.add(allGame.get(i));
            }
        }

        // 게임 결과값이 없을 때 실행한 게임이 없다는 메시지 반환
        if(trueGame.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                    noMemberGames), HttpStatus.OK);
        }

        // 게임 결과값 세기
        Map<String, Integer> map = new HashMap<>();
        for (GameResult gameResult : trueGame) {
            Integer count = map.get(gameResult.getGameResult());
            if (count == null) {
                map.put(gameResult.getGameResult(), 1);
            } else {
                map.put(gameResult.getGameResult(), count + 1);
            }
        }

        // 정렬을 위한 리스트
        List<String> descList = new ArrayList<>(map.keySet());

        // 횟수를 기준으로 내림차순 정렬
        Collections.sort(descList, (d1, d2) -> (map.get(d2).compareTo(map.get(d1))));

        // 내림차순 한 결과값을 반환
        List<String> result = new ArrayList<>();
        String strResult;
        for(String key : descList){
            strResult = "지역: " + key + ", 값: " + map.get(key);
            result.add(strResult);
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                result), HttpStatus.OK);
    }
    // 전체 밸런스 게임에 대한 통계
    public ResponseEntity<PrivateResponseBody> totalGame() {

        // 전체 밸런스 게임 통계
        List<GameResult> allGame = gameChoiceRepository.findAll();

        // 게임 결과값이 있는 것만 리스트에 넣기
        List<GameResult> trueGame = new ArrayList<>();
        for(int i = 0; i < allGame.size(); i++){
            if(allGame.get(i).getGameResult()!=null){
                trueGame.add(allGame.get(i));
            }
        }

        // 게임 결과값 세기
        Map<String, Integer> map = new HashMap<>();
        for (GameResult gameResult : trueGame) {
            Integer count = map.get(gameResult.getGameResult());
            if (count == null) {
                map.put(gameResult.getGameResult(), 1);
            } else {
                map.put(gameResult.getGameResult(), count + 1);
            }
        }

        // 정렬을 위한 리스트
        List<String> descList = new ArrayList<>(map.keySet());

        // 횟수를 기준으로 내림차순 정렬
        Collections.sort(descList, (d1, d2) -> (map.get(d2).compareTo(map.get(d1))));

        // 내림차순 한 결과값을 반환
        List<String> result = new ArrayList<>();
        String strResult;
        for(String key : descList){
            strResult = "지역: " + key + ", 값: " + map.get(key);
            result.add(strResult);
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                result), HttpStatus.OK);
    }

    // 전체 밸런스 게임 중 상위 10개 통계
    public ResponseEntity<PrivateResponseBody> totalTenGame() {

        // 전체 밸런스 게임 통계
        List<GameResult> allGame = gameChoiceRepository.findAll();

        // 게임 결과값이 있는 것만 리스트에 넣기
        List<GameResult> trueGame = new ArrayList<>();
        for(int i = 0; i < allGame.size(); i++){
            if(allGame.get(i).getGameResult()!=null){
                trueGame.add(allGame.get(i));
            }
        }

        // 게임 결과값 세기
        Map<String, Integer> map = new HashMap<>();
        for (GameResult gameResult : trueGame) {
            Integer count = map.get(gameResult.getGameResult());
            if (count == null) {
                map.put(gameResult.getGameResult(), 1);
            } else {
                map.put(gameResult.getGameResult(), count + 1);
            }
        }

        // 정렬을 위한 리스트
        List<String> descList = new ArrayList<>(map.keySet());

        // 횟수를 기준으로 내림차순 정렬
        Collections.sort(descList, (d1, d2) -> (map.get(d2).compareTo(map.get(d1))));

        // 내림차순 한 결과값을 반환
        List<String> result = new ArrayList<>();
        String strResult;
        for(String key : descList){
            strResult = "지역: " + key + ", 값: " + map.get(key);
            result.add(strResult);
        }
        List<String> tenResult = result.subList(0, 9);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                tenResult), HttpStatus.OK);
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
