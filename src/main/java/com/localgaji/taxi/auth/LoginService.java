package com.localgaji.taxi.auth;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.user.User;
import com.localgaji.taxi.user.UserService;
import com.localgaji.taxi.auth.kakao_oauth.KakaoOAuth;
import com.localgaji.taxi.auth.kakao_oauth.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.localgaji.taxi.auth.dto.RequestAuth.*;


@Slf4j @Service
@RequiredArgsConstructor
public class LoginService {

    private final UserService userService;
    private final KakaoOAuthService kakaoAuthService;
    private final JwtService jwtService;

    @Transactional(noRollbackFor = CustomException.class)
    public String kakaoLogin(LoginRequest requestBody) {
        KakaoOAuth kakaoOAuth = kakaoAuthService.findKakaoOAuthByCode(requestBody.code());
        User user = kakaoOAuth.getUser();
        return getJwt(user);
    }

    @Transactional
    public String kakaoSignUp(SignUpRequest requestBody) {
        User newUser = userService.makeNewUser(requestBody);
        kakaoAuthService.makeNewKakaoOAuth(requestBody.code(), newUser);
        return getJwt(newUser);
    }

    private String getJwt(User user) {
        return jwtService.createJwt(user.getUserId());
    }
}
