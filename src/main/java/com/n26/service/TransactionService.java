package com.n26.service;

import com.n26.config.AppConfigReader;
import com.n26.exception.TransactionNotValidException;
import com.n26.exception.TransactionTimeNotValidException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService implements TransactionalService {
    @Autowired
    private AppConfigReader appConfigReader;

    private static final List<Transaction> TRANSACTION_LIST = new ArrayList<>(); //to store transactions

    public final Integer seconds = 60000; //for further time comparison
    private Object lock = new Object(); //lock adding process for being threadSafe
    private Statistics statistics;

    @Override
    public void newTransaction(Transaction transaction) {
        if (!validateTimeDiff(transaction)) {
            throw new TransactionNotValidException();
        }
        synchronized (lock) {
            TRANSACTION_LIST.add(transaction);
            statisticAnalysis();
        }
    }

    @Override
    public Statistics getStatistics() {
        if (statistics != null) {
            removeOldData();
        }
        if (statistics == null) {
            statistics = new Statistics();
        }
        return statistics;
    }

    @Override
    public void deleteAll() {

        TRANSACTION_LIST.clear();
        statistics = new Statistics();
    }


    public boolean validateTimeDiff(Transaction transaction) {
        if (Instant.now().toEpochMilli() < transaction.getTimestamp().toEpochMilli()) {
            throw new TransactionTimeNotValidException();
        }
        if ((Instant.now().toEpochMilli() - transaction.getTimestamp().toEpochMilli()) < seconds
                && Instant.now().toEpochMilli() >= transaction.getTimestamp().toEpochMilli()) {

            return true;
        }
        return false;
    }

    public void statisticAnalysis() {
        if (TRANSACTION_LIST.isEmpty()) {
            statistics = new Statistics();
        } else {
            statistics = new Statistics(
                    TRANSACTION_LIST.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2, RoundingMode.HALF_UP),
                    TRANSACTION_LIST.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add).divide(BigDecimal.valueOf(TRANSACTION_LIST.size()), 2, BigDecimal.ROUND_HALF_UP).setScale(2, RoundingMode.HALF_UP),
                    TRANSACTION_LIST.stream().map(Transaction::getAmount).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP),
                    TRANSACTION_LIST.stream().map(Transaction::getAmount).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP),
                    (long) TRANSACTION_LIST.size());
        }
    }

    public void removeOldData() {
        TRANSACTION_LIST.removeIf(
                transaction -> (!validateTimeDiff(transaction)));
        statisticAnalysis();
    }
}
