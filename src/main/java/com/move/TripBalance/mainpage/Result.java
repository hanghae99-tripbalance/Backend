package com.move.TripBalance.mainpage;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
// 법정동 코드와 sk api를 통해 도출한 인구 통계
public class Result{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String location;
    private String age;
    private String type;
    private String gender;
    private Long peopleCnt;
}
