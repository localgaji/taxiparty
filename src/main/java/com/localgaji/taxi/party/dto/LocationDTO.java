package com.localgaji.taxi.party.dto;

import com.localgaji.taxi.address.Address;
import com.localgaji.taxi.address.type.RoadNameAddress;

public class LocationDTO {

    public record CoordinateDTO(
            Double latitude,
            Double longitude
    ) {
    }

    public record AddressDTO(
            String placeName,
            RoadNameAddress roadNameAddress,
            CoordinateDTO coordinate
    ) {
        public Address toEntity() {
            return Address.builder()
                    .roadNameAddress(this.roadNameAddress)
                    .placeName(this.placeName)
                    .build();
        }
    }
}
