package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  /**
   * Method to transfer of money between accounts
   *
   * @param amountTransferRequest {@link AmountTransferRequest}
   */
  void transferAmount(AmountTransferRequest amountTransferRequest) throws AccountNotFoundException;
}
