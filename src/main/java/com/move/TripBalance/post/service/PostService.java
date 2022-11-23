package com.move.TripBalance.post.service;

import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.mypage.controller.response.MyPostResponseDto;
import com.move.TripBalance.post.*;
import com.move.TripBalance.post.controller.request.PostRequestDto;
import com.move.TripBalance.post.controller.response.OtherPostResponseDto;
import com.move.TripBalance.post.controller.response.PostResponseDto;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.post.controller.response.TopFiveResponseDto;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateException;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HeartRepository heartRepository;
    private final MediaRepository mediaRepository;
    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;
    //게시글 생성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createPost(
            PostRequestDto postRequestDto, HttpServletRequest request) {
        Member member = authorizeToken(request);

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .author(member.getNickName())
                .local(Local.partsValue(Integer.parseInt(postRequestDto.getLocal())))
                .localDetail(LocalDetail.partsValue(Integer.parseInt(postRequestDto.getLocaldetail())))
                .content(postRequestDto.getContent())
                .pet(postRequestDto.getPet())
                .member(member)
                .build();
        postRepository.save(post);
        List<Media> mediaList = new ArrayList<>();
        Media media;
        for (int i = 0; i < postRequestDto.getMediaList().size(); i++) {
            media = Media.builder()
                    .post(post)
                    .imgURL(postRequestDto.getMediaList().get(i).getImgURL()).build();
            mediaRepository.save(media);
            mediaList.add(media);
        }
        post.setImgURL(mediaList);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                "게시글 생성 완료"), HttpStatus.OK);
    }

    //전체 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getAllPost(int page, Pageable pageable) {

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 20개씩 보여주기, 작성 시간을 기준으로 내림차순 정렬
        pageable =  PageRequest.of(page, 20, Sort.by("createdAt").descending());

        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : postList) {
            Long heartNum = heartRepository.countByPost(post);
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            postResponseDtos.add(
                    PostResponseDto.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .image(oneimage)
                            .heartNum(heartNum)
                            .build()
            );
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postResponseDtos), HttpStatus.OK
        );
    }

    //게시글 세부 조회
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getPost(Long postId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Post post = isPresentPost(postId);

        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }
        //좋아요 갯수
        Long heartNum = heartRepository.countByPost(post);


        //미디어 목록
        List<Media> mediaList = mediaRepository.findAllByPost(post);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mediaList.size(); i++) {
            list.add(mediaList.get(i).getImgURL());
        }

        //좋아요 여부
        boolean heartYn = false;
        if (userDetails != null) {
            Optional<Heart> heart = heartRepository.findByMemberAndPost(member, post);
            if (heart.isPresent()) {
                heartYn = true;
            }
        }

        PostResponseDto postList = PostResponseDto.builder()
                .title(post.getTitle())
                .author(post.getAuthor())
                .local(post.getLocal().toString())
                .localdetail(post.getLocalDetail().toString())
                .pet(post.getPet())
                .content(post.getContent())
                .heartNum(heartNum)
                .heartYn(heartYn)
                .nickName(member.getNickName())
                .profileImg(member.getProfileImg())
                .mediaList(list)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, postList), HttpStatus.OK);
    }

    //게시글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updatePost(Long postId, PostRequestDto postRequestDto, HttpServletRequest request) {


        // 토큰 확인
        Member member = authorizeToken(request);

        Post post = isPresentPost(postId);

        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        if (post.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        //저장된 미디어 목록 삭제
        mediaRepository.deleteAllByPost(post);
        post.update(postRequestDto);

        //수정된 미디어 목록 저장
        List<Media> mediaList = new ArrayList<>();
        Media media;

        for (int i = 0; i < postRequestDto.getMediaList().size(); i++) {
            media = Media.builder()
                    .post(post)
                    .imgURL(postRequestDto.getMediaList().get(i).getImgURL()).build();
            mediaRepository.save(media);
            mediaList.add(media);
        }
        post.setImgURL(mediaList);

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "게시글 수정 완료"), HttpStatus.OK);
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deletePost(Long postId, HttpServletRequest request) {

        // 토큰 확인
        Member member = authorizeToken(request);

        Post post = isPresentPost(postId);
        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        if (post.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        mediaRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "게시글 삭제 완료"), HttpStatus.OK);
    }

    //게시글 검색
    @Transactional
    public ResponseEntity<PrivateResponseBody> searchPosts(String keyword, int page, Pageable pageable) {
        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 20개씩 보여주기
        pageable =  PageRequest.of(page, 20);

        Page<Post> postList = postRepository.search(keyword, pageable);
        // 검색된 항목 담아줄 리스트 생성
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        //for문을 통해서 List에 담아주기
        for (Post post : postList) {
            Long heartNum = heartRepository.countByPost(post);
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            postResponseDtos.add(
                    PostResponseDto.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .image(oneimage)
                            .heartNum(heartNum)
                            .build()
            );
        }

        //결과값
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postResponseDtos), HttpStatus.OK
        );
    }

    //카테고리별 게시글 검색
    @Transactional
    public ResponseEntity<PrivateResponseBody> searchLocalPosts(Long local, String keyword, int page, Pageable pageable) {

        // 페이징 처리 -> 요청한 페이지 값(0부터 시작), 20개씩 보여주기
        pageable =  PageRequest.of(page, 20);

        // enum으로 나눈 지역 코드 불러오기
        Local localEnum = Local.partsValue(Math.toIntExact(local));

        // keyword를 통해서 게시글 불러오기
        Page<Post> postList = postRepository.search(keyword, pageable);

        // 리스트 생성
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : postList) {
            //지역 분류 진행
            if (post.getLocal().equals(localEnum)) {
                // 미디어 파일 추출 및 할당
                List<Media> oneimage = mediaRepository.findFirstByPost(post);

                postResponseDtos.add(PostResponseDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .image(oneimage)
                        .localdetail(post.getLocalDetail().toString())
                        .build());
            }
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postResponseDtos), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // 토큰 확인 여부
    public Member authorizeToken(HttpServletRequest request) {

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

    // 좋아요 순으로 포스트 5개
    @Transactional
    public ResponseEntity<PrivateResponseBody> getTop5Posts() {
        List<TopFiveResponseDto> topFiveList = new ArrayList<>();
        List<Heart> hearts = heartRepository.findAll();
        List<Post> fivePostList = postRepository.findTop5ByHeartsIn(hearts);

        // 미디어, 좋아요 갯수 추출 및 할당
        for (Post post : fivePostList) {
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            String img = oneimage.get(0).getImgURL();
            Long heartNum = heartRepository.countByPost(post);

            topFiveList.add(TopFiveResponseDto.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .img(img)
                    .heartNum(heartNum)
                    .build());
        }
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                topFiveList), HttpStatus.OK);
    }

    // ~님의 다른 글
    public ResponseEntity<PrivateResponseBody> getOtherPosts(Long memberId){

        // 멤버 정보 추출
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // 내가 작성한 포스트 repo에서 추출
        List<Post> memberPosts = postRepository.findAllByMember(memberInfo);

        // 내가 작성한 포스트가 없을 때 메시지 반환
        if(memberPosts.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        List<OtherPostResponseDto> myPostList = new ArrayList<>();

        // 내가 작성한 포스트 목록 반환
        for(Post post : memberPosts){

            // 이미지 파일 넣어주기
            Media img = mediaRepository.findFirstByPost(post).get(0);
            myPostList.add(OtherPostResponseDto.builder()
                    .nickName(memberInfo.getNickName())
                    .profileImg(memberInfo.getProfileImg())
                    .title(post.getTitle())
                    .local(post.getLocal().toString())
                    .img(img.getImgURL())
                    .build());
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                myPostList), HttpStatus.OK);
    }
}
