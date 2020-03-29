package com.shcheglov.task.registrationservice.exception;

/**
 * @author Anton Shcheglov
 */
public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(final String username) {
        super("Account already exists: " + username);
    }

}
