package com.localgaji.taxi.place.dto;

import com.localgaji.taxi.place.Place;
import com.localgaji.taxi.place.address.RoadNameAddress;

public record PlaceDTO (
        String placeName,
        RoadNameAddress roadNameAddress
) {
    public Place toEntity() {
        return Place.builder()
                .roadNameAddress(this.roadNameAddress)
                .placeName(this.placeName)
                .build();
    }
}
