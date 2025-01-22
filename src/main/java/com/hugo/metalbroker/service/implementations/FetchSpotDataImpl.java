package com.hugo.metalbroker.service.implementations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.service.FetchSpotData;
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

    @Override
    public List<SpotItems> getItems(String metal) {
        String query = "SELECT * FROM SPOT_ITEMS WHERE metal=:metal";
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);

        return namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> SpotItems.newBuilder()
                .setDate(localDateTimeToGoogleTimestamp((LocalDateTime) rs.getObject("date")))
                .setMetal(rs.getString("metal"))
                .setWeightUnit(rs.getString("weight_unit"))
                .setAsk(rs.getDouble("ask"))
                .setBid(rs.getDouble("bid"))
                .setMid(rs.getDouble("mid"))
                .setValue(rs.getDouble("value"))
                .setPerformance(rs.getDouble("performance"))
                .build());
    }

    public Timestamp localDateTimeToGoogleTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public int insertIntoDB(String metal, Struct spotData, LocalDateTime sqlDate) {
        String query =
                "INSERT INTO spot_items (metal, date, ask, mid, bid, value, performance, weight_unit) VALUES (:metal, :date, :ask, :mid, :bid, :value, :performance, :weightUnit)";

        Map<String, Object> params = buildParamsForData(spotData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(query, params);
    }

    public Struct parseJsonToProto(JsonNode spotDataJson) throws Exception {
        String jsonToStr = spotDataJson.toString();
        Struct.Builder spotDataBuilder = Struct.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonToStr, spotDataBuilder);
        return spotDataBuilder.build();
    }

    public Map<String, Object> buildParamsForData(Struct spotData, LocalDateTime sqlDate, String metal) {
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

    public double getFieldValue(Struct historicData, String fieldName) {
        if (historicData.getFieldsMap().get(fieldName) != null) {
            return historicData.getFieldsMap().get(fieldName).getNumberValue();
        }
        return -1000;
    }

}
