package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    List<Heart> findAllByMember(Member member);
}
