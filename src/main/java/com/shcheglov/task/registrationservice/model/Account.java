package com.shcheglov.task.registrationservice.model;

import com.shcheglov.task.registrationservice.validation.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;


/**
 * User account.
 *
 * @author Anton Shcheglov
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(exclude = "password")
@ToString(exclude = "password")
public class Account implements Serializable {

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]+$")
    private String username;

    @NotNull
    @PasswordConstraint
    private String password;

    @NotNull
    private LocalDate dob;

    @NotNull
    @Pattern(regexp = "^[0-9]{15,19}$")
    private String paymentCardNumber;

}

