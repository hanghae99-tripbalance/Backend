package com.move.TripBalance.post.controller;

import com.move.TripBalance.post.domain.Media;
import com.move.TripBalance.post.repository.MediaRepository;
import com.move.TripBalance.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("게시글 테스트")
class PostControllerTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Media media;
    private MediaRepository mediaRepository;

    private String title;
    private String content;
    private String local;
    private String localdetail;
    private int pet;

//    @BeforeEach
//    void setup() {
//        title = "제목";
//        content = "내용";
//        local = "1";
//        localdetail = "2";
//        pet = 0;
//    }

    @Test
    @DisplayName("게시글 생성")
    void createPost() {
//        System.out.println("게시글 생성 테스트 시작");
//        System.out.println();
//        Member member = Member.builder()
//                .memberId(123456L)
//                .email("test@test.com")
//                .pw("123456789!")
//                .build();
//
//        Media media = Media.builder()
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .imgURL("URL")
//                .build();
//
//        List<Media> mediaList = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            mediaList.add(media);
//        }
//        System.out.println(mediaList);
//
//        PostRequestDto postRequestDto = PostRequestDto.builder()
//                .title(title)
//                .content(content)
//                .local(local)
//                .localdetail(localdetail)
//                .pet(pet)
//                .mediaList(mediaList)
//                .build();
//
//        Post post = Post.builder()
//                .title(postRequestDto.getTitle())
//                .author(member.getNickName())
//                .local(Local.partsValue(Integer.parseInt(postRequestDto.getLocal())))
//                .localDetail(LocalDetail.partsValue(Integer.parseInt(postRequestDto.getLocaldetail())))
//                .content(postRequestDto.getContent())
//                .pet(postRequestDto.getPet())
//                .member(member)
//                .build();
//        postRepository.save(post);
//        List<Media> mediaList1 = new ArrayList<>();
//        Media media;
//        for (int i = 0; i < postRequestDto.getMediaList().size(); i++) {
//            media = Media.builder()
//                    .post(post)
//                    .imgURL(postRequestDto.getMediaList().get(i).getImgURL()).build();
//            mediaRepository.save(media);
//            mediaList1.add(media);
//        }
//        post.setImgURL(mediaList1);

    }

    @Test
    @DisplayName("전체 게시글 조회")
    void getAllPost() {
    }

    @Test
    void getPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void search() {
    }

    @Test
    void searchLocal() {
    }

    @Test
    void getBestFive() {
    }

    @Test
    void getOtherPosts() {
    }
}