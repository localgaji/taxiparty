package com.localgaji.taxi.party;

import lombok.Getter;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface PartyRepositoryCustom {

    Slice<PartyWithDistanceDTO> findNearestParties(Point departure,
                                                   int pickupRadius,
                                                   Point dropoff,
                                                   int dropoffRadius,
                                                   LocalDateTime pickupTime,
                                                   int timeRange,
                                                   Pageable pageable
    );

    @Getter
    class PartyWithDistanceDTO {
        Party party;
        Float distance;
    }
}
