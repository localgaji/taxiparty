package com.localgaji.taxi.party.dto;

import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.address.dto.PlaceDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class RequestParty {
    @Schema(description = "팟 만들기")
    public record PostPartyReq(
            PlaceDTO pickup,
            PlaceDTO dropoff,
            LocalDateTime pickupTime,
            String description,
            Integer maxHeadcount
    ) {
        public Party toEntity() {
            return Party.builder()
                    .description(this.description)
                    .pickupTime(this.pickupTime)
                    .maxHeadcount(this.maxHeadcount)
                    .pickupAddress(this.pickup.toEntity())
                    .dropoffAddress(this.dropoff.toEntity())
                .build();
        }

    }

    @Schema(description = "요금 등록")
    public record AddFareReq(
            Integer fare
    ) {
    }

    @Schema(description = "조건에 맞는 파티 리스트 검색")
    public record getPartyListReq(
            CoordinateDTO departure,
            CoordinateDTO dropoff,
            LocalDateTime pickupTime
    ) {
    }

    public record CoordinateDTO(
            Double x,
            Double y
    ) {
    }
}
