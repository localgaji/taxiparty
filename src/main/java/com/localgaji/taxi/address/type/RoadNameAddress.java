package com.localgaji.taxi.address.type;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable @Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoadNameAddress {
    @Column @NotNull
    private String city;

    @Column @NotNull
    private String district;

    @Column @NotNull
    private String roadName;

    @Column @NotNull
    private String buildingNumber;


    public String toStringAddress() {

        StringBuffer sb = new StringBuffer();
        sb.append(this.city);
        sb.append(" ");
        sb.append(this.district);
        sb.append(" ");
        sb.append(this.roadName);
        sb.append(" ");
        sb.append(this.buildingNumber);

        return sb.toString();
    }
}
