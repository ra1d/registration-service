package com.shcheglov.task.registrationservice.validation


import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintValidatorContext

/**
 * @author Anton Shcheglov
 */
class PasswordValidatorTest extends Specification {

    ConstraintValidatorContext context = Mock(ConstraintValidatorContext)

    PasswordValidator passwordValidator = new PasswordValidator()

    @Unroll
    def "Password [#password] should be valid [#expectedResult]"() {
        when:
        def actualResult = passwordValidator.isValid(password, context)

        then:
        assert actualResult == expectedResult

        where:
        password           | expectedResult
        '123'              | false
        '1234'             | false
        'abc'              | false
        'Abc'              | false
        'zZ3'              | false
        'all_l0wercase'    | false
        'No-Number-Here'   | false
        '!@#$%^&*()-_=+'   | false
        'zZ3z'             | true
        'N0 LOWERCAS3'     | true
        'Numb3r-H3r3'      | true
        '!@#$%^&*()-_=+A1' | true
    }

}
