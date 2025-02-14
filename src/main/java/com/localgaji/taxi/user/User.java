package com.localgaji.taxi.user;

import com.localgaji.taxi.__global__.utils.BaseTime;
import com.localgaji.taxi.account.Account;
import com.localgaji.taxi.party.passenger.Passenger;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "user_table") @Hidden
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class User extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column @NotNull
    private String userName;

    @Column @NotNull
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "user") @Builder.Default @NotNull
    private List<Passenger> passengerList = new ArrayList<>();

    public void addAccount(Account account) {
        this.account = account;
    }
}