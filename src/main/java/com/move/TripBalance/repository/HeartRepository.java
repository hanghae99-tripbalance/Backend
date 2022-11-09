package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Optional<Heart> findByMemberAndPost(Member member, Post post);
    Long countByPost(Post post);

    @Query(value = "select review_id from heart group by review_id having count(*) = (select max(heart_max) from ( select review_id, count(review_id) as heart_max from heart group by review_id) as result)", nativeQuery = true)
    Long heartMaxCount();

}
