package com.localgaji.taxi.account;

import com.localgaji.taxi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void addAccount(User user, RequestAccount dto) {
        Account account = dto.toEntity(user);
        accountRepository.save(account);
        user.addAccount(account);
    }
}
