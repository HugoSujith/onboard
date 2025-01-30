package com.hugo.metalbroker.repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.metalbroker.exceptions.ApiFetchingFailureException;
import com.hugo.metalbroker.exceptions.DataFetchFailureException;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.utils.ProtoUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class FetchSpotData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private int checker = 0;
    private final ProtoUtils protoUtils;
    private final Logger log;

    public FetchSpotData(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ProtoUtils protoUtils) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.protoUtils = protoUtils;
        this.log = Logger.getLogger(this.getClass().getName());
    }

    @Scheduled(fixedRate = 10000)
    public boolean data() {
        boolean spotDataSilver = false;
        boolean spotDataGold = false;
        if (checker == 0) {
            spotDataSilver = storeData(Dotenv.load().get("SILVER_SPOT_URL"));
            spotDataGold = storeData(Dotenv.load().get("GOLD_SPOT_URL"));
            log.info("The whole api data (Spot Items) has been successfully fetched and added to the database.");
        } else {
            spotDataSilver = updateData(Dotenv.load().get("SILVER_SPOT_URL"));
            spotDataGold = updateData(Dotenv.load().get("GOLD_SPOT_URL"));
            log.info("The new api data (Spot Items) has been successfully fetched, verified and added to the database.");
        }
        checker++;
        return (spotDataSilver && spotDataGold);
    }

    public boolean updateData(String url) {
        JsonNode response = null;
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new ApiFetchingFailureException(this.getClass().getName());
        }

        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && !embeddedItems.isEmpty()) {
                JsonNode spotDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    Struct spotData = protoUtils.parseJsonToProto(spotDataJson);

                    LocalDateTime sqlDate = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

                    String checkQuery = SQLQueryConstants.FIND_COUNT_OF_SPOT_ITEMS_BY_PK;

                    Map<String, Object> checkParams = new HashMap<>();
                    checkParams.put("date", sqlDate);
                    checkParams.put("metal", metal);

                    int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);

                    if (count == 0) {
                        return insertIntoDB(metal, spotData, sqlDate) > 0;
                    }
                } catch (Exception e) {
                    throw new ApiFetchingFailureException(this.getClass().getName());
                }
            }
        }

        return false;
    }

    public boolean storeData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_SPOT_URL")) ? "silver" : "gold";
        int value = -1000;

        JsonNode response = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            throw new ApiFetchingFailureException(e.getClass().getName());
        }
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode spotDataJson : embeddedItems) {
                try {
                    Struct spotData = protoUtils.parseJsonToProto(spotDataJson);
                    LocalDateTime sqlDate = OffsetDateTime.parse(spotData.getFieldsMap().get("date").getStringValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();

                    value = insertIntoDB(metal, spotData, sqlDate);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return (value > 0);
    }

    public SpotItemsList getItems(String metal) {
        String query = SQLQueryConstants.GET_ALL_FROM_SPOT_ITEMS_BY_METAL;
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);

        List<SpotItems> data = null;
        try {
            data = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> SpotItems.newBuilder()
                    .setDate(protoUtils.sqlDateToGoogleTimestamp(rs.getDate("date")))
                    .setMetal(rs.getString("metal"))
                    .setWeightUnit(rs.getString("weight_unit"))
                    .setAsk(rs.getDouble("ask"))
                    .setBid(rs.getDouble("bid"))
                    .setMid(rs.getDouble("mid"))
                    .setValue(rs.getDouble("value"))
                    .setPerformance(rs.getDouble("performance"))
                    .build());
        } catch (DataAccessException e) {
            throw new DataFetchFailureException(this.getClass().getName());
        }
        SpotItemsList.Builder spotItemsListBuilder = SpotItemsList.newBuilder();
        spotItemsListBuilder.addAllItems(data);
        return spotItemsListBuilder.build();
    }

    public int insertIntoDB(String metal, Struct spotData, LocalDateTime sqlDate) {
        String query = SQLQueryConstants.INSERT_INTO_SPOT_ITEMS;

        Map<String, Object> params = buildParamsForData(spotData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(query, params);
    }

    public Map<String, Object> buildParamsForData(Struct spotData, LocalDateTime sqlDate, String metal) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", sqlDate);
        params.put("metal", metal);
        params.put("ask", protoUtils.getFieldValue(spotData, "ask"));
        params.put("mid", protoUtils.getFieldValue(spotData, "mid"));
        params.put("bid", protoUtils.getFieldValue(spotData, "bid"));
        params.put("value", protoUtils.getFieldValue(spotData, "value"));
        params.put("performance", protoUtils.getFieldValue(spotData, "performance"));
        params.put("weightUnit", spotData.getFieldsMap().get("weight_unit").getStringValue());
        return params;
    }

    public SpotItems fetchCurrentPrices(String metal) {
        String query = SQLQueryConstants.GET_CURRENT_SPOT_PRICES;
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);
        List<SpotItems> items = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> SpotItems.newBuilder()
                .setDate(protoUtils.sqlDateToGoogleTimestamp(rs.getDate("date")))
                .setMetal(rs.getString("metal"))
                .setWeightUnit(rs.getString("weight_unit"))
                .setAsk(rs.getDouble("ask"))
                .setBid(rs.getDouble("bid"))
                .setMid(rs.getDouble("mid"))
                .setValue(rs.getDouble("value"))
                .setPerformance(rs.getDouble("performance"))
                .build());
        if (!items.isEmpty()) {
            return items.getFirst();
        }
        return null;
    }
}
