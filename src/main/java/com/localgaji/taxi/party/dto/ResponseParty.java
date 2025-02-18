package com.localgaji.taxi.party.dto;

import com.localgaji.taxi.account.Account;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.party.passenger.Passenger;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseParty {
    @Schema(description = "파티 상세 정보")
    public record GetPartyRes(
            String pickup,
            String dropoff,
            LocalDateTime pickupTime,
            Integer Headcount,
            Integer maxHeadcount,
            FareDTO fare,
            Account accountDTO
    ) {
        public GetPartyRes(Party party) {
            this(
                    party.getPickupAddress().getPlaceName(),
                    party.getDropoffAddress().getPlaceName(),
                    party.getPickupTime(),
                    party.getHeadcount(),
                    party.getMaxHeadcount(),
                    new FareDTO( party.getFare(), (int) Math.ceil( (double) party.getFare() / party.getHeadcount()) ),
                    party.getAccount()

            );
        }
        public record FareDTO(
                Integer totalFare,
                Integer farePerPerson
        ) {

        }
    }

    @Schema(description = "내 파티 리스트")
    public record GetPartyListRes(
            List<MyPartyDTO> partyList
    ) {
        public record MyPartyDTO(
                Long partyId,
                String dropoff,
                LocalDateTime pickupTime,
                Integer headcount,
                Integer fullHeadcount,
                String chatPreview,
                Integer chatNoRead
        ) {
            public MyPartyDTO(Party party, String chatPreview, int chatNoRead) {
                this(
                        party.getPartyId(),
                        party.getDropoffAddress().getPlaceName(),
                        party.getPickupTime(),
                        party.getHeadcount(),
                        party.getMaxHeadcount(),
                        chatPreview,
                        chatNoRead

                );
            }
        }
    }
}