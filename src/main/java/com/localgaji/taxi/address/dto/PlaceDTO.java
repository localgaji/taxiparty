package com.localgaji.taxi.address.dto;

import com.localgaji.taxi.address.Address;
import com.localgaji.taxi.address.type.RoadNameAddress;

public record PlaceDTO (
        String placeName,
        RoadNameAddress roadNameAddress
) {
    public Address toEntity() {
        return Address.builder()
                .roadNameAddress(this.roadNameAddress)
                .placeName(this.placeName)
                .build();
    }
}
