package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Local;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {


  List<Post> findAllByOrderByCreatedAtDesc();

  @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDERBY p.created_at desc" , nativeQuery = true)
  List <Post> search(@Param("keyword") String keyword);

    List<Post> findTop5ByHeartsIn(List<Heart> hearts);
    List<Post> findAllByMember(Member member);
    List<Post> findAllByLocalOrderByCreatedAtDesc(Local localEnum);


    List<Post> findAllByOrderByModifiedAtDesc();

}
