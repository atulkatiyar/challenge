package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.runnable.DepositAmountRunnable;
import com.dws.challenge.runnable.WithdrawAmountRunnable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AccountsService {

    private static final String TRANSFER_DESCRIPTION_WITHDRAWAL_MSG= " has been transferred successfully" ;
    private static final String TRANSFER_DESCRIPTION_DEPOSIT_MSG = " has been received" ;

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    @Autowired
    private TransferAmountService transferAmountService;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }


    /**
     * API method to transfer money between accounts
     *
     * @param amountTransferRequest {@link AmountTransferRequest}
     * @throws AccountNotFoundException Not Found Exception
     */
    public void transferAmount(AmountTransferRequest amountTransferRequest) {

        Account accountFrom = accountsRepository.getAccount(amountTransferRequest.getFromAccountId());
        Account accountTo = accountsRepository.getAccount(amountTransferRequest.getToAccountId());
        if (accountFrom == null) {
            throw new AccountNotFoundException("Account id " + amountTransferRequest.getFromAccountId() + " not found");
        }
        if (accountTo == null) {
            throw new AccountNotFoundException("Account id " + amountTransferRequest.getToAccountId() + " not found");
        }

        WithdrawAmountRunnable withdrawAmount = new WithdrawAmountRunnable(
                accountFrom, amountTransferRequest.getAmount(), transferAmountService);
        DepositAmountRunnable depositAmount = new DepositAmountRunnable(accountTo, amountTransferRequest.getAmount(),
                transferAmountService);

        ExecutorService exe = Executors.newFixedThreadPool(2);
        CompletableFuture<Void> withdrawalFuture = CompletableFuture.runAsync(withdrawAmount, exe);
        try {
            withdrawalFuture.get();
        } catch (InterruptedException | InsufficientBalanceException | ExecutionException exception) {
            throw new RuntimeException(exception);
        }
        CompletableFuture<Void> depositFuture = CompletableFuture.runAsync(depositAmount, exe);
    }

}
