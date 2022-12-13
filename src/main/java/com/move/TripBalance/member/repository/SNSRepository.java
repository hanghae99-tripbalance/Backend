package com.move.TripBalance.member.repository;

import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.member.domain.SNS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SNSRepository extends JpaRepository<SNS, Long> {
    SNS findByMember(Member member);
}
