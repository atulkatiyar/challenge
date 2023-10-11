package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class TransferAmountService {


    private Lock balanceLock;

    public TransferAmountService() {
        balanceLock = new ReentrantLock();
    }

    /**
     * Method to withdraw amount from the account
     *
     * @param amount amount to be withdrawn
     * @throws InsufficientBalanceException Exception in case not enough balance
     */
    public void withdrawAmount(Account account, BigDecimal amount) throws InsufficientBalanceException {

        balanceLock.lock();
        try {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient funds in account " + account.getAccountId());
            }
            account.setBalance(account.getBalance().subtract(amount));
        } finally {
            balanceLock.unlock();
        }
    }

    /**
     * Method to deposit amount to the account
     *
     * @param amount amount to be deposited
     */
    public void depositAmount(Account account, BigDecimal amount) {
        balanceLock.lock();
        try {
            account.setBalance(account.getBalance().add(amount));
        } finally {
            balanceLock.unlock();
        }
    }
}
