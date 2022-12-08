package com.move.TripBalance.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.post.controller.request.PostRequestDto;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.shared.domain.Timestamped;
import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.heart.Heart;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Setter
public class Post extends Timestamped {

    // 고유 아이디
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postId;

    // 게시글 제목
    @Column(nullable = false)
    private String title;

    // 작성자
    @Column(nullable = false)
    private String author;

    // 지역
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Local local;

    // 지역 디테일
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocalDetail localDetail;

    //반려동물
    @Column(nullable = false)
    private int pet;

    // 게시글 내용
    @Column(nullable = false)
    private String content;

    // 좋아요 개수
    @Column(nullable = true)
    private int heartNum;

    // 댓글
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 좋아요
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts = new ArrayList<>();

    //미디어 파일
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Media> imgURL = new ArrayList<>();

    @JsonIgnore
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.pet = postRequestDto.getPet();
        this.content = postRequestDto.getContent();
        this.local = Local.partsValue(Integer.parseInt(postRequestDto.getLocal()));
        this.localDetail = LocalDetail.partsValue(Integer.parseInt(postRequestDto.getLocaldetail()));
    }

    public void heartNumUpdate(){
        this.heartNum = heartNum+1;
    }

    public void heartNumCancel(){
        int a = -1;
        this.heartNum = heartNum+a;
    }
    // 닉네임 변경시 바로 반영을 위한 업데이트
    public void updateMember(Member member){
        this.author = member.getNickName();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
