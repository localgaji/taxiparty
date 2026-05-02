package com.localgaji.taxi.party;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PartyRepositoryCustomImpl implements PartyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PartyWithDistanceDTO> findNearestParties(Point departure,
                                                          int pickupRadius,
                                                          Point dropoff,
                                                          int dropoffRadius,
                                                          LocalDateTime pickupTime,
                                                          int timeRange,
                                                          Pageable pageable) {

        QParty party = QParty.party;

        // 거리 계산을 위한 Expression
        NumberExpression<Double> distanceExpression =
                Expressions.numberTemplate(Double.class,
                        "ST_Distance_Sphere({0}, {1})",
                        party.pickupPoint, departure);

        // 시간 차이 계산을 위한 Expression
        NumberExpression<Integer> timeDiffExpression =
                Expressions.numberTemplate(Integer.class,
                        "ABS(TIMESTAMPDIFF(SECOND, {0}, {1}))",
                        party.pickupTime, pickupTime);

        // 공간 함수 Expression들
        BooleanExpression pickupContains =
                Expressions.booleanTemplate(
                        "ST_Contains(ST_Buffer({0}, {1}), {2})",
                        departure, pickupRadius, party.pickupPoint);

        BooleanExpression dropoffContains =
                Expressions.booleanTemplate(
                        "ST_Contains(ST_Buffer({0}, {1}), {2})",
                        dropoff, dropoffRadius, party.dropoffPoint);

        // 메인 쿼리
        List<PartyWithDistanceDTO> content = queryFactory
                .select(Projections.constructor(PartyWithDistanceDTO.class,
                        party,
                        distanceExpression))
                .from(party)
                .join(party.pickupAddress).fetchJoin()
                .join(party.dropoffAddress).fetchJoin()
                .where(
                        pickupContains,
                        dropoffContains,
                        party.status.eq(PartyStatus.ACTIVE),
                        timeDiffExpression.loe(timeRange)
                )
                .orderBy(distanceExpression.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // hasNext 확인을 위해 +1
                .fetch();

        // Slice 생성
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
