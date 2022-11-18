package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findByLeftId(Long ans);
    Question findByRightId(Long ans);
}
