package com.localgaji.taxi.auth;

import com.localgaji.taxi.__global__.utils.ApiUtil.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.localgaji.taxi.__global__.utils.ApiUtil.success;
import static com.localgaji.taxi.auth.dto.RequestAuth.*;

@RestController @RequiredArgsConstructor
@Tag(name = "로그인/회원가입", description = "로그인/회원가입 관련 API")
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login/oauth/kakao")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인")
    public ResponseEntity<Response<String>> postKakaoLogin(@RequestBody LoginRequest requestBody) {
        String jwt = loginService.kakaoLogin(requestBody);
        return buildLoginResponse(jwt);
    }

    @PostMapping("/signUp/oauth/kakao")
    @Operation(summary = "카카오 회원가입", description = "카카오 회원가입")
    public ResponseEntity<Response<String>> postKakaoSignUp(@RequestBody SignUpRequest requestBody) {
        String jwt = loginService.kakaoSignUp(requestBody);
        return buildLoginResponse(jwt);
    }

    private ResponseEntity<Response<String>> buildLoginResponse(String jwt) {
        return ResponseEntity.ok()
                .header("Authorization", jwt)
                .body(success(null));
    }
}
