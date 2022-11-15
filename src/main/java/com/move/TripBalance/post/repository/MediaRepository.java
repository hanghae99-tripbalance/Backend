package com.move.TripBalance.post.repository;

import com.move.TripBalance.post.Media;
import com.move.TripBalance.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findAllByPost(Post post);
    List<Media> findFirstByPost(Post post);
    List<Media> deleteAllByPost(Post post);
}
