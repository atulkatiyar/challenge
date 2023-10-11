package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.AmountTransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferAmountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private TransferAmountService transferAmountService;

  @BeforeEach
  void prepareMockMvc() {
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  void addAccount() {
    //accountsRepository.clearAccounts();
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
    //Test case for unrelated Account
  void testTransferAmountForDifferentAccount() {
    BigDecimal amountToTransfer = new BigDecimal(500);
    List<AmountTransferRequest> amountTransferRequests = new ArrayList<>();

    // Here I am creating 1000 number of account transfer request to check if it will work on multi threaded environment
    // Her from account and to Account are different everytime
    long startTime1 = System.nanoTime();
    for (int i = 1; i < 1001; i++) {
      String fromAcc = 100 + i + "";
      String toAcc = 5000 + i + "";
      this.accountsService.createAccount(new Account(fromAcc, new BigDecimal(500000)));
      this.accountsService.createAccount(new Account(toAcc));
      accountsService.transferAmount(AmountTransferRequest.builder().fromAccountId(""+ fromAcc)
              .amount(amountToTransfer).toAccountId(""+ toAcc).build());
    }
    long endTime1 = System.nanoTime();
    System.out.println("Total Elapsed time 2 " + (endTime1 - startTime1) / 1000000 + " ms");


    // After completion of all the request, the result should be :-
    // 499500 balance in all the Source account and 500 in all the destination accounts which is asserted below

    for (int i = 1; i < 1001; i++) {
      String fromAcc = 100 + i + "";
      String toAcc = 5000 + i + "";
      assertThat(accountsService.getAccount(fromAcc).getBalance()).isEqualTo(new BigDecimal(499500));
      assertThat(accountsService.getAccount(toAcc).getBalance()).isEqualTo(new BigDecimal(500));
    }
  }

  @Test
  void testTransferAmount() {
    BigDecimal amountToTransfer = new BigDecimal(500);
    List<AmountTransferRequest> amountTransferRequests = new ArrayList<>();
    this.accountsService.createAccount(new Account("Id-123", new BigDecimal(500000)));
    this.accountsService.createAccount(new Account("Id-456"));

    // Here I am creating 1000 number of account transfer request to check if it will work on multi threaded environment
    // here from account and to Account are same for every request
    long startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      accountsService.transferAmount(AmountTransferRequest.builder().fromAccountId("Id-123")
              .amount(amountToTransfer).toAccountId("Id-456").build());
    }
    long endTime = System.nanoTime();
    System.out.println("Total Elapsed time 1 " + (endTime - startTime) / 1000000 + " ms");

    // After completion of all the request, the result should be :-
    // Zero balance in Source account i.e. Id-123 and 500000 in Id-456 which is asserted below
    assertThat(accountsService.getAccount("Id-123").getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(accountsService.getAccount("Id-456").getBalance()).isEqualTo(new BigDecimal(500000));

  }



  @Test
  void transferAmountWithNotEnoughBalance() {
    BigDecimal amountToTransfer = new BigDecimal(200);
    this.accountsService.createAccount(new Account("Id-123", new BigDecimal(100)));
    this.accountsService.createAccount(new Account("Id-456"));

    try {
      this.accountsService.transferAmount(AmountTransferRequest.builder().fromAccountId("Id-123")
              .amount(amountToTransfer).toAccountId("Id-456").build());
      fail("Should be failed when there is insufficient balance in account");
    } catch (RuntimeException ex) {
      assertThat(ex.getMessage()).isEqualTo("java.util.concurrent.ExecutionException: " +
              "com.dws.challenge.exception.InsufficientBalanceException: Insufficient funds in account Id-123" );
    }
  }

  @Test
  void transferAmountWithInvalidAMount() {
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
