package com.move.TripBalance.member.service;

import com.move.TripBalance.member.Member;
import com.move.TripBalance.member.SNS;
import com.move.TripBalance.member.controller.request.IdCkeckRequestDto;
import com.move.TripBalance.member.controller.request.LoginRequestDto;
import com.move.TripBalance.member.controller.request.MemberRequestDto;
import com.move.TripBalance.member.controller.request.NickNameCheckRequestDto;
import com.move.TripBalance.member.repository.SNSRepository;
import com.move.TripBalance.shared.jwt.controller.request.TokenDto;
import com.move.TripBalance.member.controller.response.MemberResponseDto;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final SNSRepository snsRepository;

    //회원가입
    @Transactional
    public ResponseEntity<PrivateResponseBody> createMember(MemberRequestDto requestDto) {

        // Email 중복 확인
        if (null != isPresentEmail(requestDto.getEmail())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_EMAIL, null), HttpStatus.OK);
        }

        // NickName 중복 확인
        if (null != isPresentNickName(requestDto.getNickName())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_NICKNAME, null), HttpStatus.OK);
        }

        // 비밀번호 중복 확인
        if (!requestDto.getPw().equals(requestDto.getPwConfirm())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_PASSWORD, null), HttpStatus.OK);
        }

        // 회원 정보 저장
        Member member = Member.builder()
                .email(requestDto.getEmail())
                .nickName(requestDto.getNickName())
                .pw(passwordEncoder.encode(requestDto.getPw()))
                .profileImg(null)
                .self("자기소개를 등록해주세요")
                .build();
        memberRepository.save(member);
        snsRepository.save(SNS.builder()
                .blog("블로그를 등록해주세요")
                .insta("인스타를 등록해주세요")
                .youtube("유튜브를 등록해주세요")
                .facebook("페이스북을 등록해주세요")
                .member(member)
                .build());

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "회원가입 성공"), HttpStatus.OK);
    }

    //로그인
    @Transactional
    public ResponseEntity<PrivateResponseBody> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentEmail(requestDto.getEmail());

        // DB에 Email 확인
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_EMAIL_FAIL, null), HttpStatus.OK);
        }

        // DB에 PW 확인
        if (!member.validatePassword(passwordEncoder, requestDto.getPw())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_PASSWORD_FAIL, null), HttpStatus.OK);
        }

        //토큰 지급
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, MemberResponseDto.builder()
                        .email(member.getEmail())
                        .nickName(member.getNickName())
                        .build()), HttpStatus.OK);
    }

    //로그아웃
    public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {

        // 토큰 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_WRONG_FORM_JWT_TOKEN, null), HttpStatus.OK);
        }
        Member member = tokenProvider.getMemberFromAuthentication();

        // 회원 확인
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_EMAIL_FAIL, null), HttpStatus.OK);
        }

        tokenProvider.deleteRefreshToken(member);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "로그아웃"), HttpStatus.OK);
    }

    //Email 중복 확인
    public ResponseEntity<PrivateResponseBody> idcheck(IdCkeckRequestDto requestDto) {

        //email 체크
        if (null != isPresentEmail(requestDto.getEmail())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_EMAIL, null), HttpStatus.OK);
        }

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "Email 중복 확인 완료"), HttpStatus.OK);
    }

    //닉네임 중복 확인
    public ResponseEntity<PrivateResponseBody> nicknamecheck(NickNameCheckRequestDto requestDto) {

        //nickname 체크
        if (null != isPresentNickName(requestDto.getNickName())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_NICKNAME, null), HttpStatus.OK);
        }

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "NickName 중복 확인 완료"), HttpStatus.OK);
    }

    //Email 확인
    @Transactional(readOnly = true)
    public Member isPresentEmail(String email) {
        Optional<Member> optionalEmail = memberRepository.findByEmail(email);
        return optionalEmail.orElse(null);
    }

    //NickName 확인
    @Transactional(readOnly = true)
    public Member isPresentNickName(String nickname) {
        Optional<Member> optionalNickName = memberRepository.findByNickName(nickname);
        return optionalNickName.orElse(null);
    }

    //토큰 지급
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}
