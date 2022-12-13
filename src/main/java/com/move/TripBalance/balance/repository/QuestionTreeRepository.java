package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.domain.QuestionTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTreeRepository extends JpaRepository<QuestionTree, Long> {

}
