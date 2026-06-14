package com.devesh.banking.repository;

import com.devesh.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
}
