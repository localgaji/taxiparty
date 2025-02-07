package com.localgaji.taxi.auth.dto;
import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

public class RequestAuth {
    @Schema(description = "OAuth 로그인")
    public record LoginRequest(
        @Schema(description = "OAuth 인가코드")
        String code
    ) {
    }

    @Schema(description = "OAuth 회원가입")
    public record SignUpRequest(
        @Schema(description = "회원 이름")
        String userName,
        @Schema(description = "OAuth 인가코드")
        String code
    ) {
        public User toEntity() {
            return User.builder()
                    .userName(userName)
                    .build();
        }
    }

}
