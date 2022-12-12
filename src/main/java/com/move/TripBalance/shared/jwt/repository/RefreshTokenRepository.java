package com.move.TripBalance.shared.jwt.repository;

import com.move.TripBalance.member.domain.Member;

import java.util.Optional;

import com.move.TripBalance.shared.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByMember(Member member);
}
