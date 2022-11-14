package com.move.TripBalance.domain;

public enum LawCode {
    서울("서울", 1100000000L),

    //강원도
    춘천("춘천", 4211000000L),
    원주("원주", 4213000000L),
    강릉("강릉", 4215000000L),
    동해("동해", 4217000000L),
    태백("태백", 4219000000L),
    속초("속초", 4221000000L),


    ;

    private final String name;
    private final Long value;

    LawCode(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return this.name;
    }

    public Long getValue(){
        return this.value;
    }
}
