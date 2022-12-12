package com.move.TripBalance.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Media{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long Mediaid;

    @Column(nullable = false)
    private String imgURL;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId")
    private Post post;

    }

