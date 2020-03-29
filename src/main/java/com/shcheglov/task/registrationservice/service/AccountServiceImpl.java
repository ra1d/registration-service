package com.shcheglov.task.registrationservice.service;


import com.shcheglov.task.registrationservice.dao.AccountDao;
import com.shcheglov.task.registrationservice.exception.AccountAlreadyExistsException;
import com.shcheglov.task.registrationservice.exception.BlockedIINException;
import com.shcheglov.task.registrationservice.exception.RestrictedAgeException;
import com.shcheglov.task.registrationservice.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Business logic.
 *
 * @author Anton Shcheglov
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final List<String> blockedIINs;

    public AccountServiceImpl(final AccountDao accountDao,
                              @Qualifier("blockedIINs") final List<String> blockedIINs) {
        this.accountDao = accountDao;
        this.blockedIINs = blockedIINs;
    }

    @Override
    public void save(final Account account) {
        log.info("Saving account: " + account);
        if (accountDao.getByUsername(account.getUsername()) == null) {
            if (isAgeUnder18(account)) {
                throw new RestrictedAgeException(account.getUsername());
            } else if (isIINBlocked(account)) {
                throw new BlockedIINException(account.getPaymentCardNumber());
            } else {
                accountDao.save(account);
            }
        } else {
            throw new AccountAlreadyExistsException(account.getUsername());
        }
    }

    private boolean isAgeUnder18(final Account account) {
        return account.getDob().plusYears(18).isAfter(LocalDate.now());
    }

    private boolean isIINBlocked(final Account account) {
        return blockedIINs.parallelStream()
                .anyMatch(blockedIIN -> Objects.equals(account.getPaymentCardNumber().substring(0, 6), blockedIIN));
    }

}
