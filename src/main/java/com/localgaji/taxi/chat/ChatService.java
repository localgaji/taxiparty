package com.localgaji.taxi.chat;

import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.party.UtilPartyService;
import com.localgaji.taxi.user.User;
import com.localgaji.taxi.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.localgaji.taxi.chat.dto.RequestChat.*;
import static com.localgaji.taxi.chat.dto.ResponseChat.*;

@Service @RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;
    private final UtilPartyService utilPartyService;

    /** 채팅 저장하기 */
    @Transactional
    public void saveNewChat(Long userId, Long partyId, PostNewChatReq requestBody) {
        User user = userService.findUserById(userId);
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 권한 체크
        utilPartyService.checkUserInPartyOrThrow(user, party);

        Chat chat = Chat.builder()
                .user(user)
                .party(party)
                .message(requestBody.message())
                .build();
        chatRepository.save(chat);
        chat.addChat();
    }

    /** 파티의 채팅 리스트 (스크롤 페이징) */
    public GetChatListRes getChatList(User user, Long partyId, int amount, Long cursor) {
        Party party = utilPartyService.findPartyByIdOr404(partyId);

        // 권한 체크
        utilPartyService.checkUserInPartyOrThrow(user, party);

        // 커서 페이징으로 chatList 가져오기 -> 매핑
        List<ChatRes> chatList = chatRepository.findChatListByPartyWithCursor(partyId, amount, cursor).stream()
                .map(dto ->
                        new ChatRes( dto.chat(), dto.user() )
                ).toList();

        return new GetChatListRes(chatList);
    }

}
