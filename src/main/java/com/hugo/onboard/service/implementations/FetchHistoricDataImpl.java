package com.hugo.onboard.service.implementations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.onboard.service.FetchHistoricData;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchHistoricDataImpl implements FetchHistoricData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(FetchHistoricDataImpl.class.getName());
    private int checker = 0;

    public FetchHistoricDataImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Scheduled(fixedRate = 10000)
    @Override
    public boolean data() {
        boolean historicDataSilver = false;
        boolean historicDataGold = false;
        if (checker == 0) {
            historicDataSilver = storeData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            historicDataGold = storeData(Dotenv.load().get("GOLD_HISTORIC_URL"));
        } else {
            historicDataSilver = updateData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            historicDataGold = updateData(Dotenv.load().get("GOLD_HISTORIC_URL"));
        }
        checker++;
        return (historicDataSilver && historicDataGold);
    }

    @Override
    public boolean updateData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && !embeddedItems.isEmpty()) {
                JsonNode historicDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    Struct historicData = parseJsonToProto(historicDataJson);

                    LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                    Date sqlDate = Date.valueOf(date);

                    String checkQuery = "SELECT COUNT(*) FROM historic_items WHERE date = :date AND metal = :metal";

                    Map<String, Object> checkParams = new HashMap<>();
                    checkParams.put("date", sqlDate);
                    checkParams.put("metal", metal);

                    int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);

                    if (count == 0) {
                        return insertIntoDB(metal, historicData, sqlDate) > 0;
                    }
                } catch (Exception e) {
                    LOGGER.info("Error processing historic data: " + e.getMessage());
                }
            }
        }

        return false;
    }

    @Override
    public boolean storeData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        int value = -1000;
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode historicDataJson : embeddedItems) {
                try {
                    Struct historicData = parseJsonToProto(historicDataJson);
                    LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                    Date sqlDate = Date.valueOf(date);

                    value = insertIntoDB(metal, historicData, sqlDate);
                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return (value > 0);
    }

}
