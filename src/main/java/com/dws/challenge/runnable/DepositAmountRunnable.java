package com.dws.challenge.runnable;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.TransferAmountService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class DepositAmountRunnable implements Runnable{

    private Account accountTo;

    private BigDecimal amount;

    private TransferAmountService transferAmountService;

    @Override
    public void run() {
        this.transferAmountService.depositAmount(accountTo, amount);
    }
}
