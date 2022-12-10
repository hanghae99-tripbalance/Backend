package com.move.TripBalance.shared;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class) // 스프링 시큐리티 테스트용 SecurityContext 를 만듦
public @interface WithMockCustomUser {

}