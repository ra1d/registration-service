package com.shcheglov.task.registrationservice.dao;


import com.shcheglov.task.registrationservice.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Persistence.
 *
 * @author Anton Shcheglov
 */
@Repository
@Slf4j
public class AccountDaoImpl implements AccountDao {

    private final Map<String, Account> accountsByUsername;

    @Autowired
    public AccountDaoImpl(@Qualifier("accountsByUsername") final Map<String, Account> accountsByUsername) {
        this.accountsByUsername = accountsByUsername;
    }

    @Override
    public void save(final Account account) {
        log.info("Saving account: " + account);
        accountsByUsername.put(account.getUsername(), account);
    }

    @Override
    public Account getByUsername(final String username) {
        log.info("Getting account by username: " + username);
        return accountsByUsername.get(username);
    }

}
