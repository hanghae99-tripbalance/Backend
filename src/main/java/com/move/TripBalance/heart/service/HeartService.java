package com.move.TripBalance.heart.service;


import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.heart.domain.Heart;
import com.move.TripBalance.heart.repository.HeartRepository;
import com.move.TripBalance.post.domain.Post;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final PostService postService;
    private final EntityManager em;

    // 좋아요, 좋아요취소
    @Transactional
    public ResponseEntity<PrivateResponseBody> heart (Long  postId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Post post = postService.isPresentPost(postId);

        // memberId, postId 존재여부 확인
        Optional<Heart> heart = heartRepository.findByMemberAndPost(member, post);

        // 1. 좋아요 기록이 있으면 삭제 ( = 좋아요 취소)
        if(heart.isPresent()) {
            heartRepository.deleteById(heart.get().getHeartId());
            post.heartNumCancel();
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"좋아요 취소"), HttpStatus.OK);
        } else {
            // 2.좋아요 기록이 없으면 저장한다.
            Heart heartList = Heart.builder()
                    .post(post)
                    .member(member)
                    .build();
            post.heartNumUpdate();
            heartRepository.save(heartList);
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"좋아요!"), HttpStatus.OK);
        }
    }
}
