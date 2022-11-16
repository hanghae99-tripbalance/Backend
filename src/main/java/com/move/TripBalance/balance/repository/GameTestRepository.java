package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.GameTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTestRepository extends JpaRepository<GameTest, Long> {
}


