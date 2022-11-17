package com.move.TripBalance.heart.repository;

import com.move.TripBalance.member.Member;
import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    Page<Heart> findAllByMember(Member member, Pageable pageable);
    Optional<Heart> findByMemberAndPost(Member member, Post post);
    Long countByPost(Post post);

    @Query(value = "select review_id from heart group by review_id having count(*) = (select max(heart_max) from ( select review_id, count(review_id) as heart_max from heart group by review_id) as result)", nativeQuery = true)
    Long heartMaxCount();

}
