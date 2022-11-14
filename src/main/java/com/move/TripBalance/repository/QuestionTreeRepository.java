package com.move.TripBalance.repository;

import com.move.TripBalance.domain.QuestionTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTreeRepository extends JpaRepository<QuestionTree, Long> {
}
