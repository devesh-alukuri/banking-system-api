package com.devesh.banking.controller;

import com.devesh.banking.dto.BankingDTO.*;
import com.devesh.banking.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Banking API", description = "Account management and transactions")
public class BankingController {

    private final BankingService bankingService;

    @PostMapping
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankingService.createAccount(req));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankingService.getAccount(accountNumber));
    }

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit money into account")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawRequest req) {
        return ResponseEntity.ok(bankingService.deposit(accountNumber, req));
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw money from account")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositWithdrawRequest req) {
        return ResponseEntity.ok(bankingService.withdraw(accountNumber, req));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between accounts")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest req) {
        return ResponseEntity.ok(bankingService.transfer(req));
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get transaction history for an account")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(bankingService.getTransactionHistory(accountNumber));
    }
}
