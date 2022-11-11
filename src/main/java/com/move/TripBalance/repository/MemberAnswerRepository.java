package com.move.TripBalance.repository;

import com.move.TripBalance.domain.MemberAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {
}
