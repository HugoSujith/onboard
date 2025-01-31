package com.hugo.metalbroker.repository;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FetchHistoricPerformance {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FetchHistoricPerformance(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkDataIfPresent(Map<String, Object> params) {
        String checkQuery = SQLQueryConstants.FIND_COUNT_OF_HISTORIC_ENTRY_BY_PK;
        int count = jdbcTemplate.queryForObject(checkQuery, params, Integer.class);
        return count > 0;
    }

    public boolean insertIntoDb(Map<String, Object> params) {
        String insertQuery = SQLQueryConstants.INSERT_INTO_HISTORIC_PERFORMANCE;
        int count = jdbcTemplate.update(insertQuery, params);
        return count > 0;
    }
}