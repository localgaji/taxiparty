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
                    party.getPickupPlace().getPlaceName(),
                    party.getDropoffPlace().getPlaceName(),
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
            List<PartyBriefDTO> partyList
    ) {
        public record PartyBriefDTO(
                Long partyId,
                String dropoff,
                LocalDateTime pickupTime,
                Integer headcount,
                Integer fullHeadcount,
                String chatPreview,
                Integer chatNoRead
        ) {
            public PartyBriefDTO(Party party, String chatPreview, int chatNoRead) {
                this(
                        party.getPartyId(),
                        party.getDropoffPlace().getPlaceName(),
                        party.getPickupTime(),
                        party.getHeadcount(),
                        party.getMaxHeadcount(),
                        chatPreview,
                        chatNoRead

                );
            }
        }
    }

    @Schema(description = "팀원 리스트")
    public record GetPassengersRes(
            List<PassengerInfo> passengers
    ) {
    }

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