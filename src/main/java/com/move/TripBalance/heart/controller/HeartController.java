package com.move.TripBalance.heart.controller;

import com.move.TripBalance.heart.service.HeartService;
import com.move.TripBalance.shared.configuration.SwaggerAnnotation;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class HeartController {
    private final HeartService heartService;

    // 좋아요, 좋아요취소
    @SwaggerAnnotation
    @PostMapping("/posts/{postId}/heart")
    public ResponseEntity<PrivateResponseBody> heart(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return heartService.heart(postId, userDetails);
    }
}
