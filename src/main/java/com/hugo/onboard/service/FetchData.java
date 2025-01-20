package com.hugo.onboard.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(FetchData.class.getName());
    private int checker = 0;

    public FetchData(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public boolean data() {
        boolean historicDataSilver = false;
        boolean spotDataSilver = false;
        boolean historicDataGold = false;
        boolean spotDataGold = false;
        if (checker == 0) {
            historicDataSilver = storeHistoricData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            spotDataSilver = storeSpotData(Dotenv.load().get("SILVER_SPOT_URL"));
            historicDataGold = storeHistoricData(Dotenv.load().get("GOLD_HISTORIC_URL"));
            spotDataGold = storeSpotData(Dotenv.load().get("GOLD_SPOT_URL"));
        } else {
            spotDataSilver = updateSpotData(Dotenv.load().get("SILVER_SPOT_URL"));
            historicDataSilver = updateHistoricData(Dotenv.load().get("SILVER_HISTORIC_URL"));
            historicDataGold = updateHistoricData(Dotenv.load().get("GOLD_HISTORIC_URL"));
            spotDataGold = updateSpotData(Dotenv.load().get("GOLD_SPOT_URL"));
        }
        checker++;
        return (historicDataSilver && spotDataSilver && spotDataGold && historicDataGold);
    }

    public boolean updateSpotData(String url) {
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        String metal = "";
        if (url.equals(Dotenv.load().get("SILVER_SPOT_URL"))) {
            metal = "silver";
        } else if (url.equals(Dotenv.load().get("GOLD_SPOT_URL"))) {
            metal = "gold";
        } else {
            throw new IllegalArgumentException("The URL does not correspond to a valid metal type.");
        }
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && embeddedItems.size() > 0) {
                // Extract the last element
                JsonNode spotDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    String jsonToStr = spotDataJson.toString();
                    Struct.Builder spotDataBuilder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, spotDataBuilder);
                    Struct spotData = spotDataBuilder.build();

                    // Extract values from the Struct
                    LocalDateTime sqlDate = OffsetDateTime.parse(
                            spotData.getFieldsMap().get("date").getStringValue(),
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    ).toLocalDateTime();

                    String checkQuery = "";

                    if (metal.equals("gold")) {
                        checkQuery = "SELECT COUNT(*) FROM spot_items_gold WHERE date = :date";
                    } else {
                        checkQuery = "SELECT COUNT(*) FROM spot_items_silver WHERE date = :date";
                    }

                    Map<String, Object> checkParams = new HashMap<>();
                    checkParams.put("date", sqlDate);

                    int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);

                    if (count == 0) {
                        String insertQuery = "";
                        if (metal.equals("gold")) {
                            insertQuery = "INSERT INTO spot_items_gold (date, ask, mid, bid, value, performance, weight_unit) VALUES (:date, :ask, :mid, :bid, :value, :performance, :weightUnit)";
                        } else {
                            insertQuery = "INSERT INTO spot_items_silver (date, ask, mid, bid, value, performance, weight_unit) VALUES (:date, :ask, :mid, :bid, :value, :performance, :weightUnit)";
                        }
                        Map<String, Object> insertParams = new HashMap<>();
                        insertParams.put("date", sqlDate);
                        insertParams.put("ask", spotData.getFieldsMap().get("ask").getNumberValue());
                        insertParams.put("mid", spotData.getFieldsMap().get("mid").getNumberValue());
                        insertParams.put("bid", spotData.getFieldsMap().get("bid").getNumberValue());
                        insertParams.put("value", spotData.getFieldsMap().get("value").getNumberValue());
                        insertParams.put("performance", spotData.getFieldsMap().get("performance").getNumberValue());
                        insertParams.put("weightUnit", spotData.getFieldsMap().get("weight_unit").getStringValue());

                        int value = namedParameterJdbcTemplate.update(insertQuery, insertParams);

                        return value > 0;
                    }
                } catch (Exception e) {
                    LOGGER.info("Error processing spot data: " + e.getMessage());
                }
            }
        }

        return false;
    }

    public boolean updateHistoricData(String url) {
        String metal = "";
        if (url.equals(Dotenv.load().get("SILVER_HISTORIC_URL"))) {
            metal = "silver";
        } else if (url.equals(Dotenv.load().get("GOLD_HISTORIC_URL"))) {
            metal = "gold";
        } else {
            throw new IllegalArgumentException("The URL does not correspond to a valid metal type.");
        }
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && embeddedItems.size() > 0) {
                // Extract the last element
                JsonNode historicDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    String jsonToStr = historicDataJson.toString();
                    Struct.Builder hhistoricDataBuilder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, hhistoricDataBuilder);
                    Struct historicData = hhistoricDataBuilder.build();

                    LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                    Date sqlDate = Date.valueOf(date);

                    String checkQuery = "";

                    if (metal.equals("gold")) {
                        checkQuery = "SELECT COUNT(*) FROM historic_items_gold WHERE date = :date";
                    } else {
                        checkQuery = "SELECT COUNT(*) FROM historic_items_silver WHERE date = :date";
                    }

                    Map<String, Object> checkParams = new HashMap<>();
                    checkParams.put("date", sqlDate);

                    int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);

                    if (count == 0) {
                        String insertQuery = "";
                        if (metal.equals("silver")) {
                            insertQuery = "INSERT INTO historic_items_silver (date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";
                        } else {
                            insertQuery = "INSERT INTO historic_items_gold (date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";
                        }
                        Map<String, Object> insertParams = new HashMap<>();
                        insertParams.put("date", sqlDate);
                        insertParams.put("open", historicData.getFieldsMap().get("open").getNumberValue());
                        insertParams.put("close", historicData.getFieldsMap().get("close").getNumberValue());
                        insertParams.put("high", historicData.getFieldsMap().get("high").getNumberValue());
                        insertParams.put("low", historicData.getFieldsMap().get("low").getNumberValue());
                        insertParams.put("ma50", historicData.getFieldsMap().get("ma50").getNumberValue());
                        insertParams.put("ma200", historicData.getFieldsMap().get("ma200").getNumberValue());
                        insertParams.put("weightUnit", historicData.getFieldsMap().get("weight_unit").getStringValue());

                        int value = namedParameterJdbcTemplate.update(insertQuery, insertParams);

                        return value > 0;
                    }
                } catch (Exception e) {
                    LOGGER.info("Error processing historic data: " + e.getMessage());
                }
            }
        }

        return false;
    }

    public boolean storeSpotData(String url) {
        String metal = "";
        if (url.equals(Dotenv.load().get("SILVER_SPOT_URL"))) {
            metal = "silver";
        } else if (url.equals(Dotenv.load().get("GOLD_SPOT_URL"))) {
            metal = "gold";
        } else {
            throw new IllegalArgumentException("The URL does not correspond to a valid metal type.");
        }
        RestTemplate restTemplate = new RestTemplate();
        int value = -1000;
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode spotDataJson : embeddedItems) {
                try {
                    String jsonToStr = spotDataJson.toString();
                    Struct.Builder spotDataBuilder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, spotDataBuilder);
                    Struct spotData = spotDataBuilder.build();

                    String query = "";

                    if (metal.equals("gold")) {
                        query = "INSERT INTO spot_items_gold (date, ask, mid, bid, value, performance, weight_unit) VALUES (:date, :ask, :mid, :bid, :value, :performance, :weightUnit)";
                    } else {
                        query = "INSERT INTO spot_items_silver (date, ask, mid, bid, value, performance, weight_unit) VALUES (:date, :ask, :mid, :bid, :value, :performance, :weightUnit)";
                    }

                    Map<String, Object> params = new HashMap<>();
                    LocalDateTime sqlDate = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
                    params.put("date", sqlDate);
                    params.put("ask", spotData.getFieldsMap().get("ask").getNumberValue());
                    params.put("mid", spotData.getFieldsMap().get("mid").getNumberValue());
                    params.put("bid", spotData.getFieldsMap().get("bid").getNumberValue());
                    params.put("value", spotData.getFieldsMap().get("value").getNumberValue());
                    params.put("performance", spotData.getFieldsMap().get("performance").getNumberValue());
                    params.put("weightUnit", spotData.getFieldsMap().get("weight_unit").getStringValue());

                    value = namedParameterJdbcTemplate.update(query, params);

                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return (value > 0);
    }

    public boolean storeHistoricData(String url) {
        String metal = "";
        if (url.equals(Dotenv.load().get("SILVER_HISTORIC_URL"))) {
            metal = "silver";
        } else if (url.equals(Dotenv.load().get("GOLD_HISTORIC_URL"))) {
            metal = "gold";
        } else {
            throw new IllegalArgumentException("The URL does not correspond to a valid metal type.");
        }
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        int value = -1000;
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode historicDataJson : embeddedItems) {
                try {
                    String jsonToStr = historicDataJson.toString();
                    Struct.Builder historicDataBuilder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, historicDataBuilder);
                    Struct historicData = historicDataBuilder.build();

                    String query = "";

                    if (metal.equals("gold")) {
                        query = "INSERT INTO historic_items_gold (date, ma200, ma50, close, open, high, low, weight_unit) VALUES (:date, :ma200, :ma50, :close, :open, :high, :low, :weightUnit)";
                    } else {
                        query = "INSERT INTO historic_items_silver (date, ma200, ma50, close, open, high, low, weight_unit) VALUES (:date, :ma200, :ma50, :close, :open, :high, :low, :weightUnit)";
                    }

                    Map<String, Object> params = new HashMap<>();
                    LocalDate date = LocalDate.parse(historicData.getFieldsMap().get("date").getStringValue());
                    Date sqlDate = Date.valueOf(date);
                    params.put("date", sqlDate);
                    if (historicData.getFieldsMap().get("MA200") != null) {
                        params.put("ma200", historicData.getFieldsMap().get("MA200").getNumberValue());
                    } else {
                        params.put("ma200", -1000);
                    }
                    if (historicData.getFieldsMap().get("MA50") != null) {
                        params.put("ma50", historicData.getFieldsMap().get("MA50").getNumberValue());
                    } else {
                        params.put("ma50", -1000);
                    }
                    params.put("close", historicData.getFieldsMap().get("close").getNumberValue());
                    params.put("open", historicData.getFieldsMap().get("open").getNumberValue());
                    params.put("high", historicData.getFieldsMap().get("high").getNumberValue());
                    params.put("low", historicData.getFieldsMap().get("low").getNumberValue());
                    params.put("weightUnit", historicData.getFieldsMap().get("weight_unit").getStringValue());
                    value = namedParameterJdbcTemplate.update(query, params);
                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return (value > 0);
    }
}
