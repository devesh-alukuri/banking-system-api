package com.devesh.banking.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String holderName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    private BigDecimal balance;

    private boolean active = true;

    @Version
    private Long version; // optimistic locking

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum AccountType { SAVINGS, CURRENT, FIXED_DEPOSIT }
}
