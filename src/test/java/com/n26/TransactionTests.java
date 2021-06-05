package com.n26;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.service.TransactionalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TransactionTests {
    private final long second = 60000;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TransactionalService transactionService;


    @Test
    public void testIfCurrentTimeReturnCreated() {

        assertEquals(HttpStatus.CREATED, restTemplate.postForEntity("/transactions", new Transaction(new BigDecimal(200.00), Instant.now()), Object.class)
                .getStatusCode());

    }

    @Test
    public void testIfPastTimeReturnNoContent() {

        assertEquals(HttpStatus.NO_CONTENT, restTemplate.postForEntity("/transactions", new Transaction(new BigDecimal(100.00), Instant.now().minusMillis(second)), Object.class)
                .getStatusCode());

    }

    @Test
    public void testIfFutureTimeReturnUnprocessable() {

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, restTemplate.postForEntity("/transactions", new Transaction(new BigDecimal("100.00"), Instant.now().plusMillis(second)), Object.class)
                .getStatusCode());

    }

    @Test
    public void testIfAmountIsNullReturnBadRequest() {

        assertEquals(HttpStatus.BAD_REQUEST, restTemplate.postForEntity("/transactions", new Transaction(null, Instant.now()), Object.class)
                .getStatusCode());
    }

    @Test
    public void testIfTimestampIsNullReturnBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, restTemplate.postForEntity("/transactions", new Transaction(new BigDecimal("34.44"), null), Object.class)
                .getStatusCode());
    }

    @Test
    public void testStatisticsIfReturnCorrectCount(){
        transactionService.deleteAll();
        transactionService.newTransaction(new Transaction(new BigDecimal("25.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("25.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("25.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("25.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("25.00"),Instant.now()));
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getCount().equals(5L));
    }
    @Test
    public void testStatisticsIfReturnCorrectSum(){
        transactionService.deleteAll();
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getSum().toString().equals("100.00"));
    }
    @Test
    public void testStatisticsIfReturnCorrectAvg(){
        transactionService.deleteAll();
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("30.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("40.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("10.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("60.00"),Instant.now()));
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getAvg().toString().equals("32.00"));
    }
    @Test
    public void testStatisticsIfReturnCorrectMax(){
        transactionService.deleteAll();
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("30.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("40.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("10.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("60.00"),Instant.now()));
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getMax().toString().equals("60.00"));
    }
    @Test
    public void testStatisticsIfReturnCorrectMin(){
        transactionService.deleteAll();
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("30.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("40.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("10.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("60.00"),Instant.now()));
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getMin().toString().equals("10.00"));
    }
    @Test
    public void deleteTest(){
        transactionService.newTransaction(new Transaction(new BigDecimal("20.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("30.00"),Instant.now()));
        transactionService.newTransaction(new Transaction(new BigDecimal("40.00"),Instant.now()));
        transactionService.deleteAll();
        Statistics statics = transactionService.getStatistics();
        assertTrue(statics.getCount().equals(0L));
    }
}
