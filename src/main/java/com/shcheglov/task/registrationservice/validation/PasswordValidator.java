package com.shcheglov.task.registrationservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Anton Shcheglov
 */
public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        return password.length() >= 4
                && password.matches(".*?[A-Z]+?.*?") // At least one capital letter
                && password.matches(".*?\\d+?.*?"); // At least one digit
    }

}
