package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.domain.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

}
