package com.hugo.metalbroker.repository;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.metalbroker.exceptions.AssetQuantityFetchException;
import com.hugo.metalbroker.exceptions.UserWalletWeightUpdateFailureException;
import com.hugo.metalbroker.exceptions.WalletIdVerificationException;
import com.hugo.metalbroker.model.user.AssetIdDTO;
import com.hugo.metalbroker.model.user.UpdateAssetDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssetRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Logger log;

    public AssetRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        log = Logger.getLogger(this.getClass().getName());
    }

    public boolean updateQuantity(UpdateAssetDTO assetDTO) {
        try {
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
                log.info("The asset data has been updated in the database");
                return count > 0;
            }
        } catch (Exception e) {
            throw new UserWalletWeightUpdateFailureException(assetDTO.getWalletId());
        }
        return false;
    }

    public boolean checkWalletIdPresent(Map<String, Object> params) {
        try {
            String query = SQLQueryConstants.FIND_COUNT_OF_USER_WALLET_INFO_BY_WALLET_ID_AND_METAL;
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new WalletIdVerificationException((String) params.get("wallet_id"));
        }
    }

    public double getAssetQuantity(AssetIdDTO assetDTO) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("wallet_id", assetDTO.getWalletId());
            params.put("metal", assetDTO.getMetal());
            if (!checkWalletIdPresent(params)) {
                String query = SQLQueryConstants.CREATE_WALLET_INFO_OF_USER;
                params.put("grams", (double) 0);
                namedParameterJdbcTemplate.update(query, params);
                log.info("A new wallet has been created for the user" + Base64.getEncoder().encodeToString(assetDTO.getWalletId().getBytes(StandardCharsets.UTF_8)));
                return 0;
            }
            String query = SQLQueryConstants.GET_ASSET_QUANTITY_BY_WALLET_ID;
            double value = namedParameterJdbcTemplate.queryForObject(query, params, Double.class);
            log.info("Fetched asset quantity from the database using wallet id and metal name.");
            return value;
        } catch (Exception e) {
            throw new AssetQuantityFetchException(assetDTO.getWalletId());
        }
    }
}
