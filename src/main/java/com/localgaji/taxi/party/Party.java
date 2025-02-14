package com.localgaji.taxi.party;

import com.localgaji.taxi.account.Account;
import com.localgaji.taxi.party.passenger.Passenger;
import com.localgaji.taxi.place.Place;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "party") @Hidden @Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partyId;

    @Column @NotNull
    private LocalDateTime pickupTime;

    @Column @NotNull
    private Integer maxHeadcount;

    @Column @NotNull
    private Integer headcount;

    @Column
    private Integer fare;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "pickup_place_id")
    private Place pickupPlace;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "dropoff_place_id")
    private Place dropoffPlace;

    @OneToMany(mappedBy = "party") @Builder.Default @NotNull
    private List<Passenger> passengers = new ArrayList<>();

    @Column @Builder.Default @NotNull
    private PartyStatus partyStatus = PartyStatus.ACTIVE;

    public void deleteParty() {
        this.partyStatus = PartyStatus.DELETED;
    }

    public void endParty() {
        this.partyStatus = PartyStatus.END;
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public void addFare(Integer fare) {
        this.fare = fare;
    }
}
