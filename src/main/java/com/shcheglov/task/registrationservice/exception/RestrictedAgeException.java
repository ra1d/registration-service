package com.shcheglov.task.registrationservice.exception;

/**
 * @author Anton Shcheglov
 */
public class RestrictedAgeException extends RuntimeException {

    public RestrictedAgeException(final String username) {
        super("The user is under the age of 18: " + username);
    }

}
