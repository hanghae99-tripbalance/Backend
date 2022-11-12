package com.move.TripBalance.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Result extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String location;
    private String age;
    private String type;
    private String gender;
    private Long peopleCnt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
