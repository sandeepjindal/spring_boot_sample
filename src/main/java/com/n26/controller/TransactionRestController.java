package com.n26.controller;

import com.n26.model.Transaction;
import com.n26.service.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
/**
 * This is the controller for transactions
 */
public class TransactionRestController {
    @Autowired
    private TransactionalService transactionService;

    // Added explicit @ResponseBody for verbosity but this can also be omitted.
    @PostMapping("/transactions")
    public @ResponseBody ResponseEntity<?> newTransaction(@Valid @RequestBody Transaction transaction) {

        transactionService.newTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);

    }

    // This can be seperated into different controller if needed
    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ResponseEntity<?> getStatistics() {

        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getStatistics());
    }

    @DeleteMapping("/transactions")
    public @ResponseBody ResponseEntity<?> deleteAll() {
        transactionService.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


}
