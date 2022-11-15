package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.MemberCurrentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCurrentAnswerRepository extends JpaRepository<MemberCurrentAnswer, Long> {
}
