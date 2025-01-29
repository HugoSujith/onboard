package com.hugo.metalbroker.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hugo.metalbroker.exceptions.RegistrationFailureException;
import com.hugo.metalbroker.exceptions.UserNotFoundException;
import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean findIfUserPresent(String username) {
        String query = SQLQueryConstants.FIND_COUNT_OF_USERS_BY_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            throw new UserNotFoundException(username);
        }
    }

    public UserDTO getUserByUsername(String username) {
        String query = SQLQueryConstants.GET_ALL_USERS_BY_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        try {
            List<UserDTO> users = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> UserDTO.newBuilder()
                    .setUsername(rs.getString("username"))
                    .setPassword(rs.getString("password"))
                    .setFirstname(rs.getString("firstname"))
                    .setLastname(rs.getString("lastname"))
                    .setBalance(rs.getInt("balance"))
                    .build());

            if (!users.isEmpty()) {
                return users.getFirst();
            } else {
                throw new UserNotFoundException(username);
            }
        } catch (Exception e) {
            throw new UserNotFoundException(username);
        }
    }

    public boolean addUsersToDB(UserDTO user) {
        if (!findIfUserPresent(user.getUsername())) {
            String query = SQLQueryConstants.INSERT_INTO_USER;
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            params.put("password", user.getPassword());
            params.put("first_name", user.getFirstname());
            params.put("last_name", user.getLastname());
            params.put("balance", user.getBalance());

            try {
                int rowsAffected = namedParameterJdbcTemplate.update(query, params);
                return rowsAffected > 0;
            } catch (Exception e) {
                throw new RegistrationFailureException(user.getUsername());
            }
        }
        return false;
    }

    public double getBalance(String username) {
        if (findIfUserPresent(username)) {
            String query = SQLQueryConstants.GET_USER_BALANCE_BY_USERNAME;
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            return namedParameterJdbcTemplate.queryForObject(query, params, Double.class);
        }
        return -1;
    }

    public boolean updateBalance(BalanceDTO updatedBalance) {
        if (findIfUserPresent(updatedBalance.getUsername())) {
            String query = SQLQueryConstants.UPDATE_USER_BALANCE_BY_USERNAME;
            Map<String, Object> params = new HashMap<>();
            params.put("balance", updatedBalance.getBalance());
            params.put("username", updatedBalance.getUsername());
            int count = namedParameterJdbcTemplate.update(query, params);
            return count > 0;
        }
        return false;
    }

    public String findUserCurrencyCode(String walletId) {
        if (!findIfUserPresent(walletId)) {
            String query = SQLQueryConstants.GET_USER_CURRENCY_CODE_BY_USERNAME;
            Map<String, Object> params = new HashMap<>();
            params.put("walletId", walletId);
            return namedParameterJdbcTemplate.queryForObject(query, params, String.class);
        }
        throw new UserNotFoundException(walletId);
    }
}
