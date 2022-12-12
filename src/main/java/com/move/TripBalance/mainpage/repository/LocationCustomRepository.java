package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.domain.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationCustomRepository {

    Location findByResult(String result);

    Page<Location> findAll(Pageable pageable);
}
