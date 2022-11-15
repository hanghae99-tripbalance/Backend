package com.move.TripBalance.controller;

import com.move.TripBalance.controller.request.LoginRequestDto;
import com.move.TripBalance.controller.request.MemberRequestDto;
import com.move.TripBalance.exception.PrivateResponseBody;
import com.move.TripBalance.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MemberController {

  private final MemberService memberService;

  //회원 가입 API
  @PostMapping(value = "/signup")
  public ResponseEntity<PrivateResponseBody> signup(@RequestBody MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  //로그인 API
  @PostMapping(value = "/login")
  public ResponseEntity<PrivateResponseBody> login(@RequestBody LoginRequestDto requestDto,
                                                   HttpServletResponse response) {
    return memberService.login(requestDto, response);
  }

  //로그아웃 API
  @PostMapping(value = "/logout")
  public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
