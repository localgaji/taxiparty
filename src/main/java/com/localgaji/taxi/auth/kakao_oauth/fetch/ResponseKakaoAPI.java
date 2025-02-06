package com.localgaji.taxi.auth.kakao_oauth.fetch;

public class ResponseKakaoAPI {
    public record GetTokenResponse(
            String access_token
    ) {
    }

    public record GetKakaoIdResponse(
            String id
    ) {
    }
}
