package com.move.TripBalance.service;

import com.move.TripBalance.controller.request.PostRequestDto;
import com.move.TripBalance.controller.response.PostResponseDto;
import com.move.TripBalance.domain.*;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.exception.PrivateException;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.exception.StatusCode;
import com.move.TripBalance.jwt.TokenProvider;
import com.move.TripBalance.repository.HeartRepository;
import com.move.TripBalance.repository.MediaRepository;
import com.move.TripBalance.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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

    //게시글 생성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createPost(
            PostRequestDto postRequestDto, HttpServletRequest request) {
        Member member = authorizeToken(request);

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .nickName(member.getNickName())
                .local(Local.partsValue(Integer.parseInt(postRequestDto.getLocal())))
                .localDetail(LocalDetail.partsValue(Integer.parseInt(postRequestDto.getLocaldetail())))
                .content(postRequestDto.getContent())
                .pet(postRequestDto.getPet())
                .member(member)
                .build();
        postRepository.save(post);
        List<Media> mediaList = new ArrayList<>();
        Media media;
        for(int i = 0; i < postRequestDto.getMediaList().size(); i++){
            media = Media.builder()
                    .post(post)
                    .imgURL(postRequestDto.getMediaList().get(i).getImgURL()).build();
            mediaRepository.save(media);
            mediaList.add(media);
        }
        post.setImgURL(mediaList);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK ,
                "게시글 생성 완료"), HttpStatus.OK);
    }

    //전체 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getAllPost() {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : postList)  {
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
    public ResponseEntity<PrivateResponseBody> getPost(Long id, UserDetailsImpl userDetails) {
        Post post = isPresentPost(id);

        //좋아요 갯수
        Long heartNum = heartRepository.countByPost(post);

        //미디어 목록
        List<Media> mediaList = mediaRepository.findAllByPost(post);
        List<String> list = new ArrayList<>();
        for(int i = 0; i < mediaList.size(); i++){
            list.add(mediaList.get(i).getImgURL());
        }

        //좋아요 여부
        boolean heartYn = false;
        if(userDetails != null) {
            Member member = userDetails.getMember();
            Optional<Heart> heart = heartRepository.findByMemberAndPost(member, post);
            if(heart.isPresent()) {
                heartYn = true;
            }
        }

        PostResponseDto postList = PostResponseDto.builder()
                .title(post.getTitle())
                .nickName(post.getNickName())
                .local(post.getLocal().toString())
                .localdetail(post.getLocalDetail().toString())
                .pet(post.getPet())
                .content(post.getContent())
                .heartNum(heartNum)
                .heartYn(heartYn)
                .mediaList(list)
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,postList), HttpStatus.OK);
    }

    //게시글 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updatePost(Long postId, PostRequestDto postRequestDto, HttpServletRequest request) {


        // 토큰 확인
        Member member = authorizeToken(request);

        Post post = isPresentPost(postId);

        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND,null),HttpStatus.OK);
        }

        if (post.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST,null),HttpStatus.OK);
        }
        //저장된 미디어 목록 삭제
        mediaRepository.deleteAllByPost(post);
        post.update(postRequestDto);


        //수정된 미디어 목록 저장
        List<Media> mediaList = new ArrayList<>();
        Media media;

        for(int i = 0; i < postRequestDto.getMediaList().size(); i++){
            media = Media.builder()
                    .post(post)
                    .imgURL(postRequestDto.getMediaList().get(i).getImgURL()).build();
            mediaRepository.save(media);
            mediaList.add(media);
        }
        post.setImgURL(mediaList);

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK,"게시글 수정 완료"),HttpStatus.OK);
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deletePost(Long postId, HttpServletRequest request) {

        // 토큰 확인
        Member member = authorizeToken(request);

        Post post = isPresentPost(postId);
        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND,null),HttpStatus.OK);
        }

        if (post.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST,null),HttpStatus.OK);
        }
        mediaRepository.deleteAllByPost(post);
        postRepository.delete(post);
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK,"게시글 삭제 완료"),HttpStatus.OK);
    }
    //게시글 검색
    @Transactional
    public ResponseEntity<PrivateResponseBody> searchPosts(String keyword) {
        List<Post> postList = postRepository.search(keyword);
        // 검색된 항목 담아줄 리스트 생성
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        //for문을 통해서 List에 담아주기
        for (Post post : postList) {
            Long heartNum = heartRepository.countByPost(post);
            List<Media> oneimage = mediaRepository.findFirstByPost(post);
            postResponseDtos.add(
                    PostResponseDto.builder()
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
}
