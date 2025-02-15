package com.localgaji.taxi.address;

import com.localgaji.taxi.address.type.RoadNameAddress;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address") @Hidden
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column @NotNull
    private String placeName;

    @Embedded
    private RoadNameAddress roadNameAddress;
}
