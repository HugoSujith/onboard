package com.hugo.metalbroker.repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Struct;
import com.hugo.metalbroker.exceptions.DataFetchFailureException;
import com.hugo.metalbroker.model.datavalues.spot.SpotItems;
import com.hugo.metalbroker.model.datavalues.spot.SpotItemsList;
import com.hugo.metalbroker.utils.DataFetchUtils;
import com.hugo.metalbroker.utils.ProtoUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpotDataRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ProtoUtils protoUtils;
    private final DataFetchUtils dataUtils;

    public SpotDataRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ProtoUtils protoUtils, DataFetchUtils dataUtils) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.protoUtils = protoUtils;
        this.dataUtils = dataUtils;
    }

    public SpotItemsList getItems(String metal) {
        String query = SQLQueryConstants.GET_ALL_FROM_SPOT_ITEMS_BY_METAL;
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);

        List<SpotItems> data = null;

        try {
            data = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> SpotItems.newBuilder()
                    .setDate(protoUtils.localDateTimeToGoogleTimestamp(((Timestamp) rs.getObject("date")).toInstant()))
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

    public int insertIntoDB(String metal, Struct spotData, Timestamp sqlDate) {
        String query = SQLQueryConstants.INSERT_INTO_SPOT_ITEMS;

        Map<String, Object> params = dataUtils.buildParamsForSpotData(spotData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(query, params);
    }

    public boolean checkIfDataPresent(Timestamp date, String metal) {
        String checkQuery = SQLQueryConstants.FIND_COUNT_OF_SPOT_ITEMS_BY_PK;
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("date", date);
        checkParams.put("metal", metal);

        int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);
        return count > 0;
    }

    public SpotItems fetchCurrentPrices(String metal) {
        List<SpotItems> items = this.getItems(metal).getItemsList();
        if (!items.isEmpty()) {
            return items.getFirst();
        }
        return null;
    }
}
