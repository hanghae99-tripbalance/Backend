package com.move.TripBalance.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MemberCurrentAnswer {
    @Id
    private Long memberId;

    private Long questionId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
