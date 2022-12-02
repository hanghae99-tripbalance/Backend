package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByLocation(String location);
}
