package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.TransferAmountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@AllArgsConstructor
public class AccountsRepositoryInMemory implements AccountsRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Autowired
    private TransferAmountService transferAmountService;

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }


}
