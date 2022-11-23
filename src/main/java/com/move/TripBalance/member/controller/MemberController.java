package com.move.TripBalance.member.controller;

import com.move.TripBalance.member.controller.request.IdCkeckRequestDto;
import com.move.TripBalance.member.controller.request.NickNameCheckRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.move.TripBalance.member.service.MemberService;
import com.move.TripBalance.member.controller.request.LoginRequestDto;
import com.move.TripBalance.member.controller.request.MemberRequestDto;
import com.move.TripBalance.oauth.service.KakaoMemberService;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tb")
public class MemberController {

  private final MemberService memberService;

  private final KakaoMemberService kakaoMemberService;


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


  //아이디 중복 체크 API
  @PostMapping(value = "/signup/idcheck")
  public ResponseEntity<PrivateResponseBody> idcheck(@RequestBody IdCkeckRequestDto requestDto) {
    return memberService.idcheck(requestDto);
  }

  //닉네임 중복 체크 API
  @PostMapping(value = "/signup/nicknamecheck")
  public ResponseEntity<PrivateResponseBody> nicknamecheck(@RequestBody NickNameCheckRequestDto requestDto) {
    return memberService.nicknamecheck(requestDto);

  // 카카오 로그인
  @GetMapping("/ouath/kakao")
  public ResponseEntity<PrivateResponseBody> kakaoSignup(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
    return kakaoMemberService.kakaoLogin(code, response);
  }
}
