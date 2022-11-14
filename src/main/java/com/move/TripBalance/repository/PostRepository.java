package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop5ByHearts(LocalDateTime now);
    List<Post> findAllByMember(Member member);

    List<Post> findAllByOrderByModifiedAtDesc();

 /*   @Modifying
    @Query("update Post p set p.view = value(a) where p.memberId = :memberId")
    int updateView(Long memberId);
*/
}
