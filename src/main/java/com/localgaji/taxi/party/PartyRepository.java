package com.localgaji.taxi.party;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

    /** 출발지 반경 (m), 도착지 반경 (m), 시간 범위 (s) 이내의 party list */
    @Query(value = """
        SELECT *, ST_Distance_Sphere(p.pickup_point, :departure) AS distance
        FROM party p
        WHERE ST_Contains( ST_Buffer(:departure, :pickupRadius  / 111000), p.pickup_point  )
          AND ST_Contains( ST_Buffer(:dropoff,   :dropoffRadius / 111000), p.dropoff_point )
          AND p.status = 'ACTIVE'
          AND ABS( TIMESTAMPDIFF(SECOND, p.pickup_time, :pickupTime) ) <= :timeRange
        ORDER BY distance ASC
        """, nativeQuery = true)
    Slice<Party> findNearestParties(@Param("departure") Point departure,
                                    @Param("pickupRadius") int pickupRadius,
                                    @Param("dropoff") Point dropoff,
                                    @Param("dropoffRadius") int dropoffRadius,
                                    @Param("pickupTime") LocalDateTime pickupTime,
                                    @Param("timeRange") int timeRange,
                                    Pageable pageable);
}
