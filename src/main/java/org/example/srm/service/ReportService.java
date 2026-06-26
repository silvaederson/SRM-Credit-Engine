package org.example.srm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.srm.model.dto.response.TransactionReportDTO;
import org.example.srm.repository.TransactionReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TransactionReportRepository reportRepository;

    public List<TransactionReportDTO> getSettlementReport(
            LocalDate startDate,
            LocalDate endDate,
            Long creditorId,
            String currencyCode,
            String status,
            int page,
            int size) {

        log.info("Generating settlement report: startDate={}, endDate={}, creditorId={}, currencyCode={}, status={}",
                startDate, endDate, creditorId, currencyCode, status);

        int offset = page * size;
        int limit = size;

        return reportRepository.findTransactions(startDate, endDate, creditorId, currencyCode, status, limit, offset);
    }

    public long countTransactions(
            LocalDate startDate,
            LocalDate endDate,
            Long creditorId,
            String currencyCode,
            String status) {

        return reportRepository.countTransactions(startDate, endDate, creditorId, currencyCode, status);
    }
}