package com.shcheglov.task.registrationservice.dao

import com.shcheglov.task.registrationservice.dao.AccountDao
import com.shcheglov.task.registrationservice.dao.AccountDaoImpl
import com.shcheglov.task.registrationservice.model.Account
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

/**
 * @author Anton Shcheglov
 */
class AccountDaoTest extends Specification {

    @Shared
    Account account1 = new Account('TestUser', 'P@$$w0rd1', LocalDate.of(1987, 6, 5), '1111222233334444')

    @Shared
    Account account2 = new Account('SomeOtherUser', 'P@$$w0rd2', LocalDate.of(1967, 8, 9), '1111000011110000111')

    @Shared
    Account account3 = new Account('LastOne', 'P@$$w0rd3', LocalDate.of(1991, 2, 3), '987654321012345')

    AccountDao accountDao

    def setup() {
        accountDao = new AccountDaoImpl(['TestUser': account1, 'SomeOtherUser': account2, 'LastOne': account3])
    }

    def "Should persist an account to the database"() {
        given: 'A new account'
        Account accountToSave = new Account('TestName', 'P@$$w0rd', LocalDate.of(1987, 6, 5), '1234123412341234')

        when:
        accountDao.save(accountToSave)

        then:
        assert accountDao.accountsByUsername['TestName'] == accountToSave
    }

    @Unroll
    def "Should get an account with the username [#username] if it exists"() {
        when:
        Account foundAccount = accountDao.getByUsername(username)

        then:
        assert foundAccount == expectedAccount

        where:
        username        || expectedAccount
        'TestUser'      || account1
        'SomeOtherUser' || account2
        'LastOne'       || account3
        'NoSuchUser'    || null
        ''              || null
        null            || null
    }

}
