package com.hugo.metalbroker.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.hugo.metalbroker.exceptions.RegistrationFailureException;
import com.hugo.metalbroker.exceptions.UserBalanceFetchingFailure;
import com.hugo.metalbroker.exceptions.UserBalanceUpdateFailure;
import com.hugo.metalbroker.exceptions.UserCurrencyCodeFetchFailureException;
import com.hugo.metalbroker.exceptions.UserNotFoundException;
import com.hugo.metalbroker.model.user.BalanceDTO;
import com.hugo.metalbroker.model.user.UserDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Logger log;

    public UserRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        log = Logger.getLogger(this.getClass().getName());
    }

    public boolean findIfUserPresent(String username) {
        String query = SQLQueryConstants.FIND_COUNT_OF_USERS_BY_USERNAME;
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        try {
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            log.info("The user presence is been verified");
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

            log.info("Fetched the user from the database by username.");

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
                log.info("The user has been successfully added into the table");
                return rowsAffected > 0;
            } catch (Exception e) {
                throw new RegistrationFailureException(user.getUsername());
            }
        }
        return false;
    }

    public double getBalance(String username) {
        if (findIfUserPresent(username)) {
            double result = 0;
            try {
                String query = SQLQueryConstants.GET_USER_BALANCE_BY_USERNAME;
                Map<String, Object> params = new HashMap<>();
                params.put("username", username);
                result = namedParameterJdbcTemplate.queryForObject(query, params, Double.class);
            } catch (DataAccessException e) {
                throw new UserBalanceFetchingFailure(username);
            }
            log.info("The user balance has been successfully fetched from the table");
            return result;
        }
        return -1;
    }

    public boolean updateBalance(BalanceDTO updatedBalance) {
        if (findIfUserPresent(updatedBalance.getUsername())) {
            int count = 0;
            try {
                String query = SQLQueryConstants.UPDATE_USER_BALANCE_BY_USERNAME;
                Map<String, Object> params = new HashMap<>();
                params.put("balance", updatedBalance.getBalance());
                params.put("username", updatedBalance.getUsername());
                count = namedParameterJdbcTemplate.update(query, params);
            } catch (Exception e) {
                throw new UserBalanceUpdateFailure(updatedBalance.getUsername());
            }
            log.info("The user balance has been successfully updated in the database");
            return count > 0;
        }
        return false;
    }

    public String findUserCurrencyCode(String walletId) {
        if (!findIfUserPresent(walletId)) {
            try {
                String query = SQLQueryConstants.GET_USER_CURRENCY_CODE_BY_USERNAME;
                Map<String, Object> params = new HashMap<>();
                params.put("walletId", walletId);
                log.info("The user's currency code has been successfully fetched from the database.");
                return namedParameterJdbcTemplate.queryForObject(query, params, String.class);
            } catch (Exception e) {
                throw new UserCurrencyCodeFetchFailureException(walletId);
            }
        }
        throw new UserNotFoundException(walletId);
    }
}
