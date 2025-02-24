package com.localgaji.taxi.chat;

import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.user.User;

import java.util.List;

public interface ChatRepositoryCustom {

    List<PartyAndChat> findPartiesAndLatestChatByUser(User user);
    List<ChatAndUser> findChatListByPartyWithCursor(Long partyId, int amount, Long lastReadChatId);

    record PartyAndChat(
            Party party,
            Chat latestChat
    ) {
    }

    record ChatAndUser(
            Chat chat,
            User user
    ) {
    }
}
