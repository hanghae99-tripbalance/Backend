package com.move.TripBalance.comment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReComment extends Timestamped {

    //대댓글 Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommentId;

    //내용
    @Column(nullable = false)
    private String content;

    // 작성자
    @Column(nullable = false)
    private String author;

    //대댓글을 작성할 멤버 id
    @JsonIgnore
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    //댓글 id
    @JsonIgnore
    @JoinColumn(name = "commentId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    //업데이트
    public void update(ReCommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }

    //멤버 확인
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
