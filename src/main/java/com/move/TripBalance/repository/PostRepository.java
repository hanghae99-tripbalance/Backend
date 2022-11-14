package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findAllByOrderByCreatedAtDesc();

//  List<Post> findByTitleOrContentContaining(String keyword);

  @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDERBY p.created_at desc" , nativeQuery = true)
  List <Post> search(@Param("keyword") String keyword);
}
