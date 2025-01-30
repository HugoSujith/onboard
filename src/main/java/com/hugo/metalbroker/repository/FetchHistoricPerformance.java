package com.hugo.metalbroker.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.hugo.metalbroker.exceptions.ApiFetchingFailureException;
import com.hugo.metalbroker.exceptions.DataFetchFailureException;
import com.hugo.metalbroker.model.datavalues.historic.HistoricPerformance;
import com.hugo.metalbroker.utils.ProtoUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class FetchHistoricPerformance {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProtoUtils protoUtils;
    private final Logger log;

    public FetchHistoricPerformance(NamedParameterJdbcTemplate jdbcTemplate, ProtoUtils protoUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.protoUtils = protoUtils;
        this.log = Logger.getLogger(this.getClass().getName());
    }

    @Scheduled(fixedRate = 7200000)
    public boolean data() {
        return checkDataIsPresent(Dotenv.load().get("SILVER_HISTORIC_URL"));
    }

    public boolean checkDataIsPresent(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        JsonNode response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = Objects.requireNonNull(restTemplate.getForObject(url, JsonNode.class)).get("_embedded").get("performances");
        } catch (Exception e) {
            throw new ApiFetchingFailureException(this.getClass().getName());
        }

        List<HistoricPerformance> performanceList = null;
        try {
            String query = SQLQueryConstants.GET_ALL_FROM_HISTORIC_PERFORMANCE;
            performanceList = jdbcTemplate.query(query, (rs, rowNum) -> HistoricPerformance.newBuilder()
                    .setDate(protoUtils.sqlDateToGoogleTimestamp(rs.getDate("date")))
                    .setMetal(metal)
                    .setFiveD(rs.getDouble("fiveD"))
                    .setFiveY(rs.getDouble("fiveY"))
                    .setMax(rs.getDouble("max"))
                    .setOneM(rs.getDouble("oneM"))
                    .setOneY(rs.getDouble("oneY"))
                    .setYtd(rs.getDouble("ytd"))
                    .setTenY(rs.getDouble("tenY"))
                    .build());
        } catch (Exception e) {
            throw new DataFetchFailureException(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        LocalDate today = LocalDate.now();
        Date todayDate = Date.valueOf(today);
        if (performanceList.isEmpty() || !performanceList.getLast().getDate().toString().equals(protoUtils.sqlDateToGoogleTimestamp(todayDate).toString())) {

            String insertQuery = SQLQueryConstants.INSERT_INTO_HISTORIC_PERFORMANCE;
            Map<String, Object> params = new HashMap<>();
            params.put("date", Date.valueOf(today));
            params.put("fived", response.get("5D").asDouble());
            params.put("fivey", response.get("5Y").asDouble());
            params.put("max", response.get("MAX").asDouble());
            params.put("onem", response.get("1M").asDouble());
            params.put("oney", response.get("1Y").asDouble());
            params.put("teny", response.get("10Y").asDouble());
            params.put("ytd", response.get("YTD").asDouble());
            params.put("metal", metal);

            jdbcTemplate.update(insertQuery, params);

            log.info("The whole api data (Historic Performance) has been successfully fetched and added to the database.");
        }

        return !performanceList.isEmpty();
    }
}
