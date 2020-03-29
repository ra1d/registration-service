package com.shcheglov.task.registrationservice.dao;

import com.shcheglov.task.registrationservice.model.Account;

/**
 * Persistence.
 *
 * @author Anton Shcheglov
 */
public interface AccountDao {

    void save(final Account account);

    Account getByUsername(final String username);

}
