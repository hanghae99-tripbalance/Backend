package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Result;


public interface ResultCustomRepository {

    Result findByLocationAndType(String districtName, String comp);

    Result findByLocationAndAge(String districtName, String age);

    Result findByLocationAndGender(String districtName, String gender);

}
