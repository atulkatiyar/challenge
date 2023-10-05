package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

    private static final String TRANSFER_DESCRIPTION_WITHDRAWAL_MSG= " has been transferred successfully" ;
    private static final String TRANSFER_DESCRIPTION_DEPOSIT_MSG = " has been received" ;

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

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
     * Method to transfer of money between accounts
     *
     * @param amountTransferRequest {@link AmountTransferRequest}
     */
    public void transferAmount(AmountTransferRequest amountTransferRequest) {
        this.accountsRepository.transferAmount(amountTransferRequest);
        this.notificationService.notifyAboutTransfer(this.getAccountsRepository().getAccount(amountTransferRequest.getFromAccountId()),
                amountTransferRequest.getAmount() + TRANSFER_DESCRIPTION_WITHDRAWAL_MSG);
        this.notificationService.notifyAboutTransfer(this.getAccountsRepository().getAccount(amountTransferRequest.getFromAccountId()),
                amountTransferRequest.getAmount() + TRANSFER_DESCRIPTION_DEPOSIT_MSG);
    }

}
