package com.localgaji.taxi.__global__.auth_global;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import com.localgaji.taxi.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Parameter;


@Component @RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 로그인 필요 여부 체크
        if (!isLoginRequired(handlerMethod)) {
            return true;
        }

        // 토큰 유효하지 않을 경우 에러 발생
        Long userId = getUserIdByRequest(request);

        // userId를 arguments resolver 에 넘기기
        request.setAttribute("userId", userId);

        return true;
    }

    private boolean isLoginRequired(HandlerMethod handlerMethod) {
        for (Parameter parameter : handlerMethod.getMethod().getParameters()) {
            if (parameter.isAnnotationPresent(AuthUser.class)) {
                return true;
            }
        }
        return false;
    }

    private Long getUserIdByRequest(HttpServletRequest request) {
        // Authorization 헤더 값 가져오기
        String authorization = request.getHeader("Authorization");

        // 헤더 값이 없을 때 커스텀 에러 발생
        if (!isHeaderEmpty(authorization)) {
            throw new CustomException(ErrorType.TOKEN_NO);
        }

        // 토큰 검증 + 사용자 정보 추출 (토큰 무효할 경우 커스텀 에러 발생)
        Claims claims = jwtService.getClaimsByAuthorizationHeader(authorization);

        return jwtService.getUserIdByClaims(claims);
    }

    private boolean isHeaderEmpty(String authorizationHeader) {
        return authorizationHeader != null;
    }
}
