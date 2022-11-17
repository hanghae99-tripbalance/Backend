package com.move.TripBalance.mainpage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// 위도와 경도를 통해 도출한 지역 정보
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 5L;
    private String lat = "35.85316944";
    private String lng = "129.2270222";
    private String result = "경상북도 경주시";
}
