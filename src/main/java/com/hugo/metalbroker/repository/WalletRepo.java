package com.hugo.metalbroker.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.metalbroker.exceptions.WalletIdFetchingFailureException;
import com.hugo.metalbroker.exceptions.WalletIdPresenceFailureException;
import com.hugo.metalbroker.exceptions.WalletIdVerificationException;
import com.hugo.metalbroker.model.user.WalletDTO;
import com.hugo.metalbroker.utils.UIDGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepo {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UIDGenerator uidGenerator;
    private final Logger log;

    public WalletRepo(NamedParameterJdbcTemplate jdbcTemplate, UIDGenerator uidGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.uidGenerator = uidGenerator;
        log = Logger.getLogger(this.getClass().getName());
    }

    public boolean createWallet(String username) {
        int rowsAffected = 0;
        if (!this.findIfUserHasWallet(username)) {
            String query = SQLQueryConstants.INSERT_INTO_WALLET;
            Map<String, Object> params = new HashMap<>();
            params.put("wallet_id", uidGenerator.generateUID(30));
            params.put("user_id", username);
            params.put("status", "ACTIVE");
            params.put("currency_code", "INR");
            rowsAffected = jdbcTemplate.update(query, params);
            log.info("A wallet has been created after verifying the presence");
        }

        return rowsAffected > 0;
    }

    public boolean findIfUserHasWallet(String username) {
        int count = 0;
        try {
            String query = SQLQueryConstants.FIND_COUNT_OF_WALLETS_BY_USERNAME;
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            count = jdbcTemplate.queryForObject(query, params, Integer.class);
        } catch (DataAccessException e) {
            throw new WalletIdPresenceFailureException(username);
        }

        log.info("Checked if the user has a wallet.");

        return count > 0;
    }

    public String getWalletIdByUsername(String username) {
        List<WalletDTO> userWallet = null;
        try {
            String query = SQLQueryConstants.GET_ALL_WALLETS_FROM_USERNAME;
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", username);
            userWallet = jdbcTemplate.query(query, params, (rs, rowNum) -> {
                WalletDTO.Builder builder = WalletDTO.newBuilder();
                builder.setWalletId(rs.getString("wallet_id"));
                builder.setUserId(rs.getString("user_id"));
                builder.setStatus(WalletDTO.Status.valueOf(rs.getString("status")));
                return builder.build();
            });
        } catch (Exception e) {
            throw new WalletIdFetchingFailureException(username);
        }
        log.info("Fetched the wallet id from database using the username.");
        return userWallet.getFirst().getWalletId();
    }
}
