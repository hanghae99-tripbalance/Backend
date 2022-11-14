package com.move.TripBalance.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String question; // 문제 내용

    private Long parentId; // 부모 문제 id

    private Long leftId; // 왼쪽 선택지 문제 id

    private Long rightId; // 오른쪽 선택지 문제 id

    private String leftAnswer; // 왼쪽 선택지 내용

    private String rightAnswer; // 오른쪽 선택지 내용

    private String trip;

}
