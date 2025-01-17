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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(FetchData.class.getName());

    public FetchData(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public boolean updateDataRegularly() {
        boolean histData = storeHistoricData();
        boolean spotData = storeSpotData();
        return (histData && spotData);
    }

    public boolean storeSpotData() {
        String url = "https://goldbroker.com/api/spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode i : embeddedItems) {
                try {
                    String jsonString = i.toString();
                    Struct.Builder builder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
                    Struct protobuf = builder.build();
                    String query = "INSERT IGNORE INTO spot_items (date, ask, mid, bid, value, performance, weight_unit) VALUES (:date, :ask, :mid, :bid, :value, :performance, :weightUnit)";
                    Map<String, Object> params = new HashMap<>();
                    LocalDateTime sqlDate = OffsetDateTime.parse(protobuf.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
                    params.put("date", sqlDate);
                    params.put("ask", protobuf.getFieldsMap().get("ask").getNumberValue());
                    params.put("mid", protobuf.getFieldsMap().get("mid").getNumberValue());
                    params.put("bid", protobuf.getFieldsMap().get("bid").getNumberValue());
                    params.put("value", protobuf.getFieldsMap().get("value").getNumberValue());
                    params.put("performance", protobuf.getFieldsMap().get("performance").getNumberValue());
                    params.put("weightUnit", protobuf.getFieldsMap().get("weight_unit").getStringValue());

                    namedParameterJdbcTemplate.update(query, params);

                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean storeHistoricData() {
        String url = "https://goldbroker.com/api/historical-spot-prices?metal=XAG&currency=PKR&weight_unit=g";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode i : embeddedItems) {
                try {
                    String jsonString = i.toString();
                    Struct.Builder builder = Struct.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(jsonString, builder);
                    Struct protobuf = builder.build();
                    String query = "INSERT IGNORE INTO historic_items (date, ma200, ma50, close, open, high, low, weight_unit) VALUES (:date, :ma200, :ma50, :close, :open, :high, :low, :weightUnit)";
                    Map<String, Object> params = new HashMap<>();
                    LocalDate date = LocalDate.parse(protobuf.getFieldsMap().get("date").getStringValue());
                    Date sqlDate = Date.valueOf(date);
                    params.put("date", sqlDate);
                    if (protobuf.getFieldsMap().get("MA200") != null) {
                        params.put("ma200", protobuf.getFieldsMap().get("MA200").getNumberValue());
                    } else {
                        params.put("ma200", -1000);
                    }
                    if (protobuf.getFieldsMap().get("MA50") != null) {
                        params.put("ma50", protobuf.getFieldsMap().get("MA50").getNumberValue());
                    } else {
                        params.put("ma50", -1000);
                    }
                    params.put("close", protobuf.getFieldsMap().get("close").getNumberValue());
                    params.put("open", protobuf.getFieldsMap().get("open").getNumberValue());
                    params.put("high", protobuf.getFieldsMap().get("high").getNumberValue());
                    params.put("low", protobuf.getFieldsMap().get("low").getNumberValue());
                    params.put("weightUnit", protobuf.getFieldsMap().get("weight_unit").getStringValue());

                    namedParameterJdbcTemplate.update(query, params);
                } catch (Exception e) {
                    LOGGER.info(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
}
