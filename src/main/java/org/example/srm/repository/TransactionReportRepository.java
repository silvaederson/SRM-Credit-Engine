package org.example.srm.repository;

import org.example.srm.model.dto.response.TransactionReportDTO;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<TransactionReportDTO> findTransactions(
            LocalDate startDate,
            LocalDate endDate,
            Long creditorId,
            String currencyCode,
            String status,
            int limit,
            int offset) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  t.id as transactionId, ");
        sql.append("  c.name as creditorName, ");
        sql.append("  rt.name as receivableType, ");
        sql.append("  t.face_value as faceValue, ");
        sql.append("  t.present_value as presentValue, ");
        sql.append("  t.currency_code as currencyCode, ");
        sql.append("  t.settlement_date as settlementDate, ");
        sql.append("  t.status as status, ");
        sql.append("  (t.face_value - t.present_value) / t.face_value * 100 as discountRate ");
        sql.append("FROM transactions t ");
        sql.append("JOIN creditors c ON t.creditor_id = c.id ");
        sql.append("JOIN receivable_types rt ON t.receivable_type_id = rt.id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (startDate != null) {
            sql.append("AND t.settlement_date >= ?").append(paramIndex++).append(" ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND t.settlement_date <= ?").append(paramIndex++).append(" ");
            params.add(endDate);
        }

        if (creditorId != null) {
            sql.append("AND t.creditor_id = ?").append(paramIndex++).append(" ");
            params.add(creditorId);
        }

        if (currencyCode != null && !currencyCode.isEmpty()) {
            sql.append("AND t.currency_code = ?").append(paramIndex++).append(" ");
            params.add(currencyCode);
        }

        if (status != null && !status.isEmpty()) {
            sql.append("AND t.status = ?").append(paramIndex++).append(" ");
            params.add(status);
        }

        sql.append("ORDER BY t.settlement_date DESC, t.id DESC ");

        if (limit > 0) {
            sql.append("LIMIT ?").append(paramIndex++).append(" ");
            params.add(limit);
        }

        if (offset > 0) {
            sql.append("OFFSET ?").append(paramIndex++).append(" ");
            params.add(offset);
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "TransactionReportMapping");

        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return query.getResultList();
    }

    public long countTransactions(LocalDate startDate, LocalDate endDate, Long creditorId, String currencyCode, String status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM transactions t WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (startDate != null) {
            sql.append("AND t.settlement_date >= ?").append(paramIndex++).append(" ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND t.settlement_date <= ?").append(paramIndex++).append(" ");
            params.add(endDate);
        }

        if (creditorId != null) {
            sql.append("AND t.creditor_id = ?").append(paramIndex++).append(" ");
            params.add(creditorId);
        }

        if (currencyCode != null && !currencyCode.isEmpty()) {
            sql.append("AND t.currency_code = ?").append(paramIndex++).append(" ");
            params.add(currencyCode);
        }

        if (status != null && !status.isEmpty()) {
            sql.append("AND t.status = ?").append(paramIndex++).append(" ");
            params.add(status);
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return ((Number) query.getSingleResult()).longValue();
    }
}