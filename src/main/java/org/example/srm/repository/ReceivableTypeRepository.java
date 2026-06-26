package org.example.srm.repository;

import org.example.srm.model.entity.ReceivableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceivableTypeRepository extends JpaRepository<ReceivableType, Long> {
    Optional<ReceivableType> findByName(String name);

    List<ReceivableType> findByIsActiveTrue();

    List<ReceivableType> findByCategory(String category);
}