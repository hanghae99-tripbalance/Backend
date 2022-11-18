package com.move.TripBalance.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.comment.controller.request.ReCommentRequestDto;
import com.move.TripBalance.member.Member;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommentId;

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


    @Column(nullable = false)
    private String content;

    public void update(ReCommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
