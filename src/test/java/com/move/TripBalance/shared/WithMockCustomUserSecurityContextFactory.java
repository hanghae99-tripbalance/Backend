package com.move.TripBalance.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
@RequiredArgsConstructor
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

//
//        final TokenDto tokenDto = tokenProvider.generateTokenDto(Member.builder().email(annotation.email()).nickName(annotation.nickName())
//                .pw(passwordEncoder.encode(annotation.pw())).build());

        final Authentication authentication = new TestingAuthenticationToken("test1234@test.com", "test1234!", "ROLE_USER");

        securityContext.setAuthentication(authentication);

        return securityContext;
    }

}