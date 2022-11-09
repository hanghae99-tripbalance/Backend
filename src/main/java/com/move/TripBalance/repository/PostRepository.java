package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();

  @Modifying
  @Query("update Post p set p.view = value(a) where p.memberId = :memberId")
  int updateView(Long memberId);

}
