package com.localgaji.taxi.chat;

import com.localgaji.taxi.__global__.auth_global.AuthUser;
import com.localgaji.taxi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.localgaji.taxi.__global__.utils.ApiUtil.*;
import static com.localgaji.taxi.chat.dto.ResponseChat.*;

@RequiredArgsConstructor @RestController
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/party/{party_id}/chatList")
    public ResponseEntity<Response<GetChatListRes>> getChatList(@AuthUser User user,
                                                                @PathVariable Long partyId,
                                                                @RequestParam(required = false) Long cursor,
                                                                @RequestParam(defaultValue = "20") int amount) {
        GetChatListRes responseBody = chatService.getChatList(user, partyId, amount, cursor);
        return ResponseEntity.ok().body(success(responseBody));
    }

}
