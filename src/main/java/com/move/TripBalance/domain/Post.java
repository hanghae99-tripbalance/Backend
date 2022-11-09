package com.move.TripBalance.domain;

import com.move.TripBalance.controller.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Post extends Timestamped{

    // 고유 아이디
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postId;

    // 게시글 제목
    @Column(nullable = false)
    private String title;

    // 작성자
    @Column(nullable = false)
    private String nickName;

    // 지역
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Local local;

    //반려동물
    @Column(nullable = false)
    private int pet;

    // 게시글 내용
    @Column(nullable = false)
    private String content;

    // 댓글
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 좋아요
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts = new ArrayList<>();

//    //미디어 파일
//    @OneToMany(
//            mappedBy = "post",
//            fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    private List<Media> medias;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Long countVisit;

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.pet = postRequestDto.getPet();
        this.content = postRequestDto.getContent();
        this.local = Local.partsValue(Integer.parseInt(postRequestDto.getLocal()));
    }
    private void updateVisit(Long countVisit){
        this.countVisit = countVisit;
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
