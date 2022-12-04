package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Hotel;

import java.util.List;

public interface HotelCustomRepository {
    List<Hotel> findAllByLocation(String location);
}
