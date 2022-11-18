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


  List<Post> findAllByOrderByCreatedAtDesc();

  @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDERBY p.created_at desc" , nativeQuery = true)
  List <Post> search(@Param("keyword") String keyword);

    List<Post> findTop5ByHeartsIn(List<Heart> hearts);
    Page<Post> findAllByMember(Member member, Pageable pageable);
  List<Post> findAllByMember(Member member);

  List<Post> findAllByLocalOrderByCreatedAtDesc(Local localEnum);


    List<Post> findAllByOrderByModifiedAtDesc();

}
