package com.localgaji.taxi.user.user_dto;

import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class ResponseUser {
    @Getter
    @Schema(description = "내 정보 조회")
    public static class GetMyInfoResponse {
        @Schema(description = "회원 이름")
        String userName;

        @Schema(description = "이메일")
        String email;

        public GetMyInfoResponse(User user) {
            this.userName = user.getUserName();
        }
    }
}
