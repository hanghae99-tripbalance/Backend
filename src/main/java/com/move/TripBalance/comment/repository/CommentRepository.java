package com.move.TripBalance.comment.repository;

import com.move.TripBalance.comment.Comment;
import com.move.TripBalance.member.Member;
import com.move.TripBalance.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //post에서 댓글 가지고 오기
    List<Comment> findAllByPost(Post post);

    //멤버별로 찾기
    List<Comment> findAllByMember(Member member);
}
