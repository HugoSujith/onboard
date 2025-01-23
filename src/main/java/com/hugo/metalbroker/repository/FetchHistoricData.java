package com.hugo.metalbroker.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.utils.ProtoUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchHistoricData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger LOGGER = Logger.getLogger(FetchHistoricData.class.getName());
    private int checker = 0;
    private final ProtoUtils protoUtils;

    public FetchHistoricData(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ProtoUtils protoUtils) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.protoUtils = protoUtils;
    }

    @Scheduled(fixedRate = 10000)
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

    public boolean updateData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");

            if (embeddedItems != null && !embeddedItems.isEmpty()) {
                JsonNode historicDataJson = embeddedItems.get(embeddedItems.size() - 1);

                try {
                    Struct historicData = protoUtils.parseJsonToProto(historicDataJson);

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

    public boolean storeData(String url) {
        String metal = url.equals(Dotenv.load().get("SILVER_HISTORIC_URL")) ? "silver" : "gold";
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        int value = -1000;
        if (response != null) {
            ArrayNode embeddedItems = (ArrayNode) response.get("_embedded").get("items");
            for (JsonNode historicDataJson : embeddedItems) {
                try {
                    Struct historicData = protoUtils.parseJsonToProto(historicDataJson);
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

    public HistoricItemsList getItems(String metal) {
        String query = "SELECT * FROM HISTORIC_ITEMS WHERE metal=:metal";
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);

        List<HistoricItems> data = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> HistoricItems.newBuilder()
                .setDate(protoUtils.sqlDateToGoogleTimestamp(rs.getDate("date")))
                .setMetal(rs.getString("metal"))
                .setWeightUnit(rs.getString("weight_unit"))
                .setHigh(rs.getDouble("high"))
                .setLow(rs.getDouble("low"))
                .setOpen(rs.getDouble("open"))
                .setClose(rs.getDouble("close"))
                .setMA50(rs.getDouble("MA50"))
                .setMA200(rs.getDouble("MA200"))
                .build());
        HistoricItemsList.Builder historicItemsListBuilder = HistoricItemsList.newBuilder();
        historicItemsListBuilder.addAllItems(data);
        return historicItemsListBuilder.build();
    }

    public int insertIntoDB(String metal, Struct historicData, Date sqlDate) {
        String insertQuery =
                "INSERT INTO historic_items (metal, date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:metal, :date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";

        Map<String, Object> params = buildParamsForData(historicData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(insertQuery, params);
    }

    public Map<String, Object> buildParamsForData(Struct historicData, Date sqlDate, String metal) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", sqlDate);
        params.put("metal", metal);
        params.put("ma200", protoUtils.getFieldValue(historicData, "MA200"));
        params.put("ma50", protoUtils.getFieldValue(historicData, "MA50"));
        params.put("close", protoUtils.getFieldValue(historicData, "close"));
        params.put("open", protoUtils.getFieldValue(historicData, "open"));
        params.put("high", protoUtils.getFieldValue(historicData, "high"));
        params.put("low", protoUtils.getFieldValue(historicData, "low"));
        params.put("weightUnit", historicData.getFieldsMap().get("weight_unit").getStringValue());
        return params;
    }
}
