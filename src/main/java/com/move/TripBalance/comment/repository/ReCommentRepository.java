package com.move.TripBalance.comment.repository;

import java.util.List;
import java.util.Optional;

import com.move.TripBalance.comment.domain.Comment;
import com.move.TripBalance.comment.domain.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReCommentRepository extends JpaRepository<ReComment, Long> {

    //댓글에서 대댓글 찾기
    List<ReComment> findAllByComment(Comment comment);

    //optional로 id별로 대댓글 찾기
    Optional<ReComment> findById(Long id);
}
