package com.move.TripBalance.balance.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "question_id",columnList = "id")})
public class Question {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    // 문제 내용
    private String question;
    // 부모 문제 id
    private Long parentId;
    // 왼쪽 선택지 문제 id
    private Long leftId;
    // 오른쪽 선택지 문제 id
    private Long rightId;
    // 왼쪽 선택지 내용
    private String leftAnswer;
    // 오른쪽 선택지 내용
    private String rightAnswer;
    // 최종 결과 여행지
    private String trip;
    // 여행지 상세 정보
    private String tripcontent;
}
