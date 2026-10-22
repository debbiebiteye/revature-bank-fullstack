package com.revature.bank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class AmountRequest {

    //An amount must be supplied.
    @NotNull(message = "Amount is required.")
    //Zero and negative amounts are rejected.
    @DecimalMin(value = "0.01",
            message = "Amount must be at least 0.01.")
    //Limits the size and decimal places to match NUMERIC(12, 2)
    @Digits(integer = 10, fraction = 2,
            message = "Amount must have at most 10 whole-number digits and 2 decimal places.")
    private BigDecimal amount;

    public AmountRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
