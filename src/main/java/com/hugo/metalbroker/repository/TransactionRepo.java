package com.hugo.metalbroker.repository;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.metalbroker.exceptions.TransactionCreationFailureException;
import com.hugo.metalbroker.model.transactions.Transactions;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepo {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Logger log;

    public TransactionRepo(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.log = Logger.getLogger(this.getClass().getName());
    }

    public boolean createTransaction(Transactions transaction) {
        LocalDate today = LocalDate.now();

        int count = 0;
        try {
            String query = SQLQueryConstants.INSERT_INTO_TRANSACTION;
            Map<String, Object> params = new HashMap<>();
            params.put("transaction_id", transaction.getId());
            params.put("date", Date.valueOf(today));
            params.put("grams", transaction.getGrams());
            params.put("price", transaction.getPrice());
            params.put("status", transaction.getStatus());
            params.put("metal", transaction.getMetal());
            params.put("username", transaction.getUsername());

            count = jdbcTemplate.update(query, params);
        } catch (Exception e) {
            throw new TransactionCreationFailureException(transaction.getId());
        }

        log.info("A new transaction has been made with id: " + Base64.getEncoder().encodeToString(transaction.getId().getBytes(StandardCharsets.UTF_8)));

        return count > 0;
    }
}
