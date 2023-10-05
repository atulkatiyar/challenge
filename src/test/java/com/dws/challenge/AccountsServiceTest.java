package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.EmailNotificationService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private NotificationService notificationService;

  @Test
  void addAccount() {
    accountsRepository.clearAccounts();
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void testTransferAmount() {
    accountsRepository.clearAccounts();
    BigDecimal amountToTransfer = new BigDecimal(50);
    List<AmountTransferRequest> amountTransferRequests = new ArrayList<>();
    this.accountsService.createAccount(new Account("Id-123", new BigDecimal(500000)));
    this.accountsService.createAccount(new Account("Id-456"));

    // Here I am creating 10K account transfer request to check if it will work on multithreaded environment
    for (int i = 0; i < 10000; i++) {
      amountTransferRequests.add(AmountTransferRequest.builder().fromAccountId("Id-123")
              .amount(amountToTransfer).toAccountId("Id-456").build());
    }

    // After creating 10K account transfer request on the same from and to account,
    // I am trying to execute all the requests in parallel using parallel streams
    amountTransferRequests
            .parallelStream()
            .forEach(amountTransferRequest -> accountsService.transferAmount(amountTransferRequest));
    assertThat(accountsService.getAccount("Id-123").getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(accountsService.getAccount("Id-456").getBalance()).isEqualTo(new BigDecimal(500000));

  }

  @Test
  void transferAmountWithNotEnoughBalance() {
    accountsRepository.clearAccounts();
    BigDecimal amountToTransfer = new BigDecimal(200);
    this.accountsService.createAccount(new Account("Id-123", new BigDecimal(100)));
    this.accountsService.createAccount(new Account("Id-456"));

    try {
      this.accountsService.transferAmount(AmountTransferRequest.builder().fromAccountId("Id-123")
              .amount(amountToTransfer).toAccountId("Id-456").build());
      fail("Should be failed when there is insufficient balance in account");
    } catch (InsufficientBalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Insufficient funds in account Id-123" );
    }
  }

  @Test
  void transferAmountWithInvalidAMount() {
    accountsRepository.clearAccounts();
    BigDecimal amountToTransfer = new BigDecimal(50);
    this.accountsService.createAccount(new Account("Id-456"));
    try {
      this.accountsService.transferAmount(AmountTransferRequest.builder().fromAccountId("Id-123")
              .amount(amountToTransfer).toAccountId("Id-456").build());
      fail("Should be failed when from account id is invalid");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id Id-123 not found");
    }

  }

}
