package com.move.TripBalance.post.repository;

import com.move.TripBalance.heart.domain.Heart;
import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//  Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

  @Query(value = "SELECT * FROM post WHERE title LIKE %:keyword% OR content LIKE %:keyword% ORDER BY created_at desc" , nativeQuery = true)
  Page <Post> search(@Param("keyword") String keyword, Pageable pageable);

  List<Post> findAllByMember(Member member);

  List<Post> findTop10ByHeartsIn(List<Heart> hearts);

}
