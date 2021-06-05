package com.n26.service;

import com.n26.model.Statistics;
import com.n26.model.Transaction;

public interface TransactionalService {
    void newTransaction(Transaction transaction);
    Statistics getStatistics();
    void deleteAll();

}
