package com.move.TripBalance.controller;

import com.move.TripBalance.configuration.SwaggerAnnotation;
import com.move.TripBalance.controller.request.LoginRequestDto;
import com.move.TripBalance.controller.request.MemberRequestDto;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  //회원 가입 API
  @PostMapping(value = "/tb/signup")
  public ResponseEntity<PrivateResponseBody> signup(@RequestBody MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  //로그인 API
  @PostMapping(value = "/tb/login")
  public ResponseEntity<PrivateResponseBody> login(@RequestBody LoginRequestDto requestDto,
                                                   HttpServletResponse response) {
    return memberService.login(requestDto, response);
  }

  //로그아웃 API
  @SwaggerAnnotation
  @PostMapping(value = "/tb/logout")
  public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
