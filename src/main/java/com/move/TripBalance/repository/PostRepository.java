package com.move.TripBalance.repository;

import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop5ByHearts(LocalDateTime now);
}
