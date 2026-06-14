package com.devesh.banking.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balanceAfter;

    private String description;
    private String referenceAccountNumber;

    @CreationTimestamp
    private LocalDateTime timestamp;

    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }
}
