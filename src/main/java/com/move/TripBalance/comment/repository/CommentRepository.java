package com.move.TripBalance.comment.repository;

import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //post에서 댓글 가지고 오기
    List<Comment> findAllByPost(Post post);
//    List<Comment> findAllByMember(Member member);
//    int countAllByPost(Post post);
}
