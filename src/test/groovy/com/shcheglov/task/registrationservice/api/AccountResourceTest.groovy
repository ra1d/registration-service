package com.shcheglov.task.registrationservice.api

import com.shcheglov.task.registrationservice.RegistrationServiceApplication
import com.shcheglov.task.registrationservice.dao.AccountDao
import com.shcheglov.task.registrationservice.model.Account
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author Anton Shcheglov
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = RegistrationServiceApplication)
class AccountResourceTest extends Specification {

    @LocalServerPort
    int localPort

    RestTemplate restTemplate

    @Autowired
    AccountDao accountDao

    def setup() {
        restTemplate = new RestTemplateBuilder().rootUri("http://localhost:$localPort/registration/account").build()
    }

    @Unroll
    def "Should successfully create an account for the user [#username], password [#password], date of birth [#dob] and card number [#cardNo]"() {
        given:
        Account newAccount = new Account(username, password, dob, cardNo)

        when:
        def response = restTemplate.postForEntity("/", newAccount, Object.class)

        then:
        assert response.statusCode == HttpStatus.CREATED
        assert response.body == null

        and:
        assert newAccount == accountDao.accountsByUsername[username]

        where:
        username    | password             | dob                      | cardNo
        'JohnDoe'   | 'Pass1'              | LocalDate.of(1987, 6, 5) | '123456789012345'
        'l33tC0d3r' | 'R3@l_CrYpT1c-$tUfF' | LocalDate.of(1999, 8, 7) | '1234567890123456'
        '007'       | '1234O4321'          | LocalDate.of(1971, 2, 3) | '12345678901234567'
    }

    def "Should return 409 if trying to create a preexisting account"() {
        given:
        def existingUsername = 'ExistingUser'
        Account newAccount = new Account(existingUsername, 'TestPassword21', LocalDate.of(1981, 2, 3), '2222333344445555')
        Account existingUsernameAccount = new Account(existingUsername, 'TestPassword22', LocalDate.of(1992, 3, 4), '3333444455556666')
        def responseStatus = ''
        def responseBody = ''

        when: "Posting the first time"
        restTemplate.postForEntity("/", newAccount, Object.class)

        then: "The account should be saved normally"
        assert newAccount == accountDao.accountsByUsername[existingUsername]

        when: "Posting an account with the same username"
        try {
            restTemplate.postForEntity("/", existingUsernameAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then: "The request should fail"
        assert responseStatus == HttpStatus.CONFLICT
        assert responseBody?.contains(existingUsername)
    }

    @Unroll
    def "Should return 406 if the payment card number [#cardNo] is on the list of blocked IINs"() {
        given:
        Account newAccount = new Account("ValidName", 'TestPassword6', LocalDate.of(1974, 4, 4), cardNo)
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.NOT_ACCEPTABLE
        assert responseBody?.contains(cardNo)

        where:
        cardNo << ['0000001234567890', '9999007890112233', '0011225678990011']
    }

    def "Should return 403 if the user is under the age of 18"() {
        given:
        Account newAccount = new Account('TooYoungUser', 'TestPassword3', LocalDate.now().minusYears(17).minusMonths(10), '4444555566667777')
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.FORBIDDEN
        assert responseBody?.contains('TooYoungUser')
    }

    @Unroll
    def "Should return 400 if the username [#username] is invalid"() {
        given:
        Account newAccount = new Account(username, 'TestPassword4', LocalDate.of(1985, 4, 3), '5555666677778888')
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.BAD_REQUEST
        assert responseBody?.contains(username)

        where:
        username << ['With Space', 'Hash#Symbol', 'doll@r_$ign', 'Hy-Phen']
    }

    @Unroll
    def "Should return 400 if the password [#password] is invalid"() {
        given:
        Account newAccount = new Account('ImaSimpleGuy', password, LocalDate.of(1989, 1, 2), '5555666677778888999')
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.BAD_REQUEST
        assert responseBody?.contains(password)

        where:
        password << ['123', 'abc', 'Abc', 'zZ3', 'all_l0wercase', 'No-Number-Here']
    }

    @Unroll
    def "Should return 400 if the date of birth [#dob] is invalid"() {
        given:
        def newAccount = new JsonSlurper().parseText """{
            "username": "Test1",
            "password": "Password5",
            "dob": "$dob",
            "paymentCardNumber": "6666777788889999"
        }"""
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.BAD_REQUEST
        assert responseBody?.contains(dob)

        where:
        dob << ['15-03-1987', '01-23-1945', '1987/11/22', '10.11.2003', '23 April 1995', 'June 7, 1989']
    }

    @Unroll
    def "Should return 400 if the payment card number [#cardNo] is invalid"() {
        given:
        Account newAccount = new Account("ValidName", 'TestPassword6', LocalDate.of(1985, 4, 3), cardNo)
        def responseStatus = ''
        def responseBody = ''

        when:
        try {
            restTemplate.postForEntity("/", newAccount, String.class)
        } catch (HttpStatusCodeException e) {
            responseStatus = e.getStatusCode()
            responseBody = e.getResponseBodyAsString()
        }

        then:
        assert responseStatus == HttpStatus.BAD_REQUEST
        assert responseBody?.contains(cardNo)

        where:
        cardNo << ['12345678901234', '12345678901234567890', '123456789o123456', '1234567890 12345']
    }

}
