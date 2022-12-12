package com.move.TripBalance.shared.jwt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.move.TripBalance.member.domain.Member;
import com.move.TripBalance.shared.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken extends Timestamped {

  @Id
  @Column(nullable = false)
  private Long id;

  @JoinColumn(name = "memberId", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private Member member;

  @Column(nullable = false)
  private String value;

  public void updateValue(String token) {
    this.value = token;
  }
}
