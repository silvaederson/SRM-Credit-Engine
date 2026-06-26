package org.example.srm.repository;

import org.example.srm.model.entity.Creditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditorRepository extends JpaRepository<Creditor, Long> {
    Optional<Creditor> findByDocument(String document);

    List<Creditor> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Creditor c WHERE c.isActive = true")
    List<Creditor> findAllActive();
}