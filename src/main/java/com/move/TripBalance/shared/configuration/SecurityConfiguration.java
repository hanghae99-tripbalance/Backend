package com.move.TripBalance.shared.configuration;


import com.move.TripBalance.shared.jwt.AccessDeniedHandlerException;
import com.move.TripBalance.shared.jwt.AuthenticationEntryPointException;
import com.move.TripBalance.shared.jwt.TokenProvider;
import com.move.TripBalance.shared.jwt.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

  @Value("${jwt.secret}")
  String SECRET_KEY;
  private final TokenProvider tokenProvider;
  private final UserDetailsServiceImpl userDetailsService;
  private final AuthenticationEntryPointException authenticationEntryPointException;
  private final AccessDeniedHandlerException accessDeniedHandlerException;
  private final CorsConfig corsConfig;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors();

    http.csrf().disable()

        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPointException)
        .accessDeniedHandler(accessDeniedHandlerException)

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .antMatchers(
                "/tb/signup/**",//회원가입
                "/tb/login",//로그인
                "/tb/ouath/kakao", // 카카오 로그인
                "/tb/ouath/kakao?**",
                "/tb/posts/**",// 포스트 조회
                "/tb/posts/{postId}",
                "/tb/besttrip", // 메인페이지
                "/tb/apimap/**",
                "/tb/apimap",
                "/tb/localpost/**",
                "/tb/member/answer", // 밸런스 게임
                "/tb/game/**",
                "/tb/hotel/**",
                "/tb/blog/**",
                "/tb/comments/**", // 댓글
                "/tb/memberinfo/**", // 회원 정보
                "/tb/mypage/totaldb",
                "/tb/mypage/totalten"
        ).permitAll()
            .antMatchers(HttpMethod.GET,"/tb/posts").permitAll()
        .antMatchers( "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui/**").permitAll()
        .anyRequest().authenticated()

        .and()
        .addFilter(corsConfig.corsFilter())
        .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

    return http.build();
  }
}
