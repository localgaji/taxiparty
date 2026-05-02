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
    @Query("""
        SELECT
            p AS party,
            ST_Distance_Sphere(p.pickupPoint, :departure) AS distance
        FROM Party p
        JOIN FETCH p.pickupAddress pa
        JOIN FETCH p.dropoffAddress da
        WHERE ST_Contains( ST_Buffer(:departure, :pickupRadius), p.pickupPoint )
            AND ST_Contains( ST_Buffer(:dropoff, :dropoffRadius), p.dropoffPoint )
            AND p.status = 'ACTIVE'
            AND ABS( TIMESTAMPDIFF(MINUTE, p.pickupTime, :pickupTime) ) <= :timeRange
        ORDER BY distance ASC
    """)
    Slice<PartyWithDistance> findNearestParties(@Param("departure") Point departure,
                                                @Param("pickupRadius") int pickupRadius,
                                                @Param("dropoff") Point dropoff,
                                                @Param("dropoffRadius") int dropoffRadius,
                                                @Param("pickupTime") LocalDateTime pickupTime,
                                                @Param("timeRange") int timeRange,
                                                Pageable pageable);

    interface PartyWithDistance {
        Party getParty();
        Float getDistance();
    }

    /** 출발지 반경 (m), 도착지 반경 (m), 시간 범위 (s) 이내의 party list */
    @Query(value = """
    SELECT
        p.id, p.pickup_time, p.description,
        pa.place_name,
        da.place_name,
        ST_Distance_Sphere(p.pickup_point, :departure) AS distance
    FROM party p FORCE INDEX (time_status_idx)
    INNER JOIN address pa ON p.pickup_address_id = pa.id
    INNER JOIN address da ON p.dropoff_address_id = da.id
    WHERE ST_Contains(ST_Buffer(:departure, :pickupRadius), p.pickup_point)
      AND ST_Contains(ST_Buffer(:dropoff, :dropoffRadius), p.dropoff_point)
      AND (SELECT COUNT(*) FROM passenger ps
           WHERE ps.party_id = p.id AND ps.status = 'ACTIVE') < p.max_headcount
      AND p.status = 'ACTIVE'
    AND p.pickup_time BETWEEN
        DATE_SUB(:pickupTime, INTERVAL :timeRange MINUTE) AND
        DATE_ADD(:pickupTime, INTERVAL :timeRange MINUTE)
    ORDER BY distance ASC
    """, nativeQuery = true)
    Slice<PartyNecessaryColumns> findNearestPartiesForceIndex(@Param("departure") Point departure,
                                                @Param("pickupRadius") int pickupRadius,
                                                @Param("dropoff") Point dropoff,
                                                @Param("dropoffRadius") int dropoffRadius,
                                                @Param("pickupTime") LocalDateTime pickupTime,
                                                @Param("timeRange") int timeRange,
                                                Pageable pageable);
    /** 출발지 반경 (m), 도착지 반경 (m), 시간 범위 (s) 이내의 party list */
    @Query(value = """
    WITH filtered_parties AS (
      SELECT
        p.*
      FROM party p
      WHERE p.status = 'ACTIVE'
        AND p.pickup_time BETWEEN
            DATE_SUB(:pickupTime, INTERVAL :timeRange MINUTE) AND
            DATE_ADD(:pickupTime, INTERVAL :timeRange MINUTE)
    )
    SELECT
        p.id, p.pickup_time, p.description,
        pa.place_name,
        da.place_name,
        ST_Distance_Sphere(p.pickup_point, :departure) AS distance
    FROM filtered_parties p
    INNER JOIN address pa ON p.pickup_address_id = pa.id
    INNER JOIN address da ON p.dropoff_address_id = da.id
    WHERE ST_Contains(ST_Buffer(:departure, :pickupRadius), p.pickup_point)
      AND ST_Contains(ST_Buffer(:dropoff, :dropoffRadius), p.dropoff_point)
      AND (SELECT COUNT(*) FROM passenger ps
           WHERE ps.party_id = p.id AND ps.status = 'ACTIVE') < p.max_headcount
    ORDER BY distance ASC
    """, nativeQuery = true)
    Slice<PartyNecessaryColumns> findNearestPartiesWithCTE(@Param("departure") Point departure,
                                                           @Param("pickupRadius") int pickupRadius,
                                                           @Param("dropoff") Point dropoff,
                                                           @Param("dropoffRadius") int dropoffRadius,
                                                           @Param("pickupTime") LocalDateTime pickupTime,
                                                           @Param("timeRange") int timeRange,
                                                           Pageable pageable);

    interface PartyNecessaryColumns {
        Long getId();
        LocalDateTime getPickupTime();
        String getDescription();
        String getPickupPlaceName();
        String getDropoffPlaceName();
        Float getDistance();
    }

}
