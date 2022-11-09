package com.move.TripBalance.service;


import com.move.TripBalance.domain.Heart;
import com.move.TripBalance.domain.Member;
import com.move.TripBalance.domain.Post;
import com.move.TripBalance.domain.UserDetailsImpl;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.exception.StatusCode;
import com.move.TripBalance.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final PostService postService;

    // 좋아요, 좋아요취소
    public ResponseEntity<PrivateResponseBody> heart (Long  postId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Post post = postService.isPresentPost(postId);

        // memberId, postId 존재여부 확인
        Optional<Heart> heart = heartRepository.findByMemberAndPost(member, post);

        // 1. 좋아요 기록이 있으면 삭제 ( = 좋아요 취소)
        if(heart.isPresent()) {
            heartRepository.deleteById(heart.get().getHeartId());
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"좋아요 취소"), HttpStatus.OK);
        } else {
            // 2.좋아요 기록이 없으면 저장한다.
            Heart heartList = Heart.builder()
                    .post(post)
                    .member(member)
                    .build();
            heartRepository.save(heartList);
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"좋아요!"), HttpStatus.OK);
        }
    }
}
