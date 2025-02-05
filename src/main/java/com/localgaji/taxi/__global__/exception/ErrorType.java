package com.localgaji.taxi.__global__.exception;

import lombok.Getter;

@Getter
public enum ErrorType {

    ETC_ERROR(400, -20000, "기타 에러"),

    TOKEN_NO(401, -10001, "토큰 없음"),
    TOKEN_INVALID(401, -10002, "유효하지 않은 토큰"),
    TOKEN_EXPIRED(401, -10003, "만료된 토큰"),
    OAUTH_FAIL(401, -10004, "oauth 인증 실패"),

    FORBIDDEN(403, -11000, "권한 없음 (jwt)"),
    FORBIDDEN_DATA(403, -11001, "권한 없음 (데이터 접근)"),

    NOT_FOUND(404, -12000, "없음"),
    MEMBER_NOT_FOUND(404, -12001, "유저 정보 없음"),

    ALREADY_HAVE(400, -20001, "이미 있음"),
    ALREADY_CLOSED(400, -20002, "이미 처리");

    private final int statusCode;
    private final int internalCode;
    private final String errorMessage;

    ErrorType(int statusCode, int internalCode, String errorMessage) {
        this.statusCode = statusCode;
        this.internalCode = internalCode;
        this.errorMessage = errorMessage;
    }
}