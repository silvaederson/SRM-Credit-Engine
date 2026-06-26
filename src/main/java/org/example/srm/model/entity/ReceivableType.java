package org.example.srm.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "receivable_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivableType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal spread;

    @Column(length = 50)
    private String category;

    private Boolean isActive = true;
}