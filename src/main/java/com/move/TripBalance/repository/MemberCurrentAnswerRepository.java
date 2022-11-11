package com.move.TripBalance.repository;

import com.move.TripBalance.domain.MemberCurrentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCurrentAnswerRepository extends JpaRepository<MemberCurrentAnswer, Long> {
}
