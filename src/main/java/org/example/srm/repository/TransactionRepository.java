package org.example.srm.repository;

import org.example.srm.model.entity.Transaction;
import org.example.srm.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCreditorId(Long creditorId);

    List<Transaction> findByStatus(TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.settlementDate BETWEEN :startDate AND :endDate")
    List<Transaction> findBySettlementDateBetween(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.creditor.id = :creditorId AND t.status = :status")
    List<Transaction> findByCreditorIdAndStatus(@Param("creditorId") Long creditorId,
                                                @Param("status") TransactionStatus status);

    Optional<Transaction> findByExternalReference(String externalReference);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.settlementDate = :date")
    long countBySettlementDate(@Param("date") LocalDate date);
}