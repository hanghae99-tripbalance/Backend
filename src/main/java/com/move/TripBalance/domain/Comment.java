package com.move.TripBalance.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnore
    @JoinColumn(name = "postId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String nickName;

    @JoinColumn(name = "parentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent;


}
