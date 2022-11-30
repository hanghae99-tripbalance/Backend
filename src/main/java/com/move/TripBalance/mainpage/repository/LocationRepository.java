package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLng(String lat, String lng);

    Page<Location> findAll(Pageable pageable);
}
