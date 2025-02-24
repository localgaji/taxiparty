package com.localgaji.taxi.__global__.auth_global;

import com.localgaji.taxi.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // wrap 으로 message 를 감싸면 STOMP 의 헤더에 접근할 수 있다
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // stomp connect 요청일때 : jwt로 인증하기
        if ( isConnectRequest(accessor) ) {
            // Authorization 헤더 추출
            String authorization = getAuthHeaderByAccessor(accessor);

            // 헤더에서 user 객체 추출
            Long userId = jwtService.getUserIdByAuthHeader(authorization);

            accessor.setUser(new UserPrincipal(userId));
        }
        return message;
    }

    private boolean isConnectRequest(StompHeaderAccessor accessor) {
        return StompCommand.CONNECT.equals(accessor.getCommand());
    }

    private String getAuthHeaderByAccessor(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }
}
