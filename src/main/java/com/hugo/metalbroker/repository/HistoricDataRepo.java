package com.hugo.metalbroker.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Struct;
import com.hugo.metalbroker.exceptions.DataFetchFailureException;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItems;
import com.hugo.metalbroker.model.datavalues.historic.HistoricItemsList;
import com.hugo.metalbroker.utils.DataFetchUtils;
import com.hugo.metalbroker.utils.ProtoUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HistoricDataRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ProtoUtils protoUtils;
    private final DataFetchUtils dataUtils;

    public HistoricDataRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ProtoUtils protoUtils, DataFetchUtils dataUtils) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.protoUtils = protoUtils;
        this.dataUtils = dataUtils;
    }

    public HistoricItemsList getItems(String metal) {
        String query = SQLQueryConstants.GET_ALL_FROM_HISTORIC_DATA_BY_METAL;
        Map<String, Object> params = new HashMap<>();
        params.put("metal", metal);

        List<HistoricItems> data = null;
        try {
            data = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> HistoricItems.newBuilder()
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
        } catch (Exception e) {
            throw new DataFetchFailureException(Thread.currentThread().getStackTrace()[1].getMethodName());
        }

        HistoricItemsList.Builder historicItemsListBuilder = HistoricItemsList.newBuilder();
        historicItemsListBuilder.addAllItems(data);
        return historicItemsListBuilder.build();
    }

    public int insertIntoDB(String metal, Struct historicData, Date sqlDate) {
        String insertQuery = SQLQueryConstants.INSERT_INTO_HISTORIC_ITEMS;
        Map<String, Object> params = dataUtils.buildParamsForHistoricData(historicData, sqlDate, metal);

        return namedParameterJdbcTemplate.update(insertQuery, params);
    }

    public boolean checkIfDataPresent(LocalDate date, String metal) {
        Date sqlDateForCheck = Date.valueOf(date);

        String checkQuery = SQLQueryConstants.FIND_COUNT_OF_HISTORIC_ITEMS_BY_PK;

        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("date", sqlDateForCheck);
        checkParams.put("metal", metal);

        int count = namedParameterJdbcTemplate.queryForObject(checkQuery, checkParams, Integer.class);
        return count > 0;
    }
}