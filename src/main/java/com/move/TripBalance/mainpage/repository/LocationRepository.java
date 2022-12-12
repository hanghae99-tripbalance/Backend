package com.move.TripBalance.mainpage.repository;

import com.move.TripBalance.mainpage.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}
