package com.localgaji.taxi.account;

import com.localgaji.taxi.user.User;

public record RequestAccount (
         String bankName,
         String bankNumber,
         String depositorName
) {
    public Account toEntity(User user) {
        return Account.builder()
                .user(user)
                .bankName(bankName)
                .bankNumber(bankNumber)
                .depositorName(depositorName)
                .build();
    }
}
