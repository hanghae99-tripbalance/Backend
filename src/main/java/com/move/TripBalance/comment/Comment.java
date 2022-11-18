package com.move.TripBalance.comment;

import javax.persistence.*;

import com.move.TripBalance.comment.controller.request.CommentRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.post.Post;
import com.move.TripBalance.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @JoinColumn(name = "postId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;



    // 멤버
    @JsonIgnore
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }


}
