package com.hugo.onboard.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import org.springframework.scheduling.annotation.Scheduled;

public interface FetchSpotData {
    @Scheduled(fixedRate = 10000)
    boolean data();

    boolean updateData(String url);

    boolean storeData(String url);

    default int insertIntoDB(String metal, Struct spotData, LocalDateTime sqlDate) {
        String query =
                "INSERT INTO spot_items (metal, date, ask, mid, bid, value, performance, weight_unit) VALUES (:metal, :date, :ask, :mid, :bid, :value, :performance, :weightUnit)";

        Map<String, Object> params = buildParamsForData(spotData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(query, params);
    }

    default Struct parseJsonToProto(JsonNode spotDataJson) throws Exception {
        String jsonToStr = spotDataJson.toString();
        Struct.Builder spotDataBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, spotDataBuilder);
        return spotDataBuilder.build();
    }

    default Map<String, Object> buildParamsForData(Struct spotData, LocalDateTime sqlDate, String metal) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", sqlDate);
        params.put("metal", metal);
        params.put("ask", getFieldValue(spotData, "ask"));
        params.put("mid", getFieldValue(spotData, "mid"));
        params.put("bid", getFieldValue(spotData, "bid"));
        params.put("value", getFieldValue(spotData, "value"));
        params.put("performance", getFieldValue(spotData, "performance"));
        params.put("weightUnit", spotData.getFieldsMap().get("weight_unit").getStringValue());
        return params;
    }

    default double getFieldValue(Struct historicData, String fieldName) {
        if (historicData.getFieldsMap().get(fieldName) != null) {
            return historicData.getFieldsMap().get(fieldName).getNumberValue();
        }
        return -1000;
    }
}
