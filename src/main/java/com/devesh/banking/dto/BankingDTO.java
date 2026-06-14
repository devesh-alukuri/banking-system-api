package com.devesh.banking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankingDTO {

    @Data public static class CreateAccountRequest {
        @NotBlank private String holderName;
        @NotNull private String accountType;
        @DecimalMin("0.00") private BigDecimal initialDeposit = BigDecimal.ZERO;
    }

    @Data public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private String holderName;
        private String accountType;
        private BigDecimal balance;
        private boolean active;
        private LocalDateTime createdAt;
    }

    @Data public static class DepositWithdrawRequest {
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        private String description;
    }

    @Data public static class TransferRequest {
        @NotNull private String fromAccountNumber;
        @NotNull private String toAccountNumber;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        private String description;
    }

    @Data public static class TransactionResponse {
        private Long id;
        private String transactionId;
        private String type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private String description;
        private String referenceAccountNumber;
        private LocalDateTime timestamp;
    }
}
