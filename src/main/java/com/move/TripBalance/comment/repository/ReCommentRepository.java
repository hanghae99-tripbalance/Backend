package com.move.TripBalance.comment.repository;

import java.util.List;
import java.util.Optional;

import com.move.TripBalance.member.Member;
import com.move.TripBalance.comment.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReCommentRepository extends JpaRepository<ReComment, Long> {
    List<ReComment> findAllByCommentId(Long commentId);
    List<ReComment> findAllByMember(Member member);
    Optional<ReComment> findById(Long id);
    int countAllByCommentId(Long id);
}
