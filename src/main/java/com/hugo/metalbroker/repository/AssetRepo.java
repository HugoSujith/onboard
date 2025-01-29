package com.hugo.metalbroker.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hugo.metalbroker.model.user.AssetIdDTO;
import com.hugo.metalbroker.model.user.UpdateAssetDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssetRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AssetRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean updateQuantity(UpdateAssetDTO assetDTO) {
        String walletStatusQuery = SQLQueryConstants.GET_WALLET_STATUS;
        Map<String, Object> params = new HashMap<>();
        params.put("wallet_id", assetDTO.getWalletId());
        String walletStatus = namedParameterJdbcTemplate.queryForObject(walletStatusQuery, params, String.class);
        if (walletStatus != null && walletStatus.equals("ACTIVE")) {
            String updateQuantityQuery = SQLQueryConstants.UPDATE_WALLET_DETAILS_BY_WALLET_ID;
            params.clear();
            params.put("wallet_id", assetDTO.getWalletId());
            params.put("metal", assetDTO.getMetal());
            params.put("grams", assetDTO.getGrams());
            int count = namedParameterJdbcTemplate.update(updateQuantityQuery, params);
            return count > 0;
        }
        return false;
    }

    public boolean checkWalletPresent(Map<String, Object> params) {
        String query = SQLQueryConstants.FIND_COUNT_OF_USER_WALLET_INFO_BY_WALLET_ID_AND_METAL;
        Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
        return count != null && count > 0;
    }

    public double getAssetQuantity(AssetIdDTO assetDTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("wallet_id", assetDTO.getWalletId());
        params.put("metal", assetDTO.getMetal());
        if (!checkWalletPresent(params)) {
            String query = SQLQueryConstants.CREATE_WALLET_INFO_OF_USER;
            params.put("grams", (double) 0);
            namedParameterJdbcTemplate.update(query, params);
            return 0;
        }
        String query = SQLQueryConstants.GET_ASSET_QUANTITY_BY_WALLET_ID;
        return namedParameterJdbcTemplate.queryForObject(query, params, Double.class);
    }
}
