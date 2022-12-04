package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Hotel;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.move.TripBalance.result.QHotel.hotel;

@Repository
@RequiredArgsConstructor
public class HotelCustomRepositoryImpl implements HotelCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Hotel> findAllByLocation(String location) {
        return jpaQueryFactory.selectFrom(hotel)
                .where(hotel.location.eq(location))
                .fetch();
    }
}