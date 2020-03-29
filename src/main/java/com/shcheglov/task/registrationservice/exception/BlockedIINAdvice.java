package com.shcheglov.task.registrationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Anton Shcheglov
 */
@ControllerAdvice
public class BlockedIINAdvice {

    @ResponseBody
    @ExceptionHandler(BlockedIINException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String blockedIINHandler(BlockedIINException e) {
        return e.getMessage();
    }

}
