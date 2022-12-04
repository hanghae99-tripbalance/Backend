package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Result;

import java.util.List;

public interface ResultCustomRepository {

    List<Result> findByLocation(String districtName);

    Result findByLocationAndType(String districtName, String comp);

    Result findByLocationAndAge(String districtName, String age);

    Result findByLocationAndGender(String districtName, String gender);

}
