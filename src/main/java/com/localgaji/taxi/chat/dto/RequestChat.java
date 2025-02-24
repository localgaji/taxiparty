package com.localgaji.taxi.chat.dto;

public class RequestChat {
    public record PostNewChatReq(
            String message
    ) {
    }
}
