package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByLocation(String districtName);

    Result findByLocationAndType(String districtName, String comp);

    Result findByLocationAndAge(String districtName, String age);

    Result findByLocationAndGender(String districtName, String gender);
}
