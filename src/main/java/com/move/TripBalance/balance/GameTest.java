package com.move.TripBalance.balance;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.move.TripBalance.member.Member;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Setter
public class GameTest {

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
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

}
