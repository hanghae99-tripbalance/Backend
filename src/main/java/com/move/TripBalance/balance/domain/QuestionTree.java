package com.move.TripBalance.balance.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 문제 트리에 대한 정보를 담고 있는 테이블(앞 문제들에 대한 추적이 가능해진다.)
 */
@Getter
@Setter
@Entity
public class QuestionTree {
    @Id
    private Long lastId;
    private Long question1;
    private Long question2;
    private Long question3;
    private Long question4;
    private Long question5;


}
