package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLng(String lat, String lng);
}
