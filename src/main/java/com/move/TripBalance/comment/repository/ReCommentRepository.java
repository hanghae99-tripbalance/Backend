package com.move.TripBalance.comment.repository;

import java.util.List;
import java.util.Optional;

import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.comment.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReCommentRepository extends JpaRepository<ReComment, Long> {

    List<ReComment> findAllByComment(Comment comment);

    Optional<ReComment> findById(Long id);
}
