package com.localgaji.taxi.chat;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.localgaji.taxi.__global__.utils.ApiUtil.*;
import static com.localgaji.taxi.chat.dto.RequestChat.*;
import static com.localgaji.taxi.chat.dto.ResponseChat.*;

@RequiredArgsConstructor @RestController
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations operations; // 특정 Broker로 메세지를 전달

    @MessageMapping("/chat/{partyId}")  // "/pub/chat/{...} "
    public ResponseEntity<Response<String>> sendMessage(@DestinationVariable Long partyId,
                                                        PostNewChatReq message,
                                                        Principal principal){
        // 구독자들에게 채팅 보내기
        String path = "/sub/chat/" + partyId;
        operations.convertAndSend(path, message);

        // 채팅 저장하기
        chatService.saveNewChat(
                Long.parseLong( principal.getName() ),
                partyId, message);

        return ResponseEntity.ok().body(success(null));
    }

    @GetMapping("/party/{party_id}/chatList")
    public ResponseEntity<Response<GetChatListRes>> getChatList(@AuthUser User user,
                                                                @PathVariable Long partyId,
                                                                @RequestParam(required = false) Long cursor,
                                                                @RequestParam(defaultValue = "20") int amount) {
        GetChatListRes responseBody = chatService.getChatList(user, partyId, amount, cursor);
        return ResponseEntity.ok().body(success(responseBody));
    }

}
