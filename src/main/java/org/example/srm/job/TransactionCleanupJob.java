package org.example.srm.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.srm.model.entity.Transaction;
import org.example.srm.model.enums.TransactionStatus;
import org.example.srm.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCleanupJob {

    private final TransactionRepository transactionRepository;

    @Scheduled(cron = "0 0 2 * * *") // Executa todos os dias às 2:00 AM
    public void cleanupPendingTransactions() {
        log.info("Starting transaction cleanup job");

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
        List<Transaction> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PENDING);

        int cancelledCount = 0;
        for (Transaction transaction : pendingTransactions) {
            if (transaction.getCreatedAt().isBefore(cutoffTime)) {
                transaction.setStatus(TransactionStatus.CANCELLED);
                transactionRepository.save(transaction);
                cancelledCount++;
            }
        }

        log.info("Transaction cleanup completed. Cancelled {} pending transactions", cancelledCount);
    }

    @Scheduled(cron = "0 0 3 * * *") // Executa todos os dias às 3:00 AM
    public void logTransactionStatistics() {
        long total = transactionRepository.count();
        long pending = transactionRepository.findByStatus(TransactionStatus.PENDING).size();
        long settled = transactionRepository.findByStatus(TransactionStatus.SETTLED).size();
        long failed = transactionRepository.findByStatus(TransactionStatus.FAILED).size();

        log.info("Transaction Statistics: Total={}, Pending={}, Settled={}, Failed={}",
                total, pending, settled, failed);
    }
}