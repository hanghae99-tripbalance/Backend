package com.move.TripBalance.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLeftId() {
        return leftId;
    }

    public void setLeftId(Long leftId) {
        this.leftId = leftId;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    public String getLeftAnswer() {
        return leftAnswer;
    }

    public void setLeftAnswer(String leftAnswer) {
        this.leftAnswer = leftAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }
}
