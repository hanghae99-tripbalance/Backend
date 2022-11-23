package com.move.TripBalance.shared;

public enum Authority {
  MEMBER(Role.MEMBER), // 회원가입 한 멤버 권한
  GUEST(Role.GUEST),  // 회원가입 하지 않은 손님 권한
  ADMIN(Role.ADMIN); // 관리자 권한

  private final String role;

  Authority(String role) { this.role = role; }

  public String getRole() { return this.role; }

  public static class Role {

    public static final String MEMBER = "ROLE_MEMBER";

    public static final String GUEST = "ROLE_GUEST";

    public static final String ADMIN = "ROLE_ADMIN";
  }

}
