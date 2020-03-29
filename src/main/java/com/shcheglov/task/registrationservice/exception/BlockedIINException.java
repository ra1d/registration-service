package com.shcheglov.task.registrationservice.exception;

/**
 * @author Anton Shcheglov
 */
public class BlockedIINException extends RuntimeException {

    public BlockedIINException(final String cardNumber) {
        super("The card is on the list of blocked issuer identification numbers: " + cardNumber);
    }

}
