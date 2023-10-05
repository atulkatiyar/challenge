package com.dws.challenge.exception;

/**
 * The InsufficientBalanceException wraps the condition when the source account doesn't have sufficient balance to amke transfer happen.
 * You can use this code to retrieve localized error messages.
 *
 * @author atulkatiyar
 */
public class InsufficientBalanceException extends RuntimeException{

    public InsufficientBalanceException(String message) {
        super(message);
    }

}
