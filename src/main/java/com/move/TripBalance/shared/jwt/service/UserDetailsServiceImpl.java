package com.move.TripBalance.shared.jwt.service;

import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.shared.domain.UserDetailsImpl;

import com.move.TripBalance.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Can't find " + email));

    return new UserDetailsImpl(member);
  }
}
