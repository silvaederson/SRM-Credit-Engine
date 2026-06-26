package org.example.srm.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.srm.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingStrategyFactory {

    private final Map<String, PricingStrategy> strategies;

    public PricingStrategy getStrategy(String receivableTypeName) {
        if (receivableTypeName == null || receivableTypeName.trim().isEmpty()) {
            throw new BusinessException("Receivable type name cannot be null or empty");
        }

        // Mapear nome do tipo para bean name
        String beanName = mapToBeanName(receivableTypeName);

        PricingStrategy strategy = strategies.get(beanName);

        if (strategy == null) {
            log.warn("Strategy not found for type: {}, using default", receivableTypeName);
            // Usar estratégia padrão (Commercial Invoice)
            strategy = strategies.get("commercialInvoiceStrategy");
        }

        log.info("Using strategy: {} for type: {}", strategy.getStrategyName(), receivableTypeName);
        return strategy;
    }

    private String mapToBeanName(String receivableTypeName) {
        // Normalizar nome
        String normalized = receivableTypeName.toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .replace(" ", "");

        switch (normalized) {
            case "commercialinvoice":
            case "duplicatamerchant":
                return "commercialInvoiceStrategy";
            case "postdatedcheck":
            case "chequepredatado":
            case "chequepre-datado":
                return "postDatedCheckStrategy";
            case "governmentbond":
            case "títulopúblico":
            case "titulopublico":
                return "governmentBondStrategy";
            default:
                return "commercialInvoiceStrategy"; // Default
        }
    }
}