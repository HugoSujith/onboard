package com.hugo.onboard.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import org.springframework.scheduling.annotation.Scheduled;

public interface FetchHistoricData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);

    default int insertIntoDB(String metal, Struct historicData, Date sqlDate) {
        String insertQuery =
                "INSERT INTO historic_items (metal, date, open, close, high, low, ma50, ma200, weight_unit) VALUES (:metal, :date, :open, :close, :high, :low, :ma50, :ma200, :weightUnit)";

        Map<String, Object> params = buildParamsForData(historicData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(insertQuery, params);
    }

    default Struct parseJsonToProto(JsonNode historicDataJson) throws Exception {
        String jsonToStr = historicDataJson.toString();
        Struct.Builder historicDataBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, historicDataBuilder);
        return historicDataBuilder.build();
    }

    default Map<String, Object> buildParamsForData(Struct historicData, Date sqlDate, String metal) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", sqlDate);
        params.put("metal", metal);
        params.put("ma200", getFieldValue(historicData, "MA200"));
        params.put("ma50", getFieldValue(historicData, "MA50"));
        params.put("close", getFieldValue(historicData, "close"));
        params.put("open", getFieldValue(historicData, "open"));
        params.put("high", getFieldValue(historicData, "high"));
        params.put("low", getFieldValue(historicData, "low"));
        params.put("weightUnit", historicData.getFieldsMap().get("weight_unit").getStringValue());
        return params;
    }

    default double getFieldValue(Struct historicData, String fieldName) {
        if (historicData.getFieldsMap().get(fieldName) != null) {
            return historicData.getFieldsMap().get(fieldName).getNumberValue();
        }
        return -1000;
    }
}
