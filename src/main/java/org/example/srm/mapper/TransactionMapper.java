package org.example.srm.mapper;

import org.example.srm.model.dto.request.SettlementRequest;
import org.example.srm.model.dto.response.TransactionResponse;
import org.example.srm.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creditor", ignore = true)
    @Mapping(target = "receivableType", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "presentValue", ignore = true)
    @Mapping(target = "settlementDate", ignore = true)
    @Mapping(target = "baseRate", ignore = true)
    @Mapping(target = "appliedSpread", ignore = true)
    @Mapping(target = "exchangeRateUsed", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(SettlementRequest request);

    @Mapping(source = "creditor.id", target = "creditorId")
    @Mapping(source = "creditor.name", target = "creditorName")
    @Mapping(source = "receivableType.id", target = "receivableTypeId")
    @Mapping(source = "receivableType.name", target = "receivableTypeName")
    @Mapping(source = "currency.code", target = "currencyCode")
    TransactionResponse toResponse(Transaction transaction);
}