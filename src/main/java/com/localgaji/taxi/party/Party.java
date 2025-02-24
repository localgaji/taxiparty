package com.localgaji.taxi.party;

import com.localgaji.taxi.account.Account;
import com.localgaji.taxi.chat.Chat;
import com.localgaji.taxi.party.passenger.Passenger;
import com.localgaji.taxi.address.Address;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "party")
@Hidden @Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partyId;

    @Column @NotNull
    private LocalDateTime pickupTime;

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "account_id")
    private Account account;

    @Column(columnDefinition = "POINT SRID 4326") @NotNull
    private Point pickupPoint;

    @Column(columnDefinition = "POINT SRID 4326") @NotNull
    private Point dropoffPoint;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "pickup_address_id")
    private Address pickupAddress;

    @ManyToOne(fetch = FetchType.LAZY) @NotNull @JoinColumn(name = "dropoff_address_id")
    private Address dropoffAddress;

    @Column @NotNull
    private Integer maxHeadcount;

    @Column @NotNull
    private Integer headcount;

    @Column
    private Integer fare;

    @Column
    private String description;

    @OneToMany(mappedBy = "party") @Builder.Default @NotNull
    private List<Passenger> passengers = new ArrayList<>();

    @Column @Builder.Default @NotNull
    private PartyStatus status = PartyStatus.ACTIVE;

    @OneToMany(mappedBy = "party") @Builder.Default @NotNull
    @OrderBy("id asc")
    private List<Chat> chatList = new ArrayList<>();

    public void deleteParty() {
        this.status = PartyStatus.DELETED;
    }

    public void endParty() {
        this.status = PartyStatus.END;
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public void addFare(Integer fare) {
        this.fare = fare;
    }
}
