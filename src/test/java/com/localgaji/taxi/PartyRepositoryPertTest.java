package com.localgaji.taxi;

import com.localgaji.taxi.party.PartyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest
class PartyRepositoryPerfTest {

    @Autowired
    private PartyRepository partyRepository;

    // ── 변경 파라미터 (조합) ────────────────────────────────
    private static final int[] PICKUP_RADII = {500, 1000, 2000, 5000};
    private static final int[] TIME_RANGES  = {5, 10, 15, 20};

    // ── 테스트 실행 설정 ───────────────────────────────────────────
    private static final int WARMUP_COUNT  = 3;   // 워밍업 반복 횟수
    private static final int MEASURE_COUNT = 10;  // 조합별 측정 반복 횟수
    private static final Pageable PAGEABLE = PageRequest.of(0, 20);

    // ── 좌표계 설정 ────────────────────────────────────────────────
    // SRID=4326 : WGS84 위경도 좌표계
    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    // ── 랜덤 좌표 생성 범위 (고정) ────────────────────────────────
    // departure 위도 범위
    private static final double DEP_LAT_MIN  = 37.4,  DEP_LAT_MAX  = 37.6;
    // departure 경도 범위
    private static final double DEP_LON_MIN  = 126.7, DEP_LON_MAX  = 127.1;
    // dropoff 위도 범위 (departure와 동일 범위 사용, 필요시 변경)
    private static final double DROP_LAT_MIN = 37.4,  DROP_LAT_MAX = 37.6;
    // dropoff 경도 범위
    private static final double DROP_LON_MIN = 126.7, DROP_LON_MAX = 127.1;

    // ── 랜덤 pickupTime 생성 기준 ─────────────────────────────────
    // 기준 시각 2026-01-01 00:00 에서 최대 +4시간 +60분 이내 랜덤
    private static final LocalDateTime PICKUP_TIME_BASE =
            LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final int RANDOM_HOURS_RANGE   = 4;  // 0 ~ 4시간
    private static final int RANDOM_MINUTES_RANGE = 60; // 0 ~ 60분

    // ── 고정 파라미터 ──────────────────────────────────────────────
    private static final int DROPOFF_RADIUS = 100_000; // 도착지 반경 100km (고정)

    // ── 결과 저장용 ────────────────────────────────────────────────
    private final Map<String, double[]> results = new LinkedHashMap<>();

    // ──────────────────────────────────────────────────────────────
    @Test
    @DisplayName("pickupRadius × timeRange 조합 — JPQL vs NativeQuery 평균 응답시간 비교")
    void compareAllCombinations() {

        // 1) 워밍업
        //    JVM JIT 컴파일 및 DB 커넥션 풀 초기화 노이즈 제거 목적
        //    워밍업에도 랜덤 좌표/시간을 적용해 실측 환경과 동일하게 맞춤
        System.out.println("▶ 워밍업 시작 (" + WARMUP_COUNT + "회 × 2 메서드)");
        for (int i = 0; i < WARMUP_COUNT; i++) {
            runQuery(QueryType.JPQL,   500, 10);
            runQuery(QueryType.NATIVE, 500, 10);
        }
        System.out.println("▶ 워밍업 완료\n");

        // 2) 조합 순회
        for (int pickupRadius : PICKUP_RADII) {
            for (int timeRange : TIME_RANGES) {

                String label = String.format("pickupRadius=%5dm / timeRange=%2dmin",
                        pickupRadius, timeRange);

                double jpqlAvgMs   = measureAvgMs(QueryType.JPQL,   pickupRadius, timeRange);
                double nativeAvgMs = measureAvgMs(QueryType.NATIVE, pickupRadius, timeRange);

                results.put(label, new double[]{jpqlAvgMs, nativeAvgMs});

                // 조합별 중간 결과 즉시 출력
                System.out.printf("  %-45s | JPQL: %7.2f ms | Native: %7.2f ms%n",
                        label, jpqlAvgMs, nativeAvgMs);
            }
        }

        // 3) 최종 결과 테이블 출력
        printResultTable();
    }

    /**
     * 특정 조합에 대해 {@code MEASURE_COUNT}번 쿼리를 실행한 뒤
     * 평균 응답시간(ms)을 반환한다.
     * 매 반복마다 departure / dropoff / pickupTime 을 새로 랜덤 생성해서
     * 특정 좌표가 캐싱되는 것을 방지한다.
     */
    private double measureAvgMs(QueryType type, int pickupRadius, int timeRange) {
        long totalMs = 0;

        for (int i = 0; i < MEASURE_COUNT; i++) {
            long start = System.currentTimeMillis();  // ms 단위 측정 시작
            runQuery(type, pickupRadius, timeRange);  // 실제 쿼리 실행
            totalMs += System.currentTimeMillis() - start;  // ms 단위 누적
        }

        return totalMs / (double) MEASURE_COUNT;  // 평균 ms 반환
    }

