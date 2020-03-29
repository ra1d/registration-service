package com.shcheglov.task.registrationservice.service

import com.shcheglov.task.registrationservice.dao.AccountDao
import com.shcheglov.task.registrationservice.exception.AccountAlreadyExistsException
import com.shcheglov.task.registrationservice.exception.BlockedIINException
import com.shcheglov.task.registrationservice.exception.RestrictedAgeException
import com.shcheglov.task.registrationservice.model.Account
import spock.lang.Specification

import java.time.LocalDate

/**
 * @author Anton Shcheglov
 */
class AccountServiceTest extends Specification {

    AccountDao accountDao = Mock(AccountDao)

    AccountService accountService = new AccountServiceImpl(accountDao, ['000000', '123456', '987654'])

    def "Should save an account"() {
        given:
        Account newAccount = new Account('NewUser', 'TestPassword', LocalDate.of(1987, 6, 5), '1111222233334444')
        accountDao.getByUsername('NewUser') >> null

        when:
        accountService.save(newAccount)

        then:
        1 * accountDao.save(newAccount)
    }

    def "Should throw an exception if trying to create a preexisting account"() {
        given:
        def existingUsername = 'ExistingUser'
        Account newAccount = new Account(existingUsername, 'TestPassword1', LocalDate.of(1987, 6, 5), '1111222233334444')
        Account existingAccount = new Account(existingUsername, 'TestPassword2', LocalDate.of(1989, 8, 7), '2222333344445555')
        accountDao.getByUsername(existingUsername) >> existingAccount

        when:
        accountService.save(newAccount)

        then:
        0 * accountDao.save(newAccount)

        Exception exception = thrown(AccountAlreadyExistsException)
        assert exception.message.contains(existingUsername)
    }

    def "Should throw an exception if the user is under the age of 18"() {
        given:
        Account newAccount = new Account('TooYoungUser', 'TooSimplePassword', LocalDate.now().minusYears(17).minusMonths(10), '4444555566667777')

        when:
        accountService.save(newAccount)

        then:
        0 * accountDao.save(newAccount)

        Exception exception = thrown(RestrictedAgeException)
        assert exception.message.contains('TooYoungUser')
    }

    def "Should throw an exception if card [#cardNo] is on the list of blocked IINs"() {
        given:
        Account newAccount = new Account('BlockedUser', 'Password', LocalDate.of(1984, 3, 2), cardNo)
        accountDao.getByUsername('NewUser') >> null

        when:
        accountService.save(newAccount)

        then:
        0 * accountDao.save(newAccount)

        Exception exception = thrown(BlockedIINException)
        assert exception.message.contains(cardNo)

        where:
        cardNo << ['0000001234567890', '1234567890112233', '9876545678990011']
    }

}
