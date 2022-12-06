package com.move.TripBalance.result.repository;

import com.move.TripBalance.result.Blog;

import java.util.List;

public interface BlogCustomRepository {

    List<Blog> findAllByLocation(String location);
}
