package com.move.TripBalance.member.repository;

import com.move.TripBalance.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickName(String nickname);

    Optional<Member> findByKakaoId(Long kakaoId);
}
