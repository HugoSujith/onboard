package com.hugo.metalbroker.service.implementation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.hugo.metalbroker.exceptions.ApiFetchingFailureException;
import com.hugo.metalbroker.repository.FetchHistoricPerformance;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HistoricPerformanceService {
    private final Logger log;
    private final FetchHistoricPerformance historicPerformance;

    public HistoricPerformanceService(FetchHistoricPerformance historicPerformance) {
        this.log = Logger.getLogger(this.getClass().getName());
        this.historicPerformance = historicPerformance;
    }

    @Scheduled(fixedRate = 7200000)
    public boolean data() {
        boolean silver = insertHistoricPerformanceDataToDB(Dotenv.load().get("SILVER_HISTORIC_URL"));
        if (silver) {
            log.info("Historic Performance of Silver has been updated to database");
        }
        boolean gold = insertHistoricPerformanceDataToDB(Dotenv.load().get("GOLD_HISTORIC_URL"));
        if (gold) {
            log.info("Historic Performance of Gold has been updated to database");
        }
        return silver && gold;
    }

    public boolean insertHistoricPerformanceDataToDB(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        JsonNode response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = Objects.requireNonNull(restTemplate.getForObject(url, JsonNode.class)).get("_embedded").get("performances");
        } catch (Exception e) {
            throw new ApiFetchingFailureException(this.getClass().getName());
        }

        Map<String, Object> params = new HashMap<>();
        params.put("date", Date.valueOf(LocalDate.now()));
        params.put("fived", response.get("5D").asDouble());
        params.put("fivey", response.get("5Y").asDouble());
        params.put("max", response.get("MAX").asDouble());
        params.put("onem", response.get("1M").asDouble());
        params.put("oney", response.get("1Y").asDouble());
        params.put("teny", response.get("10Y").asDouble());
        params.put("ytd", response.get("YTD").asDouble());
        params.put("metal", metal);

        if (!historicPerformance.checkDataIfPresent(params)) {
            return historicPerformance.insertIntoDb(params);
        }
        return true;
    }
}
