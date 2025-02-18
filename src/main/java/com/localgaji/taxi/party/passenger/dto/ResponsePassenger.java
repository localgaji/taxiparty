package com.localgaji.taxi.party.passenger.dto;

import com.localgaji.taxi.party.passenger.Passenger;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ResponsePassenger {

    @Schema(description = "팀원 리스트")
    public record GetPassengersRes(
            List<PassengerInfo> passengers
    ) {
    }

    @Schema(description = "팀원 정보")
    public record PassengerInfo(
            Long userId,
            String userName,
            Boolean isManager
    ){
        public PassengerInfo(Passenger passenger) {
            this(
                    passenger.getUser().getUserId(),
                    passenger.getUser().getUserName(),
                    passenger.getIsManager()
            );
        }
    }

}
