package com.move.TripBalance.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  @NotBlank
  @Pattern(regexp ="^[a-zA-Z0-9]+@[a-zA-Z]+.[a-z]+${4,12}$")
  private String email;

  @NotBlank
  private String nickName;

  @NotBlank
  private String pw;

  @NotBlank
  private String pwConfirm;
}
