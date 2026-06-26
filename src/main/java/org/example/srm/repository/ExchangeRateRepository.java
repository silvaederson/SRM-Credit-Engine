package org.example.srm.repository;

import org.example.srm.model.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT e FROM ExchangeRate e WHERE e.fromCurrency.code = :fromCode " +
            "AND e.toCurrency.code = :toCode AND e.effectiveDate <= :date " +
            "ORDER BY e.effectiveDate DESC")
    Optional<ExchangeRate> findLatestRate(@Param("fromCode") String fromCode,
                                          @Param("toCode") String toCode,
                                          @Param("date") LocalDate date);

    @Query("SELECT e FROM ExchangeRate e WHERE e.fromCurrency.code = :fromCode " +
            "AND e.toCurrency.code = :toCode AND e.effectiveDate = :date")
    Optional<ExchangeRate> findByDate(@Param("fromCode") String fromCode,
                                      @Param("toCode") String toCode,
                                      @Param("date") LocalDate date);
}