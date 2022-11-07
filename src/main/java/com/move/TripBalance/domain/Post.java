package com.move.TripBalance.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
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
    private String author;

    // 카테고리
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

    // 게시글 작성 시 같이 업로드할 미디어 파일들 (존재하지 않을 수도 있음)
//    @OneToMany(
//            mappedBy = "post",
//            fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    private List<Media> medias;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
