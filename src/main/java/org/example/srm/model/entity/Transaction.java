package org.example.srm.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.srm.model.enums.TransactionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creditor_id", nullable = false)
    private Creditor creditor;

    @ManyToOne
    @JoinColumn(name = "receivable_type_id", nullable = false)
    private ReceivableType receivableType;

    @Column(name = "face_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal faceValue;

    @Column(name = "present_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal presentValue;

    @ManyToOne
    @JoinColumn(name = "currency_code", referencedColumnName = "code", nullable = false)
    private Currency currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "base_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal baseRate;

    @Column(name = "applied_spread", nullable = false, precision = 5, scale = 4)
    private BigDecimal appliedSpread;

    @Column(name = "exchange_rate_used", precision = 19, scale = 6)
    private BigDecimal exchangeRateUsed;

    @Column(name = "payment_currency", length = 3)
    private String paymentCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(length = 500)
    private String notes;
}