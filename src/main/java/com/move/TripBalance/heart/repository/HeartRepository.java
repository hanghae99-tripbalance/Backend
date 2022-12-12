package com.move.TripBalance.heart.repository;

import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.heart.domain.Heart;
import com.move.TripBalance.post.domain.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    List<Heart> findAllByMember(Member member);
    Optional<Heart> findByMemberAndPost(Member member, Post post);
    Long countByPost(Post post);

    @Query(value = "select review_id from heart group by review_id having count(*) = (select max(heart_max) from ( select review_id, count(review_id) as heart_max from heart group by review_id) as result)", nativeQuery = true)
    Long heartMaxCount();

}
