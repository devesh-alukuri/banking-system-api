package com.devesh.banking.service;

import com.devesh.banking.dto.BankingDTO.*;
import com.devesh.banking.exception.InsufficientFundsException;
import com.devesh.banking.exception.AccountNotFoundException;
import com.devesh.banking.model.Account;
import com.devesh.banking.model.Transaction;
import com.devesh.banking.repository.AccountRepository;
import com.devesh.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse createAccount(CreateAccountRequest req) {
        String accountNumber = generateAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .holderName(req.getHolderName())
                .accountType(Account.AccountType.valueOf(req.getAccountType().toUpperCase()))
                .balance(req.getInitialDeposit())
                .active(true)
                .build();
        return mapToAccountResponse(accountRepository.save(account));
    }

    public AccountResponse getAccount(String accountNumber) {
        return mapToAccountResponse(findAccount(accountNumber));
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, DepositWithdrawRequest req) {
        Account account = findAccount(accountNumber);
        account.setBalance(account.getBalance().add(req.getAmount()));
        accountRepository.save(account);

        Transaction txn = buildTransaction(account, Transaction.TransactionType.DEPOSIT,
                req.getAmount(), account.getBalance(), req.getDescription(), null);
        return mapToTxnResponse(transactionRepository.save(txn));
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, DepositWithdrawRequest req) {
        Account account = findAccount(accountNumber);
        if (account.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(req.getAmount()));
        accountRepository.save(account);

        Transaction txn = buildTransaction(account, Transaction.TransactionType.WITHDRAWAL,
                req.getAmount(), account.getBalance(), req.getDescription(), null);
        return mapToTxnResponse(transactionRepository.save(txn));
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest req) {
        Account from = findAccount(req.getFromAccountNumber());
        Account to = findAccount(req.getToAccountNumber());

        if (from.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance for transfer. Available: " + from.getBalance());
        }

        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        // Debit transaction
        transactionRepository.save(buildTransaction(from, Transaction.TransactionType.TRANSFER_OUT,
                req.getAmount(), from.getBalance(), req.getDescription(), to.getAccountNumber()));

        // Credit transaction
        Transaction credit = buildTransaction(to, Transaction.TransactionType.TRANSFER_IN,
                req.getAmount(), to.getBalance(), req.getDescription(), from.getAccountNumber());
        return mapToTxnResponse(transactionRepository.save(credit));
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        Account account = findAccount(accountNumber);
        return transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId())
                .stream().map(this::mapToTxnResponse).collect(Collectors.toList());
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    private Transaction buildTransaction(Account account, Transaction.TransactionType type,
            BigDecimal amount, BigDecimal balanceAfter, String desc, String refAccount) {
        return Transaction.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .account(account).type(type).amount(amount)
                .balanceAfter(balanceAfter).description(desc)
                .referenceAccountNumber(refAccount).build();
    }

    private String generateAccountNumber() {
        return "ACC" + (1000000000L + new Random().nextInt(900000000));
    }

    private AccountResponse mapToAccountResponse(Account a) {
        AccountResponse r = new AccountResponse();
        r.setId(a.getId()); r.setAccountNumber(a.getAccountNumber());
        r.setHolderName(a.getHolderName()); r.setAccountType(a.getAccountType().name());
        r.setBalance(a.getBalance()); r.setActive(a.isActive()); r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    private TransactionResponse mapToTxnResponse(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId()); r.setTransactionId(t.getTransactionId());
        r.setType(t.getType().name()); r.setAmount(t.getAmount());
        r.setBalanceAfter(t.getBalanceAfter()); r.setDescription(t.getDescription());
        r.setReferenceAccountNumber(t.getReferenceAccountNumber()); r.setTimestamp(t.getTimestamp());
        return r;
    }
}
