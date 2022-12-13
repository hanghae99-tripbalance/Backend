package com.move.TripBalance.balance.repository;

import com.move.TripBalance.balance.domain.GameResult;
import com.move.TripBalance.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameChoiceRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findAllByMember(Member member);
}


