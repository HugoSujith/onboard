package com.hugo.metalbroker.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Struct;
import org.springframework.stereotype.Component;

@Component
public class DataFetchUtils {
    private final ProtoUtils protoUtils;

    public DataFetchUtils(ProtoUtils protoUtils) {
        this.protoUtils = protoUtils;
    }

    public Map<String, Object> buildParamsForSpotData(Struct spotData, Timestamp sqlDate, String metal) {
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

    public Map<String, Object> buildParamsForHistoricData(Struct historicData, Date sqlDate, String metal) {
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
