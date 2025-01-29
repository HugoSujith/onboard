package com.hugo.metalbroker.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.hugo.metalbroker.model.transactions.Transactions;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepo {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TransactionRepo(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean createTransaction(Transactions transaction) {
        LocalDate today = LocalDate.now();

        String query = SQLQueryConstants.INSERT_INTO_TRANSACTION;
        Map<String, Object> params = new HashMap<>();
        params.put("transaction_id", transaction.getId());
        params.put("date", Date.valueOf(today));
        params.put("grams", transaction.getGrams());
        params.put("price", transaction.getPrice());
        params.put("status", transaction.getStatus());
        params.put("metal", transaction.getMetal());
        params.put("username", transaction.getUsername());

        int count = jdbcTemplate.update(query, params);

        return count > 0;
    }
}
