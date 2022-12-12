package com.move.TripBalance.balance.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.balance.controller.response.ChoiceResponseDto;
import com.move.TripBalance.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Setter
public class GameResult {

    // 고유 아이디
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameId;

    //1번 문제 답변
    private Long answer1;

    //2번 문제 답변
    private Long answer2;

    //3번 문제 답변
    private Long answer3;

    //4번 문제 답변
    private Long answer4;

    //5번 문제 답변
    private Long answer5;

    //게임 결과
    private String gameResult;

    //회원고유 아이디
    @JsonIgnore
    @JoinColumn(name = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(ChoiceResponseDto ChoiceResponseDto) {
        this.answer1 = ChoiceResponseDto.getAnswer1();
        this.answer2 = ChoiceResponseDto.getAnswer2();
        this.answer3 = ChoiceResponseDto.getAnswer3();
        this.answer4 = ChoiceResponseDto.getAnswer4();
        this.answer5 = ChoiceResponseDto.getAnswer5();
        this.gameResult = ChoiceResponseDto.getTrip();
    }

}
