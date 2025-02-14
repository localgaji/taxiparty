package com.localgaji.taxi.place;

import com.localgaji.taxi.place.address.RoadNameAddress;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place") @Hidden
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column @NotNull
    private String placeName;

    @Embedded
    private RoadNameAddress roadNameAddress;
}
