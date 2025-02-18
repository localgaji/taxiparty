package com.localgaji.taxi.party.dto;

import com.localgaji.taxi.party.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

import static com.localgaji.taxi.party.dto.LocationDTO.*;

public class RequestParty {
    @Schema(description = "팟 만들기")
    public record PostPartyReq(
            AddressDTO pickup,
            AddressDTO dropoff,
            LocalDateTime pickupTime,
            String description,
            Integer maxHeadcount
    ) {
        public Party toEntity(Point pickupPoint, Point dropoffPoint) {
            return Party.builder()
                    .description(this.description)
                    .pickupTime(this.pickupTime)
                    .maxHeadcount(this.maxHeadcount)
                    .pickupAddress(this.pickup.toEntity())
                    .dropoffAddress(this.dropoff.toEntity())
                    .pickupPoint(pickupPoint)
                    .dropoffPoint(dropoffPoint)
                .build();
        }

    }

    @Schema(description = "요금 등록")
    public record AddFareReq(
            Integer fare
    ) {
    }

    @Schema(description = "조건에 맞는 파티 리스트 검색")
    public record GetPartiesSearchReq(
            CoordinateDTO departure,
            CoordinateDTO dropoff,
            LocalDateTime pickupTime
    ) {
    }

}
