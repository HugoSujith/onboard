package com.hugo.metalbroker.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hugo.metalbroker.model.user.WalletDTO;
import com.hugo.metalbroker.utils.UIDGenerator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepo {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UIDGenerator uidGenerator;

    public WalletRepo(NamedParameterJdbcTemplate jdbcTemplate, UIDGenerator uidGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.uidGenerator = uidGenerator;
    }

    public boolean createWallet(String username) {
        int rowsAffected = 0;
        if (!this.findIfUserHasWallet(username)) {
            String query = SQLQueryConstants.INSERT_INTO_WALLET;
            Map<String, Object> params = new HashMap<>();
            params.put("wallet_id", uidGenerator.generateUID(30));
            params.put("user_id", username);
            params.put("status", "ACTIVE");
            rowsAffected = jdbcTemplate.update(query, params);
        }

        return rowsAffected > 0;
    }

    public boolean findIfUserHasWallet(String username) {
        String query = SQLQueryConstants.FIND_COUNT_OF_WALLETS_BY_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        int count = jdbcTemplate.queryForObject(query, params, Integer.class);

        return count > 0;
    }

    public String getWalletIdByUsername(String username) {
        String query = SQLQueryConstants.GET_ALL_WALLETS_FROM_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", username);
        List<WalletDTO> userWallet = jdbcTemplate.query(query, params, (rs, rowNum) -> {
            WalletDTO.Builder builder = WalletDTO.newBuilder();
            builder.setWalletId(rs.getString("wallet_id"));
            builder.setUserId(rs.getString("user_id"));
            builder.setStatus(WalletDTO.Status.valueOf(rs.getString("status")));
            return builder.build();
        });
        return userWallet.getFirst().getWalletId();
    }
}
