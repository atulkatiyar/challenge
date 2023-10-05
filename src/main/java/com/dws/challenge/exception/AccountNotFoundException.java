package com.dws.challenge.exception;

/**
 * The AccountNotFoundException wraps the condition when the account id is either invalid or not present in the system.
 * You can use this code to retrieve localized error messages.
 *
 * @author atulkatiyar
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
