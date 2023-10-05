package com.dws.challenge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class AmountTransferRequest {

    @NotNull
    @NotEmpty
    private String fromAccountId;

    @NotNull
    @NotEmpty
    private String toAccountId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal amount;

}
