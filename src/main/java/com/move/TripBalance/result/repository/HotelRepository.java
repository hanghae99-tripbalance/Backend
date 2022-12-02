package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findAllByLocation(String location);
}