package com.n26.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

// https://www.baeldung.com/java-money-and-currency Recommend to use money JSR 354
public class Transaction {
    // We are assuming amount is one currency(for ex: USD) we need to extend this and handle
    // all currencies like minor unit and 3 decimal places based os ISO 4217
    @NotNull(message = "amount can not be null!")
    private BigDecimal amount;
    @NotNull(message = "time can not be null!")
    private Instant timestamp;


    public Transaction(BigDecimal amount, Instant timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public Instant getTimestamp() {
        return timestamp;
    }

}
