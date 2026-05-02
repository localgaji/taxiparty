package com.localgaji.taxi.passenger;

import com.localgaji.taxi.__global__.utils.BaseTime;
import com.localgaji.taxi.party.Party;
import com.localgaji.taxi.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "passenger") @Hidden
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class Passenger extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column @NotNull @Builder.Default
    private Boolean isManager = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id") @NotNull
    private Party party;

    @Column @Builder.Default @NotNull @Enumerated(value = EnumType.STRING)
    private PassengerStatus status = PassengerStatus.ACTIVE;

    /** 해당 passenger 를 user, party 양방향 연결 */
    public void joinPassengerInParty() {
        this.party.getPassengers().add(this);
        this.user.getPassengerList().add(this);
    }

    public void leavePassenger() {
        this.status = PassengerStatus.LEFT;
    }

    public void kickPassenger() {
        this.status = PassengerStatus.KICKED_OUT;
    }

    public void activatePassenger()  {
        this.status = PassengerStatus.ACTIVE;
    }
}