    /**
     * 쿼리 타입에 따라 실제 repository 메서드를 호출한다.
     * 호출 시점마다 랜덤 파라미터(좌표, 시간)를 새로 생성한다.
     */
    private void runQuery(QueryType type, int pickupRadius, int timeRange) {
        // ── 매 호출마다 랜덤 파라미터 생성 ──────────────────────────

        // departure : 위도 37.4~37.6 / 경도 126.7~127.1 범위에서 랜덤 (소수점 5자리)
        Point departure = createRandomPoint(
                DEP_LAT_MIN, DEP_LAT_MAX,
                DEP_LON_MIN, DEP_LON_MAX);

        // dropoff : 동일 범위에서 departure와 독립적으로 랜덤 생성
        Point dropoff = createRandomPoint(
                DROP_LAT_MIN, DROP_LAT_MAX,
                DROP_LON_MIN, DROP_LON_MAX);

        // pickupTime : 2026-01-01 00:00 기준으로 0~4시간 + 0~60분 랜덤 가산
        LocalDateTime pickupTime = generateRandomPickupTime();

        switch (type) {
            case JPQL -> partyRepository.findNearestParties(
                    departure, pickupRadius,
                    dropoff,   DROPOFF_RADIUS,
                    pickupTime, timeRange,
                    PAGEABLE);
            case NATIVE -> partyRepository.findNearestPartiesForceIndex(
                    departure, pickupRadius,
                    dropoff,   DROPOFF_RADIUS,
                    pickupTime, timeRange,
                    PAGEABLE);
        }
    }

    // ── 랜덤 값 생성 헬퍼 메서드 ──────────────────────────────────

    /**
     * 위도/경도 범위를 받아 랜덤 JTS Point 를 생성한다.
     * BigDecimal 로 소수점 5자리에서 반올림해 Groovy 코드와 동일한 정밀도를 맞춤.
     * JTS Coordinate 는 (경도, 위도) 순서 = (x, y) 임에 주의.
     */
    private Point createRandomPoint(double latMin, double latMax,
                                    double lonMin, double lonMax) {
        double lat = randomCoordinate(latMin, latMax);
        double lon = randomCoordinate(lonMin, lonMax);
        // JTS : Coordinate(x=경도, y=위도)
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lon, lat));
    }

    /**
     * min~max 범위의 랜덤 실수를 소수점 5자리로 반올림해 반환한다.
     * Groovy의 BigDecimal.setScale(5, ROUND_HALF_UP) 과 동일한 동작.
     */
    private double randomCoordinate(double min, double max) {
        double raw = min + (max - min) * Math.random();
        return BigDecimal.valueOf(raw)
                .setScale(5, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 2026-01-01 00:00 기준으로 0~4시간, 0~60분을 랜덤 가산한 LocalDateTime 을 반환한다.
     * Groovy 코드의 randomHours / randomMinutes 로직과 동일.
     */
    private LocalDateTime generateRandomPickupTime() {
        long randomHours   = (long) (Math.random() * RANDOM_HOURS_RANGE);
        long randomMinutes = (long) (Math.random() * RANDOM_MINUTES_RANGE);
        return PICKUP_TIME_BASE
                .plusHours(randomHours)
                .plusMinutes(randomMinutes);
    }

    // ── 결과 출력 ─────────────────────────────────────────────────

    /**
     * 전체 측정 결과를 표 형태로 출력한다.
     */
    private void printResultTable() {
        String separator = "─".repeat(82);

        System.out.println("\n\n" + separator);
        System.out.printf("%-45s | %-12s | %-12s | %s%n",
                "조합", "JPQL (ms)", "Native (ms)", "빠른 쪽");
        System.out.println(separator);

        for (Map.Entry<String, double[]> entry : results.entrySet()) {
            double queryA    = entry.getValue()[0];
            double queryB = entry.getValue()[1];

            String faster = queryA < queryB
                    ? String.format("★ JPQL   (%.1fx)", queryB / queryA)
                    : String.format("★ Native (%.1fx)", queryA / queryB);

            System.out.printf("%-45s | %10.2f   | %10.2f   | %s%n",
                    entry.getKey(), queryA, queryB, faster);
        }

        System.out.println(separator);
        System.out.printf("  측정 조건: 조합당 %d회 반복 (평균), 워밍업 %d회 / 단위: ms%n",
                MEASURE_COUNT, WARMUP_COUNT);
    }

    enum QueryType { JPQL, NATIVE }
}