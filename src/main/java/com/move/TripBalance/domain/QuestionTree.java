package com.move.TripBalance.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 문제 트리에 대한 정보를 담고 있는 테이블(앞 문제들에 대한 추적이 가능해진다.)
 */
@Entity
public class QuestionTree {
    @Id
    private Long lastId;
    private Long question1;
    private Long question2;
    private Long question3;
    private Long question4;
//    private Long question5;

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Long getQuestion1() {
        return question1;
    }

    public void setQuestion1(Long question1) {
        this.question1 = question1;
    }

    public Long getQuestion2() {
        return question2;
    }

    public void setQuestion2(Long question2) {
        this.question2 = question2;
    }

    public Long getQuestion3() {
        return question3;
    }

    public void setQuestion3(Long question3) {
        this.question3 = question3;
    }

    public Long getQuestion4() {
        return question4;
    }

    public void setQuestion4(Long question4) {
        this.question4 = question4;
    }
}
