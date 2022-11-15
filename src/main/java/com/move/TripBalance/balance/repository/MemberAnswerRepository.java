package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.MemberAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {
}
