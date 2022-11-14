package com.move.TripBalance.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MemberAnswer {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private Long memberId; // 회원 번호
    private Long lastQuestionId; // 마지막 문제의 id
    private boolean lastAnswer; // 마지막 답변의 대답(left: true, right: false)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getLastQuestionId() {
        return lastQuestionId;
    }

    public void setLastQuestionId(Long lastQuestionId) {
        this.lastQuestionId = lastQuestionId;
    }

    public boolean isLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(boolean lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

}
