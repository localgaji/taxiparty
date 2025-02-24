package com.localgaji.taxi.chat;

import com.localgaji.taxi.__global__.utils.ApiUtil;
import com.localgaji.taxi.chat.dto.RequestChat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.localgaji.taxi.__global__.utils.ApiUtil.success;

@RequiredArgsConstructor
@RestController
public class ChatSocketController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations operations; // 특정 Broker로 메세지를 전달

    @MessageMapping("/chat/{partyId}")  // "/pub/chat/{...} "
    public ResponseEntity<ApiUtil.Response<String>> sendMessage(@DestinationVariable Long partyId,
                                                                RequestChat.PostNewChatReq message,
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

}
