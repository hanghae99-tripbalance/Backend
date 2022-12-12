package com.move.TripBalance.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.member.domain.SNS;
import com.move.TripBalance.member.controller.response.MemberResponseDto;
import com.move.TripBalance.member.repository.MemberRepository;
import com.move.TripBalance.member.repository.SNSRepository;
import com.move.TripBalance.oauth.domain.KakaoMemberInfoDto;
import com.move.TripBalance.shared.Authority;
import com.move.TripBalance.shared.domain.UserDetailsImpl;
import com.move.TripBalance.shared.exception.PrivateResponseBody;
import com.move.TripBalance.shared.exception.StatusCode;
import com.move.TripBalance.shared.jwt.TokenProvider;
import com.move.TripBalance.shared.jwt.controller.request.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class KakaoMemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SNSRepository snsRepository;
    private final TokenProvider tokenProvider;

    // 카카오 로그인 restApi Key
    @Value(value = "${oauth.kakao.api}")
    String restApi;

    // 카카오 로그인 리다이렉트 URI
    @Value(value = "${oauth.kakao.redirect}")
    String redirectUri;

    // 카카오 로그인 과정
    public ResponseEntity<PrivateResponseBody> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);

        // 3. 필요시에 회원가입
        Member kakaoMember = registerKakaoUserIfNeeded(kakaoMemberInfo);

        // 4. 강제 로그인 처리
        forceLogin(kakaoMember, response);

        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, MemberResponseDto.builder()
                        .email(kakaoMember.getEmail())
                        .nickName(kakaoMember.getNickName())
                        .build()), HttpStatus.OK);
    }

    // 액세스 토큰 요청
    private String getAccessToken(String code) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", restApi);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 토큰으로 카카오 API 호출
    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();

        // 카카오 계정 멤버 닉네임
        String nickName = jsonNode.get("properties")
                .get("nickname").asText();

        // 카카오 계정 메일
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        // 카카오 계정 프로필 사진
        String profileImg = jsonNode.get("properties")
                .get("profile_image").asText();


        return new KakaoMemberInfoDto(id, nickName, email, profileImg);
    }

    // 필요시에 회원가입 처리 혹은 카카오 아이디 할당
    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {

        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoMemberInfo.getId();
        Member kakaoMember = memberRepository.findByKakaoId(kakaoId)
                .orElse(null);

        if (kakaoMember == null) {

            // 카카오 사용자 이메일과 동일한 이메일을 가진 회원이 있는지 확인
            String kakaoEmail = kakaoMemberInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                kakaoMember = sameEmailUser;

                // 기존 회원정보에 카카오 Id 추가
                kakaoMember.setKakaoId(kakaoId);

            } else {

                // 신규 회원가입

                // nickName: kakao nickname
                String nickname = kakaoMemberInfo.getNickName();

                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoMemberInfo.getEmail();

                // role: 일반 사용자
                Authority role = Authority.MEMBER;

                // profileImg: 프로필 이미지
                String profileImg = kakaoMemberInfo.getProfileImg();

                // 카카오 로그인 멤버 정보 빌드
                kakaoMember = Member.builder()
                        .email(email)
                        .nickName(nickname)
                        .pw(encodedPassword)
                        .profileImg(profileImg)
                        .self("자기소개를 등록해주세요")
                        .role(role)
                        .build();
                memberRepository.save(kakaoMember);
                // 카카오 로그인 멤버 sns 정보 생성
                snsRepository.save(SNS.builder()
                        .blog("블로그를 등록해주세요")
                        .insta("인스타를 등록해주세요")
                        .youtube("유튜브를 등록해주세요")
                        .facebook("페이스북을 등록해주세요")
                        .member(kakaoMember)
                        .build());
            }
        }
        return kakaoMember;
    }
    // 카카오 로그인 처리
    private ResponseEntity<PrivateResponseBody> forceLogin(Member kakaoMember, HttpServletResponse response) {

        UserDetails userDetails = new UserDetailsImpl(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = tokenProvider.generateTokenDto(kakaoMember);
        tokenToHeaders(tokenDto, response);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, MemberResponseDto.builder()
                        .email(kakaoMember.getEmail())
                        .nickName(kakaoMember.getNickName())
                        .build()), HttpStatus.OK);
    }

    //토큰 지급
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}