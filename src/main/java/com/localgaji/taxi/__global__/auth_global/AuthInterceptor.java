package com.localgaji.taxi.__global__.auth_global;

import com.localgaji.taxi.auth.JwtService;
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

        // 헤더 > jwt > userId 찾아서 arguments resolver 에 넘겨주기
        forwardUserIdToResolver(request);

        return true;
    }

    private void forwardUserIdToResolver(HttpServletRequest request) {
        // 요청에서 authorization 헤더 뽑기
        String authorization = getAuthHeaderByRequest(request);

        // authorization 헤더에서 userId 뽑기 (토큰 유효하지 않을 경우 에러 발생)
        Long userId = jwtService.getUserIdByAuthHeader(authorization);

        // userId를 arguments resolver 에 넘기기
        request.setAttribute("userId", userId);
    }

    private boolean isLoginRequired(HandlerMethod handlerMethod) {
        for (Parameter parameter : handlerMethod.getMethod().getParameters()) {
            if (parameter.isAnnotationPresent(AuthUser.class)) {
                return true;
            }
        }
        return false;
    }

    private String getAuthHeaderByRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
