package com.localgaji.taxi.chat;

import com.localgaji.taxi.party.QParty;
import com.localgaji.taxi.party.passenger.PassengerStatus;
import com.localgaji.taxi.party.passenger.QPassenger;
import com.localgaji.taxi.user.QUser;
import com.localgaji.taxi.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository @RequiredArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    /** 커서 페이징으로 채팅 목록 가져오기 */
    @Override
    public List<ChatAndUser> findChatListByPartyWithCursor(Long partyId, int amount, Long lastReadChatId) {
        QChat chat = QChat.chat;
        QUser user = QUser.user;

        List<Tuple> query = queryFactory
                .select(chat, user)
                .from(chat)
                .join(chat.user, user)
                .where(
                        chat.party.id.eq(partyId)
                                .and( lastReadChatId != null ? chat.id.lt(lastReadChatId) : null )
                )
                .orderBy(chat.id.desc())
                .limit(amount)
                .fetch();

        return query.stream()
                .map(tuple ->
                        new ChatAndUser( tuple.get(chat), tuple.get(user) )
                ).toList();
    }

    /** 사용자가 속한 파티 목록과 각 파티의 최신 채팅을 가져오기  */
    @Override
    public List<PartyAndChat> findPartiesAndLatestChatByUser(User user) {
        QPassenger passenger = QPassenger.passenger;
        QParty party = QParty.party;
        QChat chat = QChat.chat;

        QChat latestChat = new QChat("latestChat");
        JPQLSubQuery<Long> latestChatSubQuery = JPAExpressions
                .select(latestChat.id.max())
                .from(latestChat)
                .where(latestChat.party.id.eq(party.id));

        List<Tuple> query = queryFactory
                .select(party, chat)
                .from(passenger)
                .join(passenger.party, party)
                .on( passenger.status.eq(PassengerStatus.ACTIVE) )
                .leftJoin(chat)
                .on( chat.id.eq(latestChatSubQuery) )
                .where( passenger.user.userId.eq(user.getUserId()) )
                .fetch();

        return query.stream()
                .map(tuple ->
                        new PartyAndChat( tuple.get(party), tuple.get(chat) )
                ).toList();
    }
}

