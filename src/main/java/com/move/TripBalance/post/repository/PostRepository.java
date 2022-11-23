package com.move.TripBalance.post.repository;

import com.move.TripBalance.heart.Heart;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.post.Local;
import com.move.TripBalance.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

  @Query(value = "SELECT * FROM post WHERE title LIKE %:keyword% OR content LIKE %:keyword% ORDER BY created_at desc" , nativeQuery = true)
  Page <Post> search(@Param("keyword") String keyword, Pageable pageable);

  List<Post> findTop5ByHeartsIn(List<Heart> hearts);

  List<Post> findAllByMember(Member member);

}
