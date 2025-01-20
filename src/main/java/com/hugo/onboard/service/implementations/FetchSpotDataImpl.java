package com.hugo.onboard.service.implementations;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.onboard.service.FetchSpotData;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchSpotDataImpl implements FetchSpotData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(FetchSpotDataImpl.class.getName());
    private int checker = 0;

    public FetchSpotDataImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Scheduled(fixedRate = 10000)
    @Override
    public boolean data() {
        boolean spotDataSilver = false;
        boolean spotDataGold = false;
        if (checker == 0) {
            spotDataSilver = storeData(Dotenv.load().get("SILVER_SPOT_URL"));
            spotDataGold = storeData(Dotenv.load().get("GOLD_SPOT_URL"));
        } else {
            spotDataSilver = updateData(Dotenv.load().get("SILVER_SPOT_URL"));
            spotDataGold = updateData(Dotenv.load().get("GOLD_SPOT_URL"));
        }
        checker++;
        return (spotDataSilver && spotDataGold);
    }

    @Override
    public boolean updateData(String url) {
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";

        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && !embeddedItems.isEmpty()) {
                JsonNode spotDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    Struct spotData = parseJsonToProto(spotDataJson);

                    LocalDateTime sqlDate = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

                    String checkQuery = "SELECT COUNT(*) FROM spot_items WHERE date = :date AND metal = :metal";

                    Map<String, Object> checkParams = new HashMap<>();
                    checkParams.put("date", sqlDate);
                    checkParams.put("metal", metal);

                    int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);

                    if (count == 0) {
                        return insertIntoDB(metal, spotData, sqlDate) > 0;
                    }
                } catch (Exception e) {
                    LOGGER.info("Error processing spot data: " + e.getMessage());
                }
            }
        }

        return false;
    }

    @Override
    public boolean storeData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";

        RestTemplate restTemplate = new RestTemplate();
        int value = -1000;
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode spotDataJson : embeddedItems) {
                try {
                    Struct spotData = parseJsonToProto(spotDataJson);
                    LocalDateTime sqlDate = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

                    value = insertIntoDB(metal, spotData, sqlDate);
                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return (value > 0);
    }

}
