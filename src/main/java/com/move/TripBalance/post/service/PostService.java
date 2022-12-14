package com.move.TripBalance.post.service;

import com.move.TripBalance.heart.domain.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.post.domain.Local;
import com.move.TripBalance.post.domain.LocalDetail;
import com.move.TripBalance.post.domain.Media;
import com.move.TripBalance.post.domain.Post;
import com.move.TripBalance.post.controller.request.PostRequestDto;
import com.move.TripBalance.post.controller.response.OtherPostResponseDto;
import com.move.TripBalance.post.controller.response.PostListResponseDto;
import com.move.TripBalance.post.controller.response.PostResponseDto;
import com.move.TripBalance.post.controller.response.TopFiveResponseDto;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostCustomRepository;
import com.move.TripBalance.post.repository.PostRepository;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateException;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

import static com.move.TripBalance.post.domain.QMedia.media;
import static com.move.TripBalance.heart.domain.QHeart.heart;
import static com.move.TripBalance.post.domain.QPost.post;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HeartRepository heartRepository;
    private final MediaRepository mediaRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final PostCustomRepository postCustomRepository;
    private final JPAQueryFactory jpaQueryFactory;


    //????????? ??????
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
                "????????? ?????? ??????"), HttpStatus.OK);
    }

    //?????? ????????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getAllPost(int page, Pageable pageable) {

        // ????????? ?????? -> ????????? ????????? ???(0?????? ??????), 20?????? ????????????, ?????? ????????? ???????????? ???????????? ??????
        pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());

        Page<Post> postList = postCustomRepository.findAllByOrderByCreatedAtDesc(pageable);

        Boolean isLastPage = postList.isLast();

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
                            .authorId(post.getMember().getMemberId())
                            .profileImg(post.getMember().getProfileImg())
                            .author(post.getMember().getNickName())
                            .build()
            );
        }

        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        postListResponseDtoList.add(
                PostListResponseDto.builder()
                        .postResponseDtoList(postResponseDtos)
                        .isLastPage(isLastPage)
                        .build()
        );

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postListResponseDtoList), HttpStatus.OK
        );
    }

    //????????? ?????? ??????
    @Transactional(readOnly = true)
    public ResponseEntity<PrivateResponseBody> getPost(Long postId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Post post = isPresentPost(postId);

        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }
        //????????? ??????
        Long heartNum = heartRepository.countByPost(post);

        //????????? ?????? 10?????? ????????????
        List<Media> mediaList = jpaQueryFactory
                .selectFrom(media)
                .where(media.post.postId.eq(post.getPostId()))
                .limit(10)
                .fetch();

        //???????????? ????????????
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mediaList.size(); i++) {
            list.add(mediaList.get(i).getImgURL());
        }

        //????????? ??????
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
                .authorId(post.getMember().getMemberId())
                .build();

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, postList), HttpStatus.OK);
    }

    //????????? ??????
    @Transactional
    public ResponseEntity<PrivateResponseBody> updatePost(Long postId, PostRequestDto postRequestDto, HttpServletRequest request) {


        // ?????? ??????
        Member member = authorizeToken(request);

        Post post = isPresentPost(postId);

        if (null == post) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        if (post.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
        }
        //????????? ????????? ?????? ??????
        mediaRepository.deleteAllByPost(post);
        post.update(postRequestDto);

        //????????? ????????? ?????? ??????
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
                (StatusCode.OK, "????????? ?????? ??????"), HttpStatus.OK);
    }

    //????????? ??????
    @Transactional
    public ResponseEntity<PrivateResponseBody> deletePost(Long postId, HttpServletRequest request) {

        // ?????? ??????
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
                (StatusCode.OK, "????????? ?????? ??????"), HttpStatus.OK);
    }

    //????????? ??????
    @Transactional
    public ResponseEntity<PrivateResponseBody> searchPosts(String keyword, int page, Pageable pageable) {
        // ????????? ?????? -> ????????? ????????? ???(0?????? ??????), 20?????? ????????????
        pageable = PageRequest.of(page, 20);

        Page<Post> postList = postRepository.search(keyword, pageable);
        // ????????? ?????? ????????? ????????? ??????
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        //for?????? ????????? List??? ????????????
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

        Boolean isLastPage = postList.isLast();

        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        postListResponseDtoList.add(
                PostListResponseDto.builder()
                        .postResponseDtoList(postResponseDtos)
                        .isLastPage(isLastPage)
                        .build()
        );

        //?????????
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postListResponseDtoList), HttpStatus.OK
        );
    }

    //??????????????? ????????? ??????
    @Transactional
    public ResponseEntity<PrivateResponseBody> searchLocalPosts(Long local, String keyword, int page, Pageable pageable) {

        // ????????? ?????? -> ????????? ????????? ???(0?????? ??????), 20?????? ????????????
        pageable = PageRequest.of(page, 20);

        // enum?????? ?????? ?????? ?????? ????????????
        Local localEnum = Local.partsValue(Math.toIntExact(local));

        // keyword??? ????????? ????????? ????????????
        Page<Post> postList = postRepository.search(keyword, pageable);

        // ????????? ??????
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : postList) {
            //?????? ?????? ??????
            if (post.getLocal().equals(localEnum)) {
                // ????????? ?????? ?????? ??? ??????
                List<Media> oneimage = mediaRepository.findFirstByPost(post);

                postResponseDtos.add(PostResponseDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .image(oneimage)
                        .localdetail(post.getLocalDetail().toString())
                        .build());
            }
        }

        Boolean isLastPage = postList.isLast();

        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        postListResponseDtoList.add(
                PostListResponseDto.builder()
                        .postResponseDtoList(postResponseDtos)
                        .isLastPage(isLastPage)
                        .build()
        );

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                postListResponseDtoList), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    // ?????? ?????? ??????
    public Member authorizeToken(HttpServletRequest request) {

        // Access ?????? ????????? ??????
        if (request.getHeader("Authorization") == null) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Refresh ?????? ????????? ??????
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new PrivateException(StatusCode.LOGIN_EXPIRED_JWT_TOKEN);
        }

        // Access, Refresh ?????? ????????? ????????? ??????????????? ?????? ????????? ?????? ?????? ??????
        Member member = tokenProvider.getMemberFromAuthentication();

        // ????????? ?????? ?????? ??????
        return member;
    }

    // ????????? ????????? ????????? 5???
    @Transactional
    public ResponseEntity<PrivateResponseBody> getTop5Posts() {
        List<Long> hearts = jpaQueryFactory
                .select(heart.post.postId)
                .from(heart)
                .groupBy(heart.post.postId)
                .orderBy(heart.count().desc())
                .limit(10)
                .fetch();

        List<TopFiveResponseDto> list = new ArrayList<>();

        for (Long posts : hearts) {
            Post post1 = jpaQueryFactory
                    .selectFrom(post)
                    .where(post.postId.eq(posts))
                    .orderBy(post.heartNum.desc())
                    .fetchOne();
            Long heartNum = heartRepository.countByPost(post1);
            List<Media> oneimage = mediaRepository.findFirstByPost(post1);
            String img = oneimage.get(0).getImgURL();
            list.add(TopFiveResponseDto.builder()
                    .postId(post1.getPostId())
                    .title(post1.getTitle())
                    .img(img)
                    .heartNum(heartNum)
                    .build());
        }

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,
                list), HttpStatus.OK);
    }

    // ~?????? ?????? ???
    public ResponseEntity<PrivateResponseBody> getOtherPosts(Long memberId) {

        // ?????? ?????? ??????
        Optional<Member> member = memberRepository.findById(memberId);
        Member memberInfo = member.get();

        // ?????? ????????? ????????? repo?????? ??????
        List<Post> memberPosts = postRepository.findAllByMember(memberInfo);

        // ?????? ????????? ???????????? ?????? ??? ????????? ??????
        if (memberPosts.isEmpty()) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        List<OtherPostResponseDto> myPostList = new ArrayList<>();

        // ?????? ????????? ????????? ?????? ??????
        for (Post post : memberPosts) {

            // ????????? ?????? ????????????
            Media img = mediaRepository.findFirstByPost(post).get(0);
            myPostList.add(OtherPostResponseDto.builder()
                    .postId(post.getPostId())
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
