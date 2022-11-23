package com.move.TripBalance.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(HttpStatus.OK, "0", "응답이 정상 처리 되었습니다."),
    LOGIN_MEMBER_ID_FAIL(HttpStatus.NOT_FOUND, "110", "해당 하는 memberId 가 없습니다"),
    LOGIN_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "111", "Password가 틀렸습니다."),
    LOGIN_WRONG_SIGNATURE_JWT_TOKEN(HttpStatus.BAD_REQUEST, "112", "잘못된 JWT 서명입니다."),
    LOGIN_EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "113", "만료된 JWT 토큰입니다."),
    LOGIN_NOT_SUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "114", "지원되지 않는 JWT 토큰입니다."),
    LOGIN_WRONG_FORM_JWT_TOKEN(HttpStatus.BAD_REQUEST, "115", "JWT 토큰이 잘못되었습니다."),
    NOT_MATCH_POST(HttpStatus.INTERNAL_SERVER_ERROR, "116", "현재 로그인한 유저가 작성한 게시글이 아닙니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST,"117","중복된 Email이 있습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST,"118","중복된 NickName이 있습니다."),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST,"119","Password가 틀립니다."),
    NOT_FOUND(HttpStatus.BAD_REQUEST,"120","존재하지 않는 게시글 id 입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"121","작성자만 수정할 수 있습니다."),
    BAD_REQUEST_COMMENT(HttpStatus.BAD_REQUEST,"122","존재하지 않는 댓글 id 입니다."),
    BAD_REQUEST_RECOMMENT(HttpStatus.BAD_REQUEST,"123","존재하지 않는 대댓글 id 입니다."),
    INTERNAL_SERVER_ERROR_PLZ_CHECK(HttpStatus.INTERNAL_SERVER_ERROR, "999", "알수없는 서버 내부 에러 발생 , dladlsgur3334@gmail.com 으로 연락 부탁드립니다.");

    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    StatusCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}