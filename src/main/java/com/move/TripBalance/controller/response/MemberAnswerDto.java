package com.move.TripBalance.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberAnswerDto {
    private Long questionId;
    private boolean checkLeft;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public boolean isCheckLeft() {
        return checkLeft;
    }

    public void setCheckLeft(boolean answer) {
        this.checkLeft = answer;
    }
}
