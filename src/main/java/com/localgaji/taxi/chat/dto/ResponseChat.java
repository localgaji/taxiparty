package com.localgaji.taxi.chat.dto;

import com.localgaji.taxi.chat.Chat;
import com.localgaji.taxi.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseChat {
    public record GetChatListRes(
        List<ChatRes> chatList
    ) {
    }
    public record ChatRes(
            Long chatId,
            String userName,
            String message,
            LocalDateTime createdAt
    ) {
        public ChatRes(Chat chat, User user) {
            this(
                    chat.getId(),
                    user.getUserName(),
                    chat.getMessage(),
                    chat.getCreatedDate()
            );
        }
    }
}
