package com.shcheglov.task.registrationservice.api;

import com.shcheglov.task.registrationservice.model.Account;
import com.shcheglov.task.registrationservice.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * REST controller to perform operations on user accounts.
 *
 * @author Anton Shcheglov
 */
@Controller
@RequestMapping("/account")
@Slf4j
public class AccountResource {

    private final AccountService accountService;

    @Autowired
    public AccountResource(final AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid @NotNull final Account newAccount) {
        log.info("Creating account: " + newAccount);
        accountService.save(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).contentLength(0).build();
    }

}
