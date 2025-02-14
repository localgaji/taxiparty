package com.localgaji.taxi.account;

import com.localgaji.taxi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service @Slf4j @RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void addAccount(User user, Account account) {
        accountRepository.save(account);
        user.addAccount(account);
    }
}
