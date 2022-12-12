package com.move.TripBalance.comment.domain;

import javax.persistence.*;

import com.move.TripBalance.comment.controller.request.CommentRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.post.domain.Post;
import com.move.TripBalance.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

    // 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    // 내용
    @Column(nullable = false)
    private String content;

    // 작성자
    @Column(nullable = false)
    private String author;

    // 게시글
    @JsonIgnore
    @JoinColumn(name = "postId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    // 멤버
    @JsonIgnore
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    //업데이트
    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }

    //멤버 확인
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }


}
