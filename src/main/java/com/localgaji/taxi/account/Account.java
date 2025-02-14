package com.localgaji.taxi.account;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account") @Hidden
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column @NotNull
    private String bankName;

    @Column @NotNull
    private String bankNumber;

    @Column @NotNull
    private String depositorName;
}
