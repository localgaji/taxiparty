package com.localgaji.taxi.address.type;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Embeddable @Builder @Getter
public class RoadNameAddress {
    @Column @NotNull
    private String city;

    @Column @NotNull
    private String district;

    @Column @NotNull
    private String roadName;

    @Column @NotNull
    private Integer buildingNumber;
}
