package com.localgaji.taxi.auth.kakao_oauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.localgaji.taxi.auth.kakao_oauth.fetch.RequestKakaoAPI.*;
import static com.localgaji.taxi.auth.kakao_oauth.fetch.ResponseKakaoAPI.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "카카오 oauth 서버 모킹", description = "")
@RequestMapping("/mock")
public class MockKakaoController {
    @PostMapping("/oauth/token")
    @Operation(summary = "토큰발급")
    public ResponseEntity<GetTokenResponse> getKakaoToken(GetTokenRequest request) {
        String token = request.getCode();
        GetTokenResponse response = new GetTokenResponse(token);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/v2/user/me")
    @Operation(summary = "아이디 조회")
    public ResponseEntity<GetKakaoIdResponse> getKakaoId(@RequestHeader("Authorization") String token) {
        System.out.println("token = " + token);
        String kakaoId = kakaoId(token);
        System.out.println("kakaoId = " + kakaoId);

        GetKakaoIdResponse response = new GetKakaoIdResponse(String.valueOf(kakaoId));

        return ResponseEntity.ok().body(response);
    }

    private String kakaoId(String token) {
        token = token.substring("Bearer ".length());

        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) {
                return "1";
            }
        }
        return token;
    }
}

